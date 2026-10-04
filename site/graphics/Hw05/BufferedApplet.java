// Ken Perlin 2011-2012. All Rights Reserved.
//
// THIS CLASS HANDLES DOUBLE BUFFERING FOR YOU, SO THAT THE IMAGE
// DOESN'T FLICKER WHILE YOU'RE RENDERING IT.  YOU DON'T REALLY
// NEED TO WORRY ABOUT THIS TOO MUCH, AND YOU'LL PROBABLY NEVER NEED
// TO CHANGE IT.  IT'S REALLY JUST USEFUL LOW LEVEL PLUMBING.

import java.awt.*;

public abstract class BufferedApplet extends java.applet.Applet implements Runnable
{
  // YOU MUST DEFINE A METHOD TO RENDER THE APPLET

  public abstract void render(Graphics g);

  // A BACKGROUND THREAD CALLS REPAINT EVERY 30 MILLISECONDS,

  public void start() { if (t == null) (t = new Thread(this)).start(); }
  public void run()   {
    try {
      final double tFrame = 1.0 / fpsTarget;
      while (true) {
        double tStart = getTime();
        repaint();
        final double tElapsed = getTime() - tStart;
        final int sleepMs = (int)((tFrame - tElapsed) * 1000.0);
        if (sleepMs > 0) {
          t.sleep(sleepMs);
        }
      }
    }
    catch(Exception e){ }
  }

  // WHICH CALLS UPDATE, WHICH CALLS YOUR RENDER METHOD.
  // THE IMAGE YOU'VE RENDERED IS THEN COPIED TO THE SCREEN.

  public void update(Graphics g) {
    if (width != getWidth() || height != getHeight()) {
      image = createImage(width = getWidth(), height = getHeight());
      buffer = image.getGraphics();
    }
    render(buffer);
    g.drawImage(image,0,0,this);
  }

  /// <summary>Get time in seconds since this instance was created.</summary>
  public double getTime() {
    return (System.currentTimeMillis() / 1000.0) - startTime;
  }

  private Thread t;
  private Image image;
  private Graphics buffer;
  private int width = 0, height = 0; 
  private final double startTime = System.currentTimeMillis() / 1000.0;
  private final int fpsTarget = 30;
}
