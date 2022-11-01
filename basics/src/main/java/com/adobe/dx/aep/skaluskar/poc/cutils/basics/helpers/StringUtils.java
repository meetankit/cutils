/**
 * StringUtils.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers;

import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;

/**
 * @author admin
 *
 */
public class StringUtils
{
  public static final String STR_EMPTY          = "";

  public static final String STR_SPACE          = " ";

  public static final String STR_NEWLINE        = "\n";

  public static final String STR_TAB            = "\t";

  public static final String STR_PERIOD         = ".";

  public static final String STR_COMMA          = ",";

  public static final String STR_LPAREN         = "(";

  public static final String STR_RPAREN         = ")";

  public static final String STR_LBRACKET       = "[";

  public static final String STR_RBRACKET       = "]";

  public static final String STR_ZERO           = "0";

  public static final String STR_ONE            = "1";

  public static final String STR_NULL           = "NULL";

  public static final String STR_QUESTION_MARK  = "?";

  public static final String STR_STAR           = "*";

  public static final String STR_BACK_SLASH     = "\\";

  public static final char   CHAR_COMMA         = ',';

  public static final char   CHAR_NEWLINE       = '\n';

  public static final char   CHAR_DOUBLE_QUOTE  = '"';

  public static final char   CHAR_ESCAPE_CHAR   = '\\';

  public static final char   CHAR_QUESTION_MARK = '?';

  public static final char   CHAR_STAR          = '*';

  /**
   * Checks if the input is either null or an empty string (0 length).
   * 
   * @input s - input string to check
   * @return true / false
   */
  public static boolean isNullOrEmpty(String s)
  {
    return (s == null) || (s.length() == 0);
  }

  /**
   * Concatenates a list of strings together, with an optional delimiter in
   * between. Returns null if the list is null; empty string if the list is
   * empty.
   * 
   * @param list
   *          - list of strings to be concatenated
   * @param delim
   *          - delimiter (optional, can be null)
   * 
   * @return concatenated string
   */
  public static String concatenate(List<String> list, String delim)
  {
    String ret = null;

    if (list != null)
    {
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (String s : list)
      {
        if (!first && (delim != null))
          sb.append(delim);

        sb.append(s);
        first = false;
      }

      ret = sb.toString();
    }
    return ret;
  }

  public static String[] mergeStringArrays(String[] array1, String[] array2)
  {
    String ret[] = null;

    if (array1 == null)
      ret = array2;
    else if (array2 == null)
      ret = array1;
    else
    {
      Set<String> union = new HashSet<String>();
      union.addAll(Arrays.asList(array1));
      union.addAll(Arrays.asList(array2));
      ret = union.toArray(new String[0]);
    }
    return ret;
  }

  public static String[] fuzzyMergeStringArrays(String[] array1,
      String[] array2, int ngramSize, double fuzzyThresh)
  {
    String ret[] = null;

    if (array1 == null)
      ret = array2;
    else if (array2 == null)
      ret = array1;
    else
    {
      Set<String> union = new HashSet<String>();
      union.addAll(Arrays.asList(array1));
      if (ngramSize == 0)
        union.addAll(Arrays.asList(array2));
      else
      {
        for (String potentialAddition : array2)
        {
          for (String existing : array1)
          {
            double dist = DistanceUtils.minCaseInsensitiveJaccardEditDistance(
                ngramSize, potentialAddition, existing);
            if (dist <= fuzzyThresh)
            {
              /* only retain one of them - we will pick the longer one */
              if (potentialAddition.length() > existing.length())
              {
                union.remove(existing);
                union.add(potentialAddition);
              }
            }
            else
              union.add(potentialAddition);
          }
        }
      }
      ret = union.toArray(new String[0]);
    }
    return ret;
  }

  /**
   * Converts a clob to string
   * 
   * @param c
   *          - clob to be converted
   * @return converted string
   */
  public static String clobToString(Clob c)
  {
    String ret = null;
    if (c != null)
    {
      try
      {
        StringBuilder sb = new StringBuilder();
        Reader r = c.getCharacterStream();
        int ch;
        while ((ch = r.read()) != -1)
          sb.append((char) ch);
        ret = sb.toString();
      }
      catch (Exception e)
      {
        throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
      }
    }
    return ret;
  }

