package com.adobe.dx.aep.poc.cutils.basics.helpers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author sakalusk
 *
 */

public class DistanceUtils
{
  /**
   * Computes the set of ngrams over the input string.
   * 
   * @param n
   *          - size of the ngrams
   * 
   * @param s
   *          - input string over which to compute ngrams
   * 
   * @return set of ngrams. Returns an empty set if the input string is null.
   */
  public static Set<String> ngrams(int n, String s)
  {
    HashSet<String> ngramSet = new HashSet<String>();

    if ((s != null) && (s.length() > 0))
    {
      char c[] = s.toCharArray();
      for (int i = 0; i <= c.length - n; i++)
      {
        String ng = new String(c, i, n);

        /*
         * we can have ngrams with spaces in it as long as there are some
         * non-space chars
         */
        if (!StringUtils.isNullOrEmpty(ng.trim()))
        {
          ngramSet.add(ng);
        }
      }
    }

    return ngramSet;
  }

  /**
   * Computes the Jaccard distance between sets of ngrams over specified
   * strings.
   * 
   * @param n
   *          - size of ngrams
   * 
   * @param a
   *          - input string
   * 
   * @param b
   *          - input string
   */
  public static double jaccardDistanceNgrams(int n, String a, String b)
  {
    Set<String> agrams = ngrams(n, a);
    Set<String> bgrams = ngrams(n, b);
    return jaccardDistanceNgrams(agrams, bgrams);
  }

  /**
   * Computes the Jaccard distance between sets of ngrams; this is an
   * optimization when the caller already has the ngrams.
   * 
   * @param ngrams1
   *          - ngrams for 1st input string
   * 
   * @param ngrams2
   *          - ngrams for 2nd input string
   */
  public static double jaccardDistanceNgrams(Set<String> ngrams1,
      Set<String> ngrams2)
  {
    double ret = 0;
    Set<String> union = new HashSet<String>(ngrams1);
    union.addAll(ngrams2);
    if (union.size() != 0)
    {
      Set<String> intersection = new HashSet<String>(ngrams1);
      intersection.retainAll(ngrams2);
      double jc = 1.0 * intersection.size() / union.size();
      ret = 1 - jc;
    }
    return ret;
  }

  /**
   * Computes the Jaccard distance between sets of strings. The sets are passed
   * in as arrays (any duplicates within each array will be ignored).
   * 
   * @param strings1
   *          - 1st set of strings
   * @param strings2
   *          - 2nd set of strings
   * @return Jaccard distance
   */
  public static double jaccardDistanceStrings(String strings1[],
      String strings2[])
  {
    return jaccardDistanceStringsFuzzy(strings1, strings2, 0, 0);
  }

  /**
   * Computes the Jaccard distance between sets of strings optionally using a
   * fuzzy comparison. The sets are passed in as arrays (any duplicates within
   * each array will be ignored).
   * 
   * @param strings1
   *          - 1st set of strings
   * @param strings2
   *          - 2nd set of strings
   * @param ngramSize
   *          - size of ngram for fuzzy comparison
   * @param threshold
   *          - threshold distance below which strings are considered equal for
   *          fuzzy comparison
   *
   * @return Jaccard distance
   */
  public static double jaccardDistanceStringsFuzzy(String strings1[],
      String strings2[], int ngramSize, double threshold)
  {
    double ret = 1;

    if (((strings1 == null) || (strings1.length == 0))
        && ((strings2 == null) || (strings2.length == 0)))
      ret = 0;
    else if ((strings1 != null)
        && (strings1.length > 0)
        && (strings2 != null)
        && (strings2.length > 0))
    {
      List<String> list1 = Arrays.asList(strings1);
      List<String> list2 = Arrays.asList(strings2);
      Set<String> union = new HashSet<String>(list1);
      union.addAll(list2);
      if (union.size() != 0)
      {
        int intSize = 0;
        if (ngramSize > 0)
        {
          Set<String> set1 = new HashSet<String>(list1);
          for (String s1 : set1)
          {
            for (String s2 : list2)
            {
              if (minCaseInsensitiveJaccardEditDistance(ngramSize, s1,
                  s2) <= threshold)
              {
                intSize++;
                break;
              }
            }
          }
        }
        else
        {
          Set<String> intersection = new HashSet<String>(list1);
          intersection.retainAll(list2);
          intSize = intersection.size();
        }
        double jc = (1.0 * intSize) / union.size();
        ret = 1 - jc;
      }
    }

    return ret;
  }

