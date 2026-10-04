/*
 * HermiteInterpolation.java
 *
 * Cubic Hermite Spline interpolation.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.math;

import com.brandonreiss.geometry.*;
import java.util.Vector;
import java.util.Arrays;

public class HermiteInterpolation extends Interpolation {

  ///<summary>Hermite spline coefficients.</summary>
  private static Matrix4x4 hermiteMat = new Matrix4x4( 2,-3, 0, 1,
                                                       1,-2, 1, 0,
                                                      -2, 3, 0, 0,
                                                       1,-1, 0, 0);
  ///<summary>Hermite spline derivative coefficients.</summary>
  private static Matrix4x4 hermiteMat_ddt = hermiteMat.clone().mul(new Matrix4x4(0,3,0,0,
                                                                                 0,0,2,0,
                                                                                 0,0,0,1,
                                                                                 0,0,0,0));

  ///<summary>Independent variable values.</summary>
  private double[] tValues = null;
  ///<summary>Dependent variable values and slopes.</summary>
  private double[] controlValues = null;

  /*
   * Instance methods.
  */

  ///<summary>Interpolate values with respect to t.</summary>
  ///<remarks>
  ///<para>The control values should be given as a continuous stream of
  ///spline control values of the form {..., x_i, m_i, x_i+1, m_i+1, ...}
  ///with the values for t {..., t_i, t_i+1, ...} having a 1-1
  ///correspondence with each pair (x_i, m_i). Values of t should be
  ///monotonically increasing. Note that t itself may be thought of as a
  ///generic time parameter while x and m are function values and slopes.
  ///</remarks>
  ///<param name="tValues_">Monotonically increasing dependent variable.</param>
  ///<param name="controlValues_">Sequential array of values and slopes</param>
  ///<param name="storeReference">If enabled, data are not copied</param>
  public HermiteInterpolation(double[] tValues_, double[] controlValues_,
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
    return controlValues[2 * idx];
  }

  @Override
  public double valueInterval(double t, int intervalIdx) {
    return HermiteInterpolation.value(controlValues, intervalIdx * 2, t);
  }

  @Override
  public double ddtInterval(double t, int intervalIdx) {
    return HermiteInterpolation.ddt(controlValues, intervalIdx * 2, t);
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
  ///<param name="spline">Curve parameters {p0, m0, p1, m1}</param>
  public static double value(Vector4 spline, double t) {
    // Setup t.
    final double t2 = t  * t;
    final double t3 = t2 * t;
    double[] bases = {t3, t2, t, 1};
    Vector4.transform(hermiteMat, bases);
    return spline.dot(bases);
  }

  ///<summary>Compute the interpolated value at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters {p0, m0, p1, m1}</param>
  public static double value(double[] spline, double t) {
    // Setup t.
    final double t2 = t  * t;
    final double t3 = t2 * t;
    double[] bases = {t3, t2, t, 1};
    Vector4.transform(hermiteMat, bases);
    return Vector4.dot(spline, bases);
  }

  ///<summary>Compute the interpolated value at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters {p0, m0, p1, m1}</param>
  public static double value(double[] spline, int idx, double t) {
    // Setup t.
    final double t2 = t  * t;
    final double t3 = t2 * t;
    double[] bases = {t3, t2, t, 1};
    Vector4.transform(hermiteMat, bases);
    return Vector4.dot(spline, idx, bases);
  }

  ///<summary>Compute the interpolated derivative at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters {p0, m0, p1, m1}</param>
  public static double ddt(Vector4 spline, double t) {
    // Setup t.
    final double t2 = t  * t;
    double[] bases = {0, t2, t, 1};
    Vector4.transform(hermiteMat_ddt, bases);
    return spline.dot(bases);
  }

  ///<summary>Compute the interpolated derivative at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters {p0, m0, p1, m1}</param>
  public static double ddt(double[] spline, double t) {
    // Setup t.
    final double t2 = t  * t;
    double[] bases = {0, t2, t, 1};
    Vector4.transform(hermiteMat_ddt, bases);
    return Vector4.dot(spline, bases);
  }

  ///<summary>Compute the interpolated derivative at t.</summary>
  ///<param name="t">Parametric step in interval [0, 1]</param>
  ///<param name="spline">Curve parameters {p0, m0, p1, m1}</param>
  public static double ddt(double[] spline, int idx, double t) {
    // Setup t.
    final double t2 = t  * t;
    double[] bases = {0, t2, t, 1};
    Vector4.transform(hermiteMat_ddt, bases);
    return Vector4.dot(spline, idx, bases);
  }

  ///<summary>Approximate curve length.</summary>
  ///<param name="t0">Parametric step begin value</param>
  ///<param name="t1">Parametric step end value</param>
  ///<param name="n">Steps used to estimate length</param>
  public static double lengthEst(Vector4 spline, double t0, double t1, int n) {
    final double tStep = (t1 - t0) / n;
    double yPrev;
    {
      final double ddt = ddt(spline, t0);
      yPrev = Math.sqrt(1 + (ddt * ddt));
    }
    // Sum double trapezoids all the way down.
    double length = 0;
    double t = t0 + tStep;
    for (int step = 0; step < n; ++step, t += tStep) {
      final double ddt = ddt(spline, t);
      final double y = Math.sqrt(1 + (ddt * ddt));
      length += tStep * (yPrev + y);
      yPrev = y;
    }
    // Take half once.
    return length * 0.5;
  }

  ///<summary>Approximate curve length.</summary>
  ///<param name="t0">Parametric step begin value</param>
  ///<param name="t1">Parametric step end value</param>
  ///<param name="n">Steps used to estimate length</param>
  public static double lengthEst(double[] spline, double t0, double t1, int n) {
    final double tStep = (t1 - t0) / n;
    double yPrev;
    {
      final double ddt = ddt(spline, t0);
      yPrev = Math.sqrt(1 + (ddt * ddt));
    }
    // Sum double trapezoids all the way down.
    double length = 0;
    double t = t0 + tStep;
    for (int step = 0; step < n; ++step, t += tStep) {
      final double ddt = ddt(spline, t);
      final double y = Math.sqrt(1 + (ddt * ddt));
      length += tStep * (yPrev + y);
      yPrev = y;
    }
    // Take half once.
    return length * 0.5;
  }

  ///<summary>Approximate curve length.</summary>
  ///<param name="t0">Parametric step begin value</param>
  ///<param name="t1">Parametric step end value</param>
  ///<param name="n">Steps used to estimate length</param>
  public static double lengthEst(double[] spline, int idx, double t0, double t1, int n) {
    final double tStep = (t1 - t0) / n;
    double yPrev;
    {
      final double ddt = ddt(spline, idx, t0);
      yPrev = Math.sqrt(1 + (ddt * ddt));
    }
    // Sum double trapezoids all the way down.
    double length = 0;
    double t = t0 + tStep;
    for (int step = 0; step < n; ++step, t += tStep) {
      final double ddt = ddt(spline, idx, t);
      final double y = Math.sqrt(1 + (ddt * ddt));
      length += tStep * (yPrev + y);
      yPrev = y;
    }
    // Take half once.
    return length * 0.5;
  }
}