  /**
   * Converts a string to clob
   * 
   * @param c
   *          - JDBC connection
   * 
   * @param s
   *          - string to be converted
   * @return clob
   */
  public static Clob stringToClob(Connection c, String s) throws SQLException
  {
    Clob clob = c.createClob();
    if (!StringUtils.isNullOrEmpty(s))
      clob.setString(1, s);

    return clob;
  }

  /**
   * Removes any escape sequence in the input string and converts it into the
   * underlying char. For example, \n is converted into newline char, \t is
   * converted into a tab.
   * 
   * Throws exceptions if the input string represents more than 1 char after
   * conversion, or if the escape sequence isn't known.
   * 
   * @param s
   *          - input string
   * 
   * @return - converted char, 0 if input string is null or empty
   */
  public static char unescapeChar(String s)
  {
    char c = 0;

    if (!StringUtils.isNullOrEmpty(s))
    {
      char firstChar = s.charAt(0);
      if ((s.length() > 2) ||
          ((s.length() > 1) &&
              (firstChar != CHAR_ESCAPE_CHAR)))
        throw new CuRuntimeException(CUMessages.CU_STRING_str_TOO_LONG, s);

      if (s.length() == 1)
        c = firstChar;
      else
      {
        switch (s.charAt(1))
        {
        case 'n':
          c = '\n';
          break;
        case 't':
          c = '\t';
          break;
        case CHAR_ESCAPE_CHAR:
          c = CHAR_ESCAPE_CHAR;
          break;
        default:
          throw new CuRuntimeException(
              CUMessages.CU_UNKNOWN_ESCAPE_SEQUENCE_str,
              s);
        }
      }
    }

    return c;
  }

  private static boolean contains(char[] chars, char charToCheck)
  {
    boolean ret = false;

    if (chars != null)
    {
      for (char c : chars)
      {
        if (c == charToCheck)
        {
          ret = true;
          break;
        }
      }
    }

    return ret;
  }

  public static String escapeChars(String inputString, char[] charsToEscape)
  {
    StringBuilder sb = new StringBuilder();
    if (!StringUtils.isNullOrEmpty(inputString))
    {
      char inputChars[] = inputString.toCharArray();
      for (char inputChar : inputChars)
      {
        if ((inputChar == CHAR_ESCAPE_CHAR)
            || (inputChar == CHAR_DOUBLE_QUOTE)
            || contains(charsToEscape, inputChar))
          sb.append(CHAR_ESCAPE_CHAR);
        sb.append(inputChar);
      }
    }

    return sb.toString();
  }

  /**
   * Splits an input stream into fragments separated by a delimiter. Invokes a
   * processing function for each fragment as it is identified.
   * 
   * The delimiter can occur inside a fragment in which case it must either be
   * preceded by an escape character (CHAR_ESCAPE_CHAR) or be part of a
   * substring enclosed within double quotes. The escape character or double
   * quotes in a fragment should be escaped too.
   * 
   * Any escaped delimiter in the fragment is unescaped (escape char is
   * removed). Escaped double quotes and escapes are unescaped optionally.
   * 
   * @param reader
   *          - input stream
   * 
   * @param delimiter
   *          - delimiter character that separates fragments
   * 
   * @param unescapeAllChars
   *          - whether escape char (backslash) and double quotes should be
   *          unescaped in the fragment
   * 
   * @param processor
   *          - processing function that is invoked for each fragment
   */
  public static void splitter(Reader reader, char delimiter,
      boolean unescapeAllChars, Consumer<Fragment> processor)
  {
    splitter(reader, delimiter, unescapeAllChars, processor, 0,
        false, null);
  }

