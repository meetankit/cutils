%{
  import java.io.IOException;
  import java.io.StringReader;
  import java.util.ArrayList;
  import java.util.List;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Dty;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Operator;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.Expression;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operands.Constant;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operands.Variable;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.And;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Or;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Not;  
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Lt;  
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Leq;  
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Eq;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Geq;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Gt;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Neq;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Plus;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Minus;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Mult;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Div;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Mod;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Null;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Between;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.In;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Field;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.Index;
  import com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.types.operators.FunctionWrapper;

@SuppressWarnings("unchecked")
%}
/* literals */
%token <obj> INT
%token <obj> LONG
%token <obj> DOUBLE
%token <obj> QSTRING
%token <obj> BOOLEAN
%token <obj> DATE

/* operators */
%token IS
%token NULL
%token NOT
%token DOT
%token BETWEEN
%token IN
%token LT
%token LE
%token EQ
%token GE
%token GT
%token NE
%left OR, PLUS, MINUS
%left AND, MULT, DIV, MOD
%right NEG

/* punctuation */
%token LPAREN
%token RPAREN
%token LBRACE
%token RBRACE
%token LBRACKET
%token RBRACKET
%token COMMA

/* placeholders */
%token <obj> ID

/* non-terminals */
%type <obj> expr
%type <obj> aexpr
%type <obj> aterm
%type <obj> afactor
%type <obj> field
%type <obj> addop
%type <obj> multop
%type <obj> bexpr
%type <obj> bterm
%type <obj> bfactor
%type <obj> orop
%type <obj> andop
%type <obj> exprlist
%type <obj> relop
%type <obj> number

%%
expr: bexpr
;

bexpr: bexpr orop bterm
{$$ = addArgs($2, $1, $3);}
| bterm
;

orop: OR
{$$ = new Or();}
;

bterm: bterm andop bfactor
{$$ = addArgs($2, $1, $3);}
| bfactor
;

andop: AND
{$$ = new And();}
;

/*
 * Treating aexpr as bfactor causes a shift reduce conflict on RPAREN,
 * which is resolved to a shift by default which is okay because the
 * resulting aexpr can eventually be treated as a bfactor if needed.
 *
 * It also allows aexpr to become top-level expression, without causing
 * many other conflicts.
 */
bfactor: aexpr relop aexpr
{$$ = addArgs($2, $1, $3);}
| aexpr
| aexpr IS NULL
{$$ = addArgs(new Null(), $1);}
| aexpr IS NOT NULL
{$$ = addArgs(new Not(), addArgs(new Null(), $1));}
| aexpr BETWEEN aexpr AND aexpr
{$$ = addArgs(new Between(), $1, $3, $5);}
| aexpr IN LBRACE exprlist RBRACE
{$$ = newIn($1, $4);}
| aexpr NOT IN LBRACE exprlist RBRACE
{$$ = addArgs(new Not(), newIn($1, $5));}
| LPAREN bexpr RPAREN
{$$ = $2;}
| NEG bfactor
{$$ = addArgs(new Not(), $2);}
| BOOLEAN
;

relop: LT
{$$ = new Lt();}
| LE
{$$ = new Leq();}
| EQ
{$$ = new Eq();}
| GE
{$$ = new Geq();}
| GT
{$$ = new Gt();}
| NE
{$$ = new Neq();}
;

aexpr: aexpr addop aterm
{$$ = addArgs($2, $1, $3);}
| aterm
;

addop: PLUS
{$$ = new Plus();}
| MINUS
{$$ = new Minus();}
;

aterm: aterm multop afactor
{$$ = addArgs($2, $1, $3);}
| afactor
;

multop: MULT
{$$ = new Mult();}
| DIV
{$$ = new Div();}
| MOD
{$$ = new Mod();}
;

afactor: field
| number
| QSTRING
| DATE
| ID LPAREN exprlist RPAREN
{$$ = newFuncWrapper($1, $3);}
| LPAREN aexpr RPAREN
{$$ = $2;}
;

field: field DOT ID
{$$ = addArgs(new Field(),
	      $1,
	      new Constant(((Variable)$3).getName(), Dty.STRING, ((Variable)$3).getName()));
}
| field LBRACKET aexpr RBRACKET
{$$ = addArgs(new Index(), $1, $3);}
| ID
;

exprlist: exprlist COMMA expr
{$$ = addExpr($1, $3);}
| expr
{$$ = newExprList($1);}
;

number: INT
| LONG
| DOUBLE
;

%%
/* a reference to the lexer object */
private ExprLexer lexer;

/* a reference to the string being parsed */
private String inputString;

/* interface to the lexer */
private int yylex () {
  int yyl_return = -1;
  try {
    yyl_return = lexer.yylex();
  }
  catch (IOException e) {
    throw new CuRuntimeException(CUMessages.CU_ERROR_PARSING_EXPRESSION_expr,
				 e, inputString);
  }
  return yyl_return;
}

void yyerror(String s)
{
  System.out.println("parse error: " + s);
}

public ExprParser(String input, boolean debug)
{
  yydebug = debug;
  inputString = input;
  lexer = new ExprLexer(new StringReader(input), this);
}

/*****************************************************************************/
/*                             Parse tree utils                              */
/*****************************************************************************/
private List<Expression> newExprList(Object e)
{
  List<Expression> exprList = new ArrayList<Expression>();
  exprList.add((Expression)e);
  return exprList;
}

private List<Expression> addExpr(Object list, Object e)
{
  List<Expression> exprList = (List<Expression>)list;
  exprList.add((Expression)e);
  return exprList;
}

private FunctionWrapper newFuncWrapper(Object fname, Object args)
{
  String name = ((Variable)fname).getName();
  List<Expression> exprList = (List<Expression>)args;
  return new FunctionWrapper(name, exprList);
}

private Expression addArgs(Object op, Object...args)
{
  for (Object arg : args)
  {
    ((Operator)op).addArg((Expression)arg);
  }
  return (Expression)op;
}

private In newIn(Object expr, Object list)
{
  In inExpr = new In();
  addArgs(inExpr, expr);
  if (list != null)
  {
    List<Expression> exprList = (List<Expression>)list;
    for (Expression e : exprList)
      inExpr.addArg((Expression)e);
  }
  return inExpr;
}
