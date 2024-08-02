package org.image.viewer.core;

import org.image.viewer.tasks.*;
import org.image.viewer.advancers.*;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


public class Logic {

  private static final int NIMGS = 40; // number of images in cache
  private static final int LIMIT = NIMGS / 2;
  private static final int NTHREADS = 4;

  private final DirectoryReader reader;
  private final ConcurrentSkipListMap<Path, NamedImage> cache;
  
  private Path currentKey;
  private final AtomicInteger nFront;
  private final AtomicInteger nBack;  
  
  private final ExecutorService executor;
  private final ReadNextImageTask readNextImageTask;
  private final ReadPrevImageTask readPrevImageTask;
  
  private Advancer advancer;

  public Logic(Path inputPath) throws InterruptedException {
    reader = new DirectoryReader(inputPath);
    cache = new ConcurrentSkipListMap<>();
    
    executor = Executors.newFixedThreadPool(NTHREADS);
    readNextImageTask = new ReadNextImageTask(reader, cache);
    readPrevImageTask = new ReadPrevImageTask(reader, cache);

    nBack = new AtomicInteger();
    nFront = new AtomicInteger();
    
    Future<?> first = executor.submit(readNextImageTask);
    for(int i=0; i<NIMGS-1; i++){
      nFront.incrementAndGet();
      executor.submit(readNextImageTask);
    }
    try{
      first.get();    
    }
    catch(InterruptedException | ExecutionException e){
      org.image.viewer.util.MyLogger.severe(e);
      throw new Error(e);
    }
    currentKey = cache.firstKey();
    advancer = new ForwardAdvancer(this);
  }
  
  public void setAdvancer(Advancer advancer){
    this.advancer = advancer;
  }
  
  public synchronized NamedImage advance(){
    return advancer.advance();
  }
  
  public NamedImage getCurrentNamedImage() {
    return cache.get(currentKey);
  }
  
  public synchronized NamedImage forward() {
    if (cache.isEmpty()) { // no images found. cannot advance
      return null;
    }
    if (currentKey.equals(cache.lastKey())) { // current is the last image
      return null;
    }
    // advance in forward direction
    currentKey = cache.higherKey(currentKey);
    NamedImage nextImage = cache.get(currentKey);
    if(nextImage == null){
      currentKey = cache.lowerKey(currentKey);
      return null;
    }
    nBack.incrementAndGet();
    nFront.decrementAndGet();
    if (nFront.get() < LIMIT && reader.hasNext()) {
      executor.submit(readNextImageTask);
      nFront.incrementAndGet();
    }
    if(nBack.get() == LIMIT){
      Path key = cache.pollFirstEntry().getKey();
      reader.putPrev(key);
      nBack.decrementAndGet();
    }
    return nextImage;
  }


  public synchronized NamedImage backward() {
    if (cache.isEmpty()) { // no images found. cannot advance
      return null;
    }
    if (currentKey.equals(cache.firstKey())) { // current is the first image
      return null;
    }
    // advance in backward direction
    currentKey = cache.lowerKey(currentKey);
    NamedImage prevImage = cache.get(currentKey);
    if(prevImage == null){
      currentKey = cache.higherKey(currentKey);
      return null;
    }
    nBack.decrementAndGet();
    nFront.incrementAndGet();
    if (nBack.get() < LIMIT && reader.hasPrev()) {
      executor.submit(readPrevImageTask);
      nBack.incrementAndGet();
    }
    if(nFront.get() == LIMIT){
      Path key = cache.pollLastEntry().getKey();
      reader.putNext(key);
      nFront.decrementAndGet();
    }
    return prevImage;
  }
}

