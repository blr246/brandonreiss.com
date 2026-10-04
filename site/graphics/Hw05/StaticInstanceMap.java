/*
 * StaticInstanceMap.java
 *
 * Store references to canonical vertex and face data for geometric
 * primitives. These data are shared to avoid unnecessary allocation and
 * computation when creating a basic shape.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;
import java.util.Map;
import java.util.HashMap;

///<summary>Cache geometric primitives.</summary>
///<remarks>
///<para>Geometric primitives may share vertices and faces so long as no
///custom modifications are required. To make modifications, one may simply
///break the instancing link by copying the face and vertex data.</para>
///</remarks>
public class StaticInstanceMap {
  ///<summary>Simple storage for vertices and faces.</summary>
  public static class Record {
    public Vector3[] vertices = null;
    public int[][] faces = null;
    public Record(Vector3[] vertices_, int[][] faces_) {
      vertices = vertices_;
      faces = faces_;
    }
  }

  ///<summary>Link instance name to a record.</summary>
  private static Map<String, Record> map = new HashMap<String, Record>();

  ///<summary>Get a record by instance name.</summary>
  public static Record get(String s) {
    return map.get(s);
  }

  ///<summary>Set a record by instance name.</summary>
  public static synchronized void put(String s, Vector3[] vertices, int[][] faces) {
    map.put(s, new Record(vertices, faces));
  }
}

