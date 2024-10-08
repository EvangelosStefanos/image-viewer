package org.image.viewer.tasks;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentNavigableMap;
import javax.imageio.ImageIO;
import org.image.viewer.core.DirectoryReader;
import org.image.viewer.core.NamedImage;
import org.image.viewer.util.DownScaler;

/**
 * Acquires paths from a {@code DirectoryReader}, reads the images they
 * represent to memory and stores them in a {@code ConcurrentMap}, with their
 * paths used as keys.
 * 
 * <p> Paths are acquired in descending order.
 * 
 * @author EvanStefan
 */
public class ReadPrevImageTask implements Runnable {
  
  private final DirectoryReader reader;
  private final ConcurrentNavigableMap<Path, NamedImage> cache;
  private final DownScaler scaler;
  
  /**
   * @param directoryReader the specified ordered set that paths will be acquired from
   * @param cache the specified map that NamedImages will be added to after they are read
   */
  public ReadPrevImageTask(DirectoryReader directoryReader, ConcurrentNavigableMap<Path, NamedImage> cache){
    this.reader = directoryReader;
    this.cache = cache;
    this.scaler = new DownScaler();
  }
  
  /**
   * Acquire a path from the provided source and use it to create a
   * {@code NamedImage} which will be stored in the provided map, using the
   * file name associated with it as its key.
   * <br><br>
   * In the case that an image cannot be read from one of the acquired paths,
   * it is removed from the provided source and this {@code Runnable} 
   * submits itself to the provided {@code Executor}.
   */
  @Override
  public void run() {
    try{
      while(true){
        if(!reader.hasPrev()){
          org.image.viewer.util.MyLogger.info("Reader leftmost edge reached.");
          return;
        }
        Path source = reader.popPrev();
        BufferedImage image = ImageIO.read(source.toFile());
        // image = scaler.resizeImage(image);
        NamedImage namedImage = new NamedImage(source, image);
        if (namedImage.getImage() != null) {
          org.image.viewer.util.MyLogger.info("Loaded NamedImage: " + namedImage);
          cache.put(source, namedImage);
          return;
        }
        org.image.viewer.util.MyLogger.info("Image could not be loaded. Dropping path: "+source);
      }
    }
    catch(IOException e){
      org.image.viewer.util.MyLogger.severe(e);
      throw new Error(e);
    }
  }
  
}
