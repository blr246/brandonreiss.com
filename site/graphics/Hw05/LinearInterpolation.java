/*
 * LinearInterpolation.java
 *
 * Linear interpolation.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.math;

import com.brandonreiss.geometry.*;
import java.util.Vector;
import java.util.Arrays;

public class LinearInterpolation extends Interpolation {

  ///<summary>Independent variable values.</summary>
  private double[] tValues = null;
  ///<summary>Dependent variable values.</summary>
  private double[] controlValues = null;

  /*
   * Instance methods.
  */

  ///<summary>Interpolate values with respect to t.</summary>
  ///<remarks>
  ///<para>The control values should be given as a continuous stream of
  ///spline control values of the form {..., x_i, x_i+1, ...}
  ///with the values for t {..., t_i, t_i+1, ...} having a 1-1
  ///correspondence with each value x_i. Values of t should be
  ///monotonically increasing.
  ///</remarks>
  ///<param name="tValues_">Monotonically increasing dependent variable</param>
  ///<param name="controlValues_">Sequential array of values</param>
  ///<param name="storeReference">If enabled, data are not copied</param>
  public LinearInterpolation(double[] tValues_, double[] controlValues_,
                             boolean storeReference) {
    if (!storeReference) {
      tValues = Arrays.copyOf(tValues_, tValues_.length);
      controlValues = Arrays.copyOf(controlValues_, controlValues_.length);
    }
    else {
      tValues = tValues_;
      controlValues = controlValues_;
    }
  }

  @Override
  public double[] t() {
    return tValues;
  }

  @Override
  public double t(int idx) {
    return tValues[idx];
  }

  @Override
  public double sample(int idx) {
    return controlValues[idx];
  }

  @Override
  public double valueInterval(double t, int intervalIdx) {
    return LinearInterpolation.value(controlValues, intervalIdx, t);
  }

  @Override
  public double ddtInterval(double t, int intervalIdx) {
    return LinearInterpolation.ddt(controlValues, intervalIdx, t);
  }

  @Override
  public int numberOfPoints() {
    return tValues.length;
  }

  /*
   * Static methods.
  */

  ///<summary>Compute the interpolated value at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters</param>
  public static double value(Vector2 spline, double t) {
    // Compute slope and then get value.
    final double m = spline.y - spline.x;
    return (m * t) + spline.x;
  }

  ///<summary>Compute the interpolated value at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters</param>
  public static double value(double[] spline, double t) {
    // Compute slope and then get value.
    final double m = spline[1] - spline[0];
    return (m * t) + spline[0];
  }

  ///<summary>Compute the interpolated value at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters</param>
  public static double value(double[] spline, int idx, double t) {
    // Compute slope and then get value.
    final double m = spline[idx + 1] - spline[idx + 0];
    return (m * t) + spline[idx + 0];
  }

  ///<summary>Compute the interpolated derivative at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters</param>
  public static double ddt(Vector2 spline, double t) {
    // Compute slope.
    return spline.y - spline.x;
  }

  ///<summary>Compute the interpolated derivative at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters</param>
  public static double ddt(double[] spline, double t) {
    // Compute slope.
    return spline[1] - spline[0];
  }

  ///<summary>Compute the interpolated derivative at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters</param>
  public static double ddt(double[] spline, int idx, double t) {
    // Compute slope.
    return spline[idx + 1] - spline[idx + 0];
  }
}

