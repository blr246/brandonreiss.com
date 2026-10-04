/*
 * MouseMaster.java
 *
 * Forward mouse events to implementations that need to receive them.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

///<summary>Forward mouse events to registered listeners.</summary>
public class MouseMaster implements MouseListener, MouseMotionListener {
  private Vector<MouseListener> mouseListeners = new Vector<MouseListener>(0, 10);
  private Vector<MouseMotionListener> mouseMotionListeners = new Vector<MouseMotionListener>(0, 10);

  public void addMouseListener(MouseListener listener) {
    mouseListeners.add(listener);
  }

  public void addMouseMotionListener(MouseMotionListener listener) {
    mouseMotionListeners.add(listener);
  }

  public void removeMouseListener(MouseListener listener) {
    mouseListeners.remove(listener);
  }

  public void removeMouseMotionListener(MouseMotionListener listener) {
    mouseMotionListeners.remove(listener);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    for (MouseMotionListener mml : mouseMotionListeners) {
      mml.mouseMoved(e);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    for (MouseMotionListener mml : mouseMotionListeners) {
      mml.mouseDragged(e);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    for (MouseListener ml : mouseListeners) {
      ml.mousePressed(e);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    for (MouseListener ml : mouseListeners) {
      ml.mouseReleased(e);
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    for (MouseListener ml : mouseListeners) {
      ml.mouseEntered(e);
    }
  }

  @Override
  public void mouseExited(MouseEvent e) {
    for (MouseListener ml : mouseListeners) {
      ml.mouseExited(e);
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    for (MouseListener ml : mouseListeners) {
      ml.mouseClicked(e);
    }
  }
}

