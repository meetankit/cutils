package com.adobe.dx.aep.skaluskar.poc.cutils.basics.expressions.parser;

import Dty;
import Value;
import Expression;
import Constant;
import Variable;

@SuppressWarnings("unused")
%%
%class ExprLexer
%unicode
%byaccj

%{
/* store a reference to the parser object */
private ExprParser parser;

/* constructor taking an additional parser object */
public ExprLexer(java.io.Reader r, ExprParser yyparser) {
  this(r);
  this.parser = yyparser;
}

private void createNode(Expression e)
{
  parser.yylval = ParserUtils.newLexerNode(e);
}

%}

/* white space */
LineTerminator = \r|\n|\r\n
whitespace     = {LineTerminator} | [ \t\f]

/* comment */
comment  = "/*" ([^\*\/] | "*" [^\/] | "/" [^\*])* "*/"

/* numeric literals */
sign           = [-+]
digit          = [0-9]
digits         = {digit}+
integer        = {sign}? {digits}
long           = {integer} [lL]?
fpnumber       = {sign}? {digits}? ("." {digits})
exponent       = [eE] {sign}? {integer}
double         = {fpnumber} {exponent}?

/* string literals */
letter         = [a-zA-Z]
qstring        = "'"
                    (   [^'\\\n\t\r]
                      | (\\ [ntr\\'])
                      | (\\ [u|U] ({letter}|{digit}){4})
                    )*
                 "'"

/* boolean literals */
true           = [tT][rR][uU][eE]
false          = [fF][aA][lL][sS][eE]

/* date literal - UTC */
digits4        = [0-9]{4}
digits2        = [0-9]{2}
digits12       = [0-9]{1,2}
date           = {digits4} "-" {digits12} "-" {digits12}
time           = "T" {digits12} ":" {digits12} ":" {digits2}
timezone       = "Z" | (("+" | "-") {digits12} ":" {digits2})
utcDay         = {date}
utcDayTime     = {date} {time}
utcFull        = {date} {time} {timezone}

/* arithmetic operators */
plus           = "+"
minus          = "-"
mult           = "*"
div            = "/"
mod            = "%"
dot            = "."

/* relational operators */
lt             = "<"
le             = "<="
eq             = "=="
ge             = ">="
gt             = ">"
ne             = "!="
is             = [iI][sS]
null           = [nN][uU][lL][lL]
not            = [nN][oO][tT]
between        = [bB][eE][tT][wW][eE][eE][nN]
in             = [iI][nN]

/* boolean operators */
and            = [aA][nN][dD]
or             = [oO][rR]
neg            = "!"

/* punctuation */
lparen         = "("
rparen         = ")"
lbrace         = "{"
rbrace         = "}"
lbracket       = "["
rbracket       = "]"
comma          = ","

/* placeholders */
identifier     = [:jletter:] [:jletterdigit:]*

%%
{whitespace} {}
{comment}    {}
{integer}    {createNode(new Constant(yytext(), Dty.INTEGER,
                                      Integer.parseInt(yytext())));
              return ExprParser.INT;}
{long}       {createNode(new Constant(yytext(), Dty.LONG,
                                      Long.parseLong(yytext().replaceAll("l|L",""))));
              return ExprParser.LONG;}
{double}     {createNode(new Constant(yytext(), Dty.DOUBLE,
                                      Double.parseDouble(yytext())));
              return ExprParser.DOUBLE;}
{qstring}    {/* capture chars excluding the quotes as string value */
              createNode(new Constant(yytext(), Dty.STRING,
                                      yytext().substring(1, yylength() - 1)));
              return ExprParser.QSTRING;}
{true}       {createNode(new Constant(yytext(), Dty.BOOLEAN,
                                      Boolean.TRUE));
              return ExprParser.BOOLEAN;}
{false}      {createNode(new Constant(yytext(), Dty.BOOLEAN,
                                      Boolean.FALSE));
              return ExprParser.BOOLEAN;}
{utcDay}     {createNode(new Constant(yytext(), Dty.DATE,
                                      Value.parseDate(yytext(), Value.UTC_DATE_DAY)));
              return ExprParser.DATE;}
{utcDayTime} {createNode(new Constant(yytext(), Dty.DATE,
                                      Value.parseDate(yytext(), Value.UTC_DATE_DAY_TIME)));
              return ExprParser.DATE;}
{utcFull}    {createNode(new Constant(yytext(), Dty.DATE,
                                      Value.parseDate(yytext(), Value.UTC_DATE_FULL)));
              return ExprParser.DATE;}
{plus}       {return ExprParser.PLUS;}
{minus}      {return ExprParser.MINUS;}
{mult}       {return ExprParser.MULT;}
{div}        {return ExprParser.DIV;}
{mod}        {return ExprParser.MOD;}
{dot}        {return ExprParser.DOT;}
{lt}         {return ExprParser.LT;}
{le}         {return ExprParser.LE;}
{eq}         {return ExprParser.EQ;}
{ge}         {return ExprParser.GE;}
{gt}         {return ExprParser.GT;}
{ne}         {return ExprParser.NE;}
{is}         {return ExprParser.IS;}
{null}       {return ExprParser.NULL;}
{not}        {return ExprParser.NOT;}
{between}    {return ExprParser.BETWEEN;}
{in}         {return ExprParser.IN;}
{and}        {return ExprParser.AND;}
{or}         {return ExprParser.OR;}
{neg}        {return ExprParser.NEG;}
{lparen}     {return ExprParser.LPAREN;}
{rparen}     {return ExprParser.RPAREN;}
{lbrace}     {return ExprParser.LBRACE;}
{rbrace}     {return ExprParser.RBRACE;}
{lbracket}   {return ExprParser.LBRACKET;}
{rbracket}   {return ExprParser.RBRACKET;}
{comma}      {return ExprParser.COMMA;}
{identifier} {createNode(new Variable(yytext()));
              return ExprParser.ID;}
