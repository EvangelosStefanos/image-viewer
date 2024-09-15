package org.image.viewer.util;

import java.io.IOException;
import java.util.logging.*;

public class MyLogger {

  private static final String PATH = "logs\\log.txt";
  private static final Logger LOGGER = Logger.getLogger(MyLogger.class.getName());

  private MyLogger() { }
  
  private static Logger get(){
    return LOGGER;
  }

  public static void init() throws IOException {
    System.setProperty("java.util.logging.SimpleFormatter.format", 
      "%1$tF %1$tT %4$s %2$s %5$s%6$s%n"); // https://stackoverflow.com/questions/194765/how-do-i-get-java-logging-output-to-appear-on-a-single-line/34229629#34229629
    FileHandler fh = new FileHandler(PATH);
    fh.setFormatter(new SimpleFormatter());

    Logger logger = get();
    logger.addHandler(fh);
    logger.info("MyLogger initialized.");
  }
  
  public static void info(String msg){
    get().info(msg);
  }
  
  public static void severe(Throwable thrown){
    get().log(Level.SEVERE, thrown.getMessage(), thrown);
  }

}
