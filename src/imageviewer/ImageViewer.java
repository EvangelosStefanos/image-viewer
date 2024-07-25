package imageviewer;

import gui.MainGui;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import util.MyLogger;

public class ImageViewer {

  /**
   * Path of the input file. Its first line must be a valid path to a directory
   * that contains images.
   */
  private static final String PATH = "input\\input.txt";

  private static String readFirstLine() throws IOException {
    return new BufferedReader(new FileReader(PATH)).readLine();
  }

  public static void main(String[] args) throws Exception {
    MyLogger.init();
    SwingUtilities.invokeLater(new MainGui(
        Paths.get(readFirstLine()))
    );
  }

}
