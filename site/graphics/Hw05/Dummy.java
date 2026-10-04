/*
 * Dummy.java
 *
 * Invisible scene object.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

public class Dummy extends SceneObject {
  public Dummy() { visible = false; }
  @Override public void render(Renderer r) {}
}

