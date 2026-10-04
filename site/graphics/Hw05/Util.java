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

  ///<summaryHelper method to wind faces in a grid of vertices.</summary>
  public static int WindFacesVertexGrid(int startIdx,
                                        int rowPitch, int numRows, 
                                        WindingDirection dir,
                                        int faceStartIdx, int[][] faces) {

    assert(rowPitch > 1);
    assert(numRows > 1);
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
    return numFaces;
  }

  ///<summaryGet the distance from the point to the plane.</summary>
  ///<remarks>
  ///<para> A positive distance indicates that the point is above the
  ///plane while a negative one indicates that it is below.
  ///</para>
  ///</remarks>
  public static double PointPlaneDistance(Vector3 pt, Vector4 plane) {
    return pt.dot(plane) + plane.w;
  }

  ///<summaryProject the point to the given plane.</summary>
  public static Vector3 PointProjectPlane(Vector3 pt, Vector4 plane) {
    final double d = -PointPlaneDistance(pt, plane);
    pt.x += plane.x * d; pt.y += plane.y * d; pt.z += plane.z * d;
    return pt;
  }

  ///<summaryProject the point to the given plane.</summary>
  public static Vector3 PointProjectPlane(Vector3 pt, Vector4 plane, Vector3 res) {
    return PointProjectPlane(res.set(pt), plane);
  }

  ///<summaryProject a point to a plane along the given direction.</summary>
  public static Vector3 PointProjectPlaneAlongDir(Vector3 pt, Vector4 plane, Vector3 dir) {
    final double nDotDir = dir.dot(plane);
    if (0 != Math.abs(nDotDir)) {
      // Get parametric step along light direction to plane.
      final double t = -PointPlaneDistance(pt, plane) / nDotDir;
      if (t > 0) {
        // Add distance along light ray to projection.
        pt.x += dir.x * t; pt.y += dir.y * t; pt.z += dir.z * t;
      }
      return pt;
    }
    else {
      // Direction is parallel to the plane.
      return pt;
    }
  }
}