  /**
   * Splits an input stream into fragments separated by a delimiter. Invokes a
   * processing function for each fragment as it is identified.
   * 
   * The delimiter can occur inside a fragment in which case it must either be
   * preceded by an escape character (CHAR_ESCAPE_CHAR) or be part of a
   * substring enclosed within double quotes. The escape character or double
   * quotes in a fragment should be escaped too.
   * 
   * Any escaped delimiter in the fragment is unescaped (escape char is
   * removed). Escaped double quotes and escapes are unescaped optionally.
   * 
   * @param reader
   *          - input stream
   * 
   * @param delimiter
   *          - delimiter character that separates fragments
   * 
   * @param unescapeAllChars
   *          - whether escape char (backslash) and double quotes should be
   *          unescaped in the fragment
   * 
   * @param processor
   *          - processing function that is invoked for each fragment
   * 
   * @param maxFragments
   *          - maximum fragments to process
   * 
   * @param returnByteOffset
   *          - whether to return byte offset, else char offset is returned
   * 
   * @param cs
   *          - character set encoding of the chars, only needed when
   *          returnByteOffset is true
   *
   * @return number of chars / bytes read from the input stream
   */
  public static int splitter(Reader reader, char delimiter,
      boolean unescapeAllChars, Consumer<Fragment> processor,
      int maxFragments, boolean returnByteOffset, Charset cs)
  {
    int numRead = 0;
    try
    {
      StringBuilder recordBuffer = new StringBuilder();
      int nextChar;
      boolean inQuotes = false;
      boolean inEscape = false;
      int fragmentNo = 0;
      int currentFragmentOffset = 0;
      while ((nextChar = reader.read()) != -1)
      {
        if (returnByteOffset)
          numRead += String.valueOf((char) nextChar).getBytes(cs).length;
        else
          numRead++;

        if ((nextChar == delimiter) && !inQuotes && !inEscape)
        {
          /*
           * This is a delimiter without an escape and is not in double quotes.
           * We treat that as the end of the current fragment.
           */
          processor.accept(new Fragment(fragmentNo++, currentFragmentOffset,
              recordBuffer.toString()));

          /*
           * Clear state. Note that inQuotes and inEscape are both already
           * false.
           */
          recordBuffer = new StringBuilder();

          /* bump up fragment offset */
          currentFragmentOffset = numRead;

          /*
           * If there is a limit on number of fragments to process and it is
           * reached then stop.
           */
          if ((maxFragments > 0) && (fragmentNo >= maxFragments))
            break;
        }
        else
        {
          /* continuation of the current fragment */
          if (inEscape)
          {
            /*
             * Always remove the escape before an escaped delimiter. Do it for
             * double quotes and the escape char optionally.
             */
            if ((nextChar == delimiter)
                || (unescapeAllChars
                    && ((nextChar == StringUtils.CHAR_DOUBLE_QUOTE)
                        || (nextChar == StringUtils.CHAR_ESCAPE_CHAR))))
            {
              /*
               * remove the escape char used to escape record delimiter or
               * double quote
               */
              recordBuffer.deleteCharAt(recordBuffer.length() - 1);
            }
          }
          recordBuffer.append((char) nextChar);

          if ((nextChar == StringUtils.CHAR_DOUBLE_QUOTE) && !inEscape)
          {
            /* unescaped double quote - toggle inQuotes */
            inQuotes = !inQuotes;
          }

          inEscape = !inEscape && (nextChar == StringUtils.CHAR_ESCAPE_CHAR);
        }
      }

      /* treat any remaining chars in the end as another fragment */
      if (recordBuffer.length() > 0)
        processor.accept(new Fragment(fragmentNo++, currentFragmentOffset,
            recordBuffer.toString()));
    }
    catch (Exception e)
    {
      throw new CuRuntimeException(CUMessages.CU_INTERNAL_OPERATION, e);
    }

    return numRead;
  }

  /**
   * Standardizes the input string to improve chances of matching with another
   * string based on visible characters. This includes ensuring normalization
   * (for unicode char sequences that have multiple representations) and
   * reduction of white space.
   * 
   * @param s
   *          - input string
   * 
   * @return standardized string
   */
  public static String standardizeVisible(String s)
  {
    /* normalize unicode char sequences */
    String ret = Normalizer.normalize(s, Form.NFC);

    /* replace white space with a single space */
    ret = ret.replaceAll("[\\s\\h\\v]+", " ");

    /* trim leading or trailing white space */
    ret = ret.trim();

    return ret;
  }

  public static class Fragment
  {
    /** fragment no starting from 0 */
    public int    fragmentNo;

    /** offset in bytes or chars depending on input arguments to the call */
    public int    offset;

    /** fragment itself as a string */
    public String fragment;

    public Fragment(int no, int off, String f)
    {
      fragmentNo = no;
      offset = off;
      fragment = f;
    }
  }
}
