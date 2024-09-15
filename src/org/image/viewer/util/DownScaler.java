package org.image.viewer.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author EvanStefan
 */
public class DownScaler {
  
  private int scaledWidth;
  private int scaledHeight;

  public DownScaler() {
    scaledWidth = 0;
    scaledHeight = 0;
  }
  
  /**
   * Downscale dimensions to fit the specified viewport while preserving the
   * aspect ratio.
   * 
   * @see <a href="https://stackoverflow.com/questions/3971841/how-to-resize-images-proportionally-keeping-the-aspect-ratio/14731922#14731922">https://stackoverflow.com/questions/3971841/how-to-resize-images-proportionally-keeping-the-aspect-ratio/14731922#14731922</a>
   * 
   * @param width int > 0
   * @param height int > 0
   * @param widthViewport int > 0
   * @param heightViewport int > 0
   */
  public void downScale(int width, int height, int widthViewport, int heightViewport) {

    double ratio = Math.min(
      (double) widthViewport / width,
      (double) heightViewport / height
    );

    scaledWidth = width;
    scaledHeight = height;

    int w = (int) (width * ratio);
    int h = (int) (height * ratio);

    if(w <= width && h <= height){
      scaledWidth = w;
      scaledHeight = h;
    }
  }
  
  public int getScaledWidth() {
    return scaledWidth;
  }
  
  public int getScaledHeight() {
    return scaledHeight;
  }
  
  public BufferedImage resizeImage(BufferedImage originalImage) throws IOException {
    downScale(originalImage.getWidth(), originalImage.getHeight(), 1920, 1080);
    int targetWidth = getScaledWidth();
    int targetHeight = getScaledHeight();
    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = resizedImage.createGraphics();
    /*
    graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    */
    graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
    graphics2D.dispose();
    return resizedImage;
  }

}
