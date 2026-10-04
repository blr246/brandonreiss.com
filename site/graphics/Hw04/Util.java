/*
 * Util.java
 *
 * Geometry utilities.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Util {
  public enum WindingDirection { CW, CCW, }

  /// <summary> Helper method to wind faces in a grid of vertices. </summary>
  public static void WindFacesVertexGrid(int startIdx,
                                         int rowPitch, int numRows, 
                                         WindingDirection dir,
                                         int faceStartIdx, int[][] faces) {

    final int numFaces = (rowPitch - 1) * (numRows - 1);
    final int faceIdxEnd = faceStartIdx + numFaces;
    assert(faces.length >= faceIdxEnd);

    {
      int faceIdx = faceStartIdx;
      int vIdxT = startIdx;
      int vIdxB = vIdxT + rowPitch;
      for (int r = 0; r < (numRows - 1); ++r, ++vIdxT, ++vIdxB) {
        for (int c = 0; c < (rowPitch - 1); ++c, ++faceIdx, ++vIdxT, ++vIdxB) {
          if (dir == WindingDirection.CW) {
            faces[faceIdx] = new int[] {vIdxT, vIdxT + 1, vIdxB + 1, vIdxB};
          }
          else {
            faces[faceIdx] = new int[] {vIdxT + 1, vIdxT, vIdxB, vIdxB + 1};
          }
        }
      }
    }
  }

  /// <summary> Project the point to the given plane. </summary>
  public static Vector3 PointProjectPlane(Vector3 pt, Vector4 plane) {
    final double distance = pt.dot(plane) - plane.w;
    return pt.add(plane.clone().mul(distance));
  }

  /// <summary> Project the point to the given plane. </summary>
  public static Vector3 PointProjectPlane(Vector3 pt, Vector4 plane, Vector3 res) {
    return PointProjectPlane(res.set(pt), plane);
  }

  /// <summary> Project a point to a plane along the given direction. </summary>
  public static Vector3 PointProjectPlaneAlongDir(Vector3 pt, Vector4 plane, Vector3 dir) {
    // Solve for projection to the plane
    final double nDotV = pt.dot(plane);
    final double nDotDir = dir.dot(plane);
    // Get parametric step along light direction to plane.
    final double t = (-nDotV - plane.w) / nDotDir;
    // Add distance along light ray to projection.
    return pt.add(dir.clone().mul(t));
  }
}

