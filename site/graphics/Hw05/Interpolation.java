/*
 * Interpolation.java
 *
 * Function interpolation interface.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.math;

import com.brandonreiss.geometry.*;
import java.util.Vector;
import java.util.Arrays;

///<summary>Classes used to interpolate values.</summary>
public abstract class Interpolation {

  ///<summary>Interpolate the curve at t within a normalized sub-interval.</summary>
  public abstract double valueInterval(double t, int intervalIdx);

  ///<summary>Interpolate the curve derivative at t within a normalized sub-interval.</summary>
  public abstract double ddtInterval(double t, int intervalIdx);

  ///<summary>Get the number of points in the curve.</summary>
  public abstract int numberOfPoints();

  ///<summary>Get the idx-th sample of t.</summary>
  public abstract double[] t();

  ///<summary>Get the idx-th sample of t.</summary>
  public abstract double t(int idx);

  ///<summary>Get the idx-th function value.</summary>
  public abstract double sample(int idx);

  ///<summary>Interpolate the curve value at t.</summary>
  ///<remarks>
  ///<para>The value of t is given with respect to the raw measurements
  ///of t stored during construction. In other words, the valid interval
  ///of t can be anything as defined by the input data.
  ///</para>
  ///</remarks>
  public double value(double t) {
    // Lookup interval.
    final int findIdx = Arrays.binarySearch(t(), t);
    final int intervalIdxRaw = (findIdx < 0) ? (-findIdx - 2) : findIdx;
    final int intervalIdx = Math.max(0, Math.min(numberOfPoints() - 2, intervalIdxRaw));
    // Scale t within the interval.
    final double tStart = t(intervalIdx);
    final double tNorm = (t - tStart) / (t(intervalIdx + 1) - tStart);
    // Compute value within the interval.
    return valueInterval(tNorm, intervalIdx);
  }

  ///<summary>Interpolate the curve derivative value at t.</summary>
  ///<remarks>
  ///<para>The value of t is given with respect to the raw measurements
  ///of t stored during construction. In other words, the valid interval
  ///of t can be anything as defined by the input data.
  ///</para>
  ///</remarks>
  public double ddt(double t) {
    // Lookup interval.
    final int findIdx = Arrays.binarySearch(t(), t);
    final int intervalIdxRaw = (findIdx < 0) ? (-findIdx - 2) : findIdx;
    final int intervalIdx = Math.max(0, Math.min(numberOfPoints() - 2, intervalIdxRaw));
    // Scale t within the interval.
    final double tStart = t(intervalIdx);
    final double tNorm = (t - tStart) / (t(intervalIdx + 1) - tStart);
    // Compute value within the interval.
    return ddtInterval(tNorm, intervalIdx);
  }
}