  private static double minFuzzyDist(String input, String strings[],
      int ngramSize)
  {
    double ret = 1;

    if (StringUtils.isNullOrEmpty(input))
      ret = 0;
    else if ((strings != null)
        && (strings.length > 0))
    {
      for (String s : strings)
      {
        if (!StringUtils.isNullOrEmpty(s))
        {
          double dist;
          if (ngramSize > 0)
            dist = minCaseInsensitiveJaccardEditDistance(ngramSize, input, s);
          else
            dist = jaccardDistanceNgrams(ngramSize, input.toLowerCase(),
                s.toLowerCase());

          if (dist < ret)
            ret = dist;
        }
      }
    }

    return ret;
  }

  private static double avgMinFuzzyDist(String strings1[],
      String strings2[], int ngramSize)
  {
    double ret = 1;

    if ((strings1 == null) || (strings1.length == 0))
      ret = 0;
    else if ((strings2 != null) && (strings2.length > 0))
    {
      Set<String> set1 = new HashSet<String>();
      for (String s : strings1)
      {
        if (!StringUtils.isNullOrEmpty(s))
          set1.add(s.toLowerCase());
      }

      double sum = 0;
      for (String s : set1)
      {
        sum += minFuzzyDist(s, strings2, ngramSize);
      }
      ret = sum / set1.size();
    }

    return ret;
  }

  public static double minAvgMinFuzzyDist(String strings1[],
      String strings2[], int ngramSize)
  {
    double d1 = avgMinFuzzyDist(strings1, strings2, ngramSize);
    double d2 = avgMinFuzzyDist(strings2, strings1, ngramSize);
    return Math.min(d1, d2);
  }

  /**
   * Computes the Levenshtein distance between specified strings.
   * 
   * @param a
   *          - input string
   * 
   * @param b
   *          - input string
   */
  public static int levenshtein(String a, String b)
  {
    int ret = 0;
    if (StringUtils.isNullOrEmpty(a))
    {
      if (!StringUtils.isNullOrEmpty(b))
        ret = b.length();
    }
    else
    {
      if (StringUtils.isNullOrEmpty(b))
        ret = a.length();
      else
      {
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
          costs[j] = j;
        for (int i = 1; i <= a.length(); i++)
        {
          // j == 0; nw = lev(i - 1, j)
          costs[0] = i;
          int nw = i - 1;
          for (int j = 1; j <= b.length(); j++)
          {
            int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
            nw = costs[j];
            costs[j] = cj;
          }
        }
        ret = costs[b.length()];
      }
    }

    return ret;
  }

  /**
   * Computes the containment index of an input set of ngrams in a target set of
   * ngrams. Returns 1 if input set is null or empty. Returns 0 if target set is
   * null or empty.
   * 
   * @param input
   *          - input set of ngrams whose containment index in the other set is
   *          to be calculated
   * 
   * @param target
   *          - target set of ngrams in which containment is to be measured
   * 
   * @return containment index
   */
  public static double containmentIndex(Set<String> input, Set<String> target)
  {
    double ret = 1.0;
    if ((input != null) && (input.size() > 0))
    {
      ret = 0.0;
      if ((target != null) && (target.size() > 0))
      {
        Set<String> intersection = new HashSet<String>(input);
        intersection.retainAll(target);
        ret = (1.0 * intersection.size()) / input.size();
      }
    }

    return ret;
  }

