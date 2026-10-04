/*
 * MatrixStack.java
 *
 * A matrix stack used to manage hierarchical scene transformations.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

///<summaryA simple matrix stack.</summary>
///<remarks>
///<paraThe matrix stack grows automatically on overflow.</para>
///</remarks>
public class MatrixStack {
  private int capacity = 20;
  private Matrix4x4[] matrices = null;
  private int size = 1;

  public MatrixStack() {
    matrices = new Matrix4x4[capacity];
    for (int mIdx = 0; mIdx < capacity; ++mIdx) {
      matrices[mIdx] = Matrix4x4.CreateMatrixIdentity();
    }
  }

  ///<summaryPush matrix to the top of the stack.</summary>
  public void push(Matrix4x4 m) {
    if (++size >= capacity) {
      int capacityNew = (int)(capacity * 1.5);
      Matrix4x4[] matricesNew = new Matrix4x4[capacityNew];
      System.arraycopy(matrices, 0, matricesNew, 0, capacity);
      for (int mIdx = capacity; mIdx < capacityNew; ++mIdx) {
        matricesNew[mIdx] = new Matrix4x4();
      }
      capacity = capacityNew;
      matrices = matricesNew;
    }
    assert(size < capacity);
    assert(null != matrices[size - 1]);
    matrices[size - 1].set(matrices[size - 2]).mul(m);
  }

  ///<summaryPop the top of the stack.</summary>
  public void pop() {
    --size;
    assert(size >= 0);
  }

  ///<summaryGet stack size.</summary>
  public int size() {
    return size;
  }

  ///<summaryAccess top of stack.</summary>
  ///<remarks>
  ///<para> The reference returned by this function is valid only until
  ///additional calls are made to Push() and Pop(). Clients should
  ///copy this data when it is accessed.
  ///</para>
  ///</remarks>
  public Matrix4x4 top() {
    return matrices[size - 1];
  }
}

