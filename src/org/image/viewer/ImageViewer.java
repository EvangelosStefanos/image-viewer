package org.image.viewer;

import javax.swing.SwingUtilities;
import org.image.viewer.gui.MainFrame;
import org.image.viewer.util.MyLogger;

public class ImageViewer {

  public static void main(String[] args) throws Exception {
    MyLogger.init();
    SwingUtilities.invokeLater(new MainFrame());
  }

}
