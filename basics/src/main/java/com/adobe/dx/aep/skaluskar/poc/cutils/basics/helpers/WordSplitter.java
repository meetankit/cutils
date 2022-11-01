/**
 * WordSplitter.java
 */
package com.adobe.dx.aep.skaluskar.poc.cutils.basics.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CUMessages;
import com.adobe.dx.aep.skaluskar.poc.cutils.basics.exceptions.CuRuntimeException;

import lombok.AllArgsConstructor;

/**
 * @author sakalusk
 *
 */
public class WordSplitter
{
  private static final String        WORDS_BY_FREQUENCY =
      "/files/words/words-by-frequency.txt";

  private static final Logger        logger             = LoggerFactory
      .getLogger(WordSplitter.class);

  private static Map<String, Double> wordCost;

  private static int                 maxWordLength;

  private static double              maxWordCost;

  private static WordSplitter        instance;

  private WordSplitter()
  {
    initDictionary();
  }

  public static WordSplitter getInstance()
  {
    if (instance == null)
    {
      synchronized (WordSplitter.class)
      {
        if (instance == null)
          instance = new WordSplitter();
      }
    }

    return instance;
  }

  private void initDictionary()
  {
    if (wordCost == null)
    {
      InputStream inStream = this.getClass().getResourceAsStream(
          WORDS_BY_FREQUENCY);
      BufferedReader reader = null;
      wordCost = new HashMap<String, Double>();
      maxWordCost = 0;

      try
      {
        /* first read all the words */
        reader = new BufferedReader(new InputStreamReader(inStream,
            StandardCharsets.UTF_8));
        String word;
        int maxLength = 0;
        List<String> words = new ArrayList<String>();
        while ((word = reader.readLine()) != null)
        {
          String normalizedWord = word.trim().toLowerCase();
          if (!StringUtils.isNullOrEmpty(normalizedWord))
          {
            words.add(normalizedWord.trim());
            int length = normalizedWord.length();
            if (length > maxLength)
              maxLength = length;
          }
        }
        maxWordLength = maxLength;

        /* add the words along with cost */
        double naturalLogDictionaryWordsCount = Math.log(words.size());
        long wordIdx = 0;
        for (String w : words)
        {
          double cost = Math.log(++wordIdx * naturalLogDictionaryWordsCount);
          wordCost.put(w, cost);
          if (cost > maxWordCost)
            maxWordCost = cost;
        }
      }
      catch (Exception e)
      {
        throw new CuRuntimeException(
            CUMessages.CU_ERROR_err_EXECUTING_INTERNAL_COMMAND,
            e, e.getMessage());
      }
      finally
      {
        if (reader != null)
        {
          try
          {
            reader.close();
          }
          catch (IOException e)
          {
            throw new CuRuntimeException(
                CUMessages.CU_ERROR_err_EXECUTING_INTERNAL_COMMAND,
                e, e.getMessage());
          }
        }
      }
    }
  }

  private BestMatch bestMatch(String input, int inputLength,
      List<BestMatch> cost)
  {
    BestMatch minPair = new BestMatch(inputLength, Double.MAX_VALUE);
    int maxCandidateLength = Math.min(maxWordLength, inputLength);
    for (int length = 0; length <= maxCandidateLength; length++)
    {
      /*
       * evaluate candidate word of length length ending at index inputLength -
       * 1.
       */
      int startPos = inputLength - length;
      String candidateWord = input.substring(startPos, inputLength)
          .toLowerCase();
      BestMatch bestUptoStart = (startPos < inputLength) ? cost.get(startPos)
          : minPair;
      double candidateCost = Double.MAX_VALUE;
      if (wordCost.containsKey(candidateWord))
        candidateCost = bestUptoStart.cost + wordCost.get(candidateWord);
      if (candidateCost < minPair.cost)
        minPair = new BestMatch(length, candidateCost);
      logger.trace(
          "input: {}, inputLength: {}, candidateWordLength: {}, "
              + "startPos: {}, candidateWord: {}, minCost: {}, "
              + "candidateCost: {}",
          input, inputLength, length, startPos, candidateWord,
          minPair.cost, candidateCost);
    }

    logger.trace("minPair - length: {}, cost: {}",
        minPair.bestLength, minPair.cost);
    return minPair;
  }

  private List<String> split(String sentence)
  {
    /*
     * list of best matches for substring of length p where p is the position in
     * the list
     */
    List<BestMatch> bestMatches = new ArrayList<>();
    bestMatches.add(new BestMatch(0, 0));
    for (int index = 1; index < sentence.length() + 1; index++)
    {
      bestMatches.add(bestMatch(sentence, index, bestMatches));
    }

    int idx = sentence.length();
    List<String> output = new ArrayList<>();
    while (idx > 0)
    {
      BestMatch c = bestMatches.get(idx);
      output.add(0, sentence.substring(idx - c.bestLength, idx));
      idx -= c.bestLength;
    }

    return output;
  }

  private void processItem(String item, boolean isNumber, List<String> words)
  {
    if (!StringUtils.isNullOrEmpty(item))
    {
      if (isNumber)
      {
        if (!words.contains(item))
          words.add(item);
      }
      else
      {
        List<String> list = split(item.toLowerCase());
        if ((list != null) && (list.size() > 0))
        {
          for (String s : list)
          {
            if (!words.contains(s))
              words.add(s);
          }
        }
      }
    }
  }

  /**
   * Extracts common words from a string excluding any punctuation or
   * whitespace. The words need not be separate by whitespace or punctuation.
   * 
   * @param sentence
   *          - input string
   * @param includeNumbers
   *          - whether numbers should be included in the output
   * @return list of words in the string
   */
  public List<String> extractWords(String sentence, boolean includeNumbers)
  {
    List<String> output = null;

    if (!StringUtils.isNullOrEmpty(sentence))
    {
      output = new ArrayList<String>();
      char chars[] = sentence.toCharArray();
      StringBuilder sb = new StringBuilder();
      boolean inNumber = false;
      boolean inWord = false;
      for (int i = 0; i < chars.length; i++)
      {
        char ch = chars[i];
        boolean charIsLetter = Character.isLetter(ch);
        boolean charIsDigit = Character.isDigit(ch);

        /* start or grow the current item */
        if ((charIsLetter && !inNumber) ||
            (charIsDigit && !inWord))
        {
          sb.append(ch);
          inWord = charIsLetter;
          inNumber = charIsDigit;
        }

        /* wrap up the current item otherwise */
        if ((inNumber && !charIsDigit) ||
            (inWord && !charIsLetter))
        {
          if ((inNumber && includeNumbers) || inWord)
            processItem(sb.toString(), inNumber, output);
          sb = new StringBuilder();
          inNumber = false;
          inWord = false;
        }
      }

      /* process any remaining fragment at the end */
      if (sb.length() > 0)
      {
        if ((inNumber && includeNumbers) || inWord)
          processItem(sb.toString(), inNumber, output);
      }
    }

    return output;
  }

  @AllArgsConstructor
  private static class BestMatch
  {
    private int    bestLength;

    private double cost;
  }

  public static void main(String args[])
  {
    WordSplitter ws = WordSplitter.getInstance();
    List<String> words = ws.extractWords("ss-57-customers-6", false);
    if (words != null)
    {
      System.out.println(StringUtils.concatenate(words, " "));
    }
  }
}
