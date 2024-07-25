package util;

/**
 *
 * @author EvanStefan
 */
public class Centerer {
  private int X;
  private int Y;

  public Centerer() {
    X = 0;
    Y = 0;
  }

  /**
   * @param width int > 0
   * @param height int > 0
   * @param widthViewport int > 0
   * @param heightViewport int > 0
   */
  public void center(int width, int height, int widthViewport, int heightViewport) {
    X = (widthViewport - width) / 2;
    Y = (heightViewport - height) / 2;    
  }

  public int getCenteredX() {
    return X;
  }

  public int getCenteredY() {
    return Y;
  }
}
