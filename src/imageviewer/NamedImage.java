package imageviewer;

import java.awt.image.BufferedImage;
import java.nio.file.Path;


/**
 * An object that contains a {@code BufferedImage} and the {@code Path} that it
 * was read from.
 * @author EvanStefan
 */
public class NamedImage {
  
  private final Path path;
  private final BufferedImage bufferedImage;

  public NamedImage(Path path, BufferedImage bufferedImage) {
    this.path = path;
    this.bufferedImage = bufferedImage;
  }
  
  public Path getPath(){
    return path;
  }
  
  public String getName(){
    return String.format(
      "%s | %d x %d", path.getFileName(), 
      bufferedImage.getWidth(), bufferedImage.getHeight()
    );
  }

  public BufferedImage getImage() {
    return bufferedImage;
  }

  @Override
  public String toString() {
    return getName() + " >>" + bufferedImage.toString() + "<<";
  }

}
