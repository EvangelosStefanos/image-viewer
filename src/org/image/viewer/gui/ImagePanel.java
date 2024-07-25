package org.image.viewer.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.image.viewer.util.Centerer;
import org.image.viewer.util.DownScaler;

/**
 * A {@code JPanel} that paints a {@code BufferedImage}. The image is downscaled
 * if its dimensions are bigger than the panels and centered such that it appears
 * in the center of the panel.
 * @author EvanStefan
 */
public class ImagePanel extends JPanel {

  private BufferedImage image;
  private final DownScaler scaler;
  private final Centerer centerer;

  public ImagePanel(BufferedImage bufferedImage) {
    super();
    scaler = new DownScaler();
    centerer = new Centerer();
    image = bufferedImage;
  }

  public void setImg(BufferedImage bufferedImage) {
    image = bufferedImage;
  }

  @Override
  public void paint(Graphics g) {
    if (image == null) {
      return;
    }

    scaler.downScale(image.getWidth(), image.getHeight(), getWidth(), getHeight());
    centerer.center(scaler.getScaledWidth(), scaler.getScaledHeight(), getWidth(), getHeight());

    g.drawImage(
      image, 
      centerer.getCenteredX(), centerer.getCenteredY(), 
      scaler.getScaledWidth(), scaler.getScaledHeight(), 
      null
    );
  }
}