  /**
   * Computes the minimum of Jaccard distance and normalized Edit (Levenshtein)
   * distance between strings. The Jaccard distance is computed using the
   * specified ngram size. Note that the result isn't a valid distance metric;
   * it doesn't satisfy triangle inequality.
   * 
   * @param n
   *          - size of ngrams
   * 
   * @param s1
   *          - input string
   * 
   * @param s2
   *          - input string
   */
  public static double minCaseInsensitiveJaccardEditDistance(int ngramSize,
      String s1, String s2)
  {
    double dist = 0;
    int s1Length = StringUtils.isNullOrEmpty(s1) ? 0 : s1.length();
    int s2Length = StringUtils.isNullOrEmpty(s2) ? 0 : s2.length();
    int maxLength = Math.max(s1Length, s2Length);
    /* dist is 0 if both are null or empty */
    if (maxLength > 0)
    {
      String lc1 = StringUtils.isNullOrEmpty(s1) ? s1 : s1.toLowerCase();
      String lc2 = StringUtils.isNullOrEmpty(s2) ? s2 : s2.toLowerCase();
      double jd = jaccardDistanceNgrams(ngramSize, lc1, lc2);
      int ld = levenshtein(lc1, lc2);
      dist = Math.min(jd, (ld * 1.0) / maxLength);
    }

    return dist;
  }

  /**
   * Computes the fraction of overlap for 2 ranges specified as [r1min, r1max]
   * and [r2min, r2max].
   * 
   * @param r1min
   *          - start of range 1
   * @param r1max
   *          - end of range 1
   * @param r2min
   *          - start of range 2
   * @param r2max
   *          - end of range 2
   * @return fractional overlap between 0 and 1
   */
  public static double rangeOverlap(double r1min, double r1max, double r2min,
      double r2max)
  {
    double ret = 0;

    if (r1min > r1max)
      throw new IllegalArgumentException(String.format(
          "Invalid range: [%f, %f]", r1min, r1max));

    if (r2min > r2max)
      throw new IllegalArgumentException(String.format(
          "Invalid range: [%f, %f]", r2min, r2max));

    /* set up so that left interval starts first so that leftmin <= rightmin */
    double leftmin, leftmax, rightmin, rightmax;
    if (r1min <= r2min)
    {
      leftmin = r1min;
      leftmax = r1max;
      rightmin = r2min;
      rightmax = r2max;
    }
    else
    {
      leftmin = r2min;
      leftmax = r2max;
      rightmin = r1min;
      rightmax = r1max;
    }

    if (leftmax >= rightmin)
    {
      /* there is overlap */
      double rangeStart = leftmin;
      double rangeEnd = Math.max(leftmax, rightmax);
      double totalRange = rangeEnd - rangeStart;

      if (totalRange == 0)
        ret = 1;
      else
      {
        double overlapStart = rightmin;
        double overlapEnd = Math.min(leftmax, rightmax);
        double overlap = overlapEnd - overlapStart;

        ret = overlap / totalRange;
      }
    }

    return ret;
  }

  public static void main(String args[])
  {
    /*
     * String sets[][] = { { "abc" }, { "ab", "ghi" }, { "abc", "def", "ghi" },
     * { "def", "ghij" }, { "pqr", "stu" } };
     * 
     * for (int i = 0; i < sets.length; i++) for (int j = i; j < sets.length;
     * j++) System.out.println(String.format("s1: %s, s2: %s, dist: %f",
     * SerDeUtils.serializeToJson(sets[i]), SerDeUtils.serializeToJson(sets[j]),
     * minAvgMinFuzzyDist(sets[i], sets[j], 3)));
     */
    /*
    String item1[] = { "account_number", "phone_number", "city", "email" };
    String item2[] = { "account_number", "phone_number", "city", "email" };
    System.out.println(minAvgMinFuzzyDist(item1, item2, 3));
    */
    System.out.println(rangeOverlap(41.644, 42, 41.66, 42));
  }
}
