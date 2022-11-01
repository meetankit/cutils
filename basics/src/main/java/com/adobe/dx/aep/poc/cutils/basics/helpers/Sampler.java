/**
 * Sampler.java
 */
package com.adobe.dx.aep.poc.cutils.basics.helpers;

import java.util.Random;

/**
 * @author admin
 *
 *         Helper class to sample elements from a set, where the number of
 *         elements in the set is known in advance.
 * 
 *         Note that only int valued set sizes are supported right now.
 */
public class Sampler
{
  /** seed for randomization */
  private String seed;

  /** number of remaining elements */
  private int    remainingElements;

  /** number of remaining elements to pick based on the target */
  private int    remainingTarget;

  private Random randomNumberGenerator;

  /**
   * Constructor for Sampler
   * 
   * @param numElements
   *          - number of elements in the set from which a sample needs to be
   *          selected
   * @param target
   *          - number of random elements to be selected as a sample
   * @param seed
   *          - optional (can be null). Providing the same seed with the same
   *          parameters will produce identical samples
   */
  public Sampler(int numElements, int target, String randomSeed)
  {
    remainingElements = numElements;
    remainingTarget = target;
    seed = randomSeed;
    if (StringUtils.isNullOrEmpty(seed))
      randomNumberGenerator = new Random();
    else
      randomNumberGenerator = new Random(seed.hashCode());
  }

  /**
   * Randomly decides if the next element should be selected. It should be
   * called for each element in the set for correct results.
   */
  public boolean select()
  {
    /*
     * Note: this uses Knuth's algorithm described in The Art of Programming Vol
     * 2. Seminumerical Algorithms, section 3.4.2. for sampling.
     * 
     * Explanation: an obvious and simple method would be to pick each element
     * with a probability of target/numElements. That would result in an average
     * of target elements but with a fairly high variance (the number of
     * elements selected follows a binomial distribution).
     * 
     * Knuth's algorithm is small variation of this, where the next element is
     * chosen with a probability of remainingTarget/remainingElements. Although
     * it seems counter-intuitive, the probability of picking any element is
     * still target/numElements (this is easier to prove by induction).
     */

    /*
     * Choose a random index between 0 (inclusive) and remainingElements
     * (exclusive).
     */
    int nextIndex = randomNumberGenerator.nextInt(remainingElements);

    /* probability of ret being true is remainingTarget/remainingElements */
    boolean ret = (nextIndex < remainingTarget);
    if (ret)
      remainingTarget--;
    remainingElements--;

    return ret;
  }
}
