package org.image.viewer.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A ConcurrentSkipListSet that stores paths read from a specific directory.
 * Paths without a .jpg or .png extension are ignored.
 * @author EvanStefan
 */
public class DirectoryReader {
  
  private static final int LIMIT = 1000; // paths per page
  private final Path targetDirectoryPath; // path of a directory that contains image files
  private final AtomicLong currentPageId;
  private final AtomicLong maxPageId;
  private final ConcurrentSkipListSet<Path> ahead;
  private final ConcurrentSkipListSet<Path> behind;

  /**
   * Creates a DirectoryReader that will contain up to {@code LIMIT} paths,
   * read from the {@code targetDirectoryPath} directory.
   * @param targetDirectoryPath
   */
  public DirectoryReader(Path targetDirectoryPath) {
    super();
    this.targetDirectoryPath = targetDirectoryPath;
    currentPageId = new AtomicLong();
    maxPageId = new AtomicLong(getPageCount()-1);
    ahead = new ConcurrentSkipListSet<>();
    behind = new ConcurrentSkipListSet<>(Comparator.reverseOrder());
    this.setPageAhead(currentPageId.get());
  }
  
  /**
   * Returns true if the specified path represents an image file. 
   * More specifically, returns true if {@code path} has a
   * .jpg or .png extension (case insensitive).
   * @param path The specified path that will be tested
   * @return true if {@code path} represents an image file
   */
  private boolean isImage(Path path){
    String lowercased = path.toString().toLowerCase();
    return 
      lowercased.endsWith(".jpg") || 
      lowercased.endsWith(".png");
  }
  
  private long getPageCount() {
    try(Stream<Path> stream = Files.list(targetDirectoryPath)){
      long pathCount = stream.filter(this::isImage).count();
      if(pathCount == 0){
        String msg = "Empty directories are not supported.";
        org.image.viewer.util.MyLogger.info(msg);
        throw new Error(msg);
      }
      long pageCount = Math.ceilDiv(pathCount, LIMIT);
      org.image.viewer.util.MyLogger.info(String.format("%d valid paths / %d pages / up to %d paths per page.", pathCount, pageCount, LIMIT));
      return pageCount;
    }
    catch(IOException e){
      org.image.viewer.util.MyLogger.severe(e);
      throw new Error(e);
    }
  }
  
  private ArrayList<Path> loadPage(long pageId) {
    org.image.viewer.util.MyLogger.info("Loading page: " + pageId);
    try(Stream<Path> stream = Files.list(targetDirectoryPath)){
      return stream
        .filter(this::isImage)
        .skip(pageId * LIMIT)
        .limit(LIMIT)
        .collect(Collectors.toCollection(ArrayList::new));
    }
    catch(IOException e){
      org.image.viewer.util.MyLogger.severe(e);
      throw new Error(e);
    }
  }
  
  private void setPageAhead(long pageId){
    ahead.addAll(loadPage(pageId));
    behind.clear();
  }
  
  private void setPageBehind(long pageId){
    ahead.clear();
    behind.addAll(loadPage(pageId));
  }
  
  private boolean canLoadNextPage(){
    boolean hasNextPage = currentPageId.get() < maxPageId.get();
    boolean aheadEmpty = ahead.isEmpty();
    return hasNextPage && aheadEmpty;
  }
  
  private boolean canLoadPrevPage(){
    boolean hasPrevPage = currentPageId.get() > 0;
    boolean behindEmpty = behind.isEmpty();
    return hasPrevPage && behindEmpty;
  }
  
  private synchronized void tryLoadNextPage(){
    if(!canLoadNextPage()){
      return;
    }
    setPageAhead(currentPageId.incrementAndGet());
  }
  
  private synchronized void tryLoadPrevPage(){
    if(!canLoadPrevPage()){
      return;
    }
    setPageBehind(currentPageId.decrementAndGet());
  }
  
  public Boolean hasNext(){
    return !ahead.isEmpty() || currentPageId.get() < maxPageId.get();
  }
  
  public Boolean hasPrev(){
    return !behind.isEmpty() || currentPageId.get() > 0;
  }
  
  public Boolean putNext(Path p){
    return ahead.add(p);
  }
  
  public Boolean putPrev(Path p){
    return behind.add(p);
  }
  
  public Path popNext(){
    tryLoadNextPage();
    return ahead.pollFirst();
  }
  
  public Path popPrev(){
    tryLoadPrevPage();
    return behind.pollFirst();
  }

}
