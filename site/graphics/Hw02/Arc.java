/*
 * Arc.java
 *
 * A wedge shape.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

/// <summary> An arc goes from theta start to theta end. </summary>
public class Arc extends Surface {
  public final int DEFAULT_STEPS = 15;
  private double thetaStart;
  private double thetaEnd;

  public Arc(double thetaStart_, double thetaEnd_) {
    thetaStart = thetaStart_;
    thetaEnd = thetaEnd_;
    makeVertices(DEFAULT_STEPS);
  }

  public Arc(double thetaStart_, double thetaEnd_, int steps) {
    thetaStart = thetaStart_;
    thetaEnd = thetaEnd_;
    makeVertices(steps);
  }

  private void makeVertices(int steps) {
    vertices = new Vector4[steps];
    final double sweep = thetaEnd - thetaStart;
    final double stepSize = sweep / (steps - 1);
    for (int step = 0; step < steps; ++step) {
      final double theta = thetaStart + (step * stepSize);
      vertices[step] = new Vector4(0.5 * Math.cos(theta),
                                   0.5 * Math.sin(theta), 0.0, 1.0);
    }
    updateAllocations();
  }
}

