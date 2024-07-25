package gui;

import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import javax.swing.*;
import imageviewer.Logic;
import imageviewer.NamedImage;
import java.util.concurrent.ExecutionException;
import util.MyLogger;
import util.Util.Direction;

public class MainGui implements Runnable {

  private final int frameWidth = 1280;
  private final int frameHeight = 720;
  private final int timerDelay = 1000;
  private JFrame frame;
  private ImagePanel imagePanel;
  private Timer timer;
  private final Logic logic;
  private boolean canExecute;

  public MainGui(Path inputPath) throws Exception {
    logic = new Logic(inputPath);
    canExecute = true;
  }

  @Override
  public void run() {
    frame = new JFrame();
    frame.setLayout(new GridLayout());

    imagePanel = new ImagePanel(logic.getCurrentNamedImage().getImage());
    frame.add(imagePanel);

    frame.addKeyListener(new KeyHandler());

    frame.setSize(frameWidth, frameHeight);
    frame.setTitle(logic.getCurrentNamedImage().getName());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    timer = new Timer(timerDelay, (ActionEvent e) -> {
      tryExecute(Direction.FORWARD);
    });
    // t.start();
  }
  
  private boolean tryExecute(Direction direction){
    if(!canExecute){
      return false;
    }
    canExecute = false;
    new MyWorker(direction).execute();
    return true;
  }

  private class KeyHandler extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_UP -> timer.start();
        case KeyEvent.VK_DOWN -> timer.stop();
        case KeyEvent.VK_LEFT -> tryExecute(Direction.BACKWARD);
        case KeyEvent.VK_RIGHT -> tryExecute(Direction.FORWARD);
        default -> { }
      }
    }
  }

  private class MyWorker extends SwingWorker<NamedImage, Void> {
    private final Direction direction;
    public MyWorker(Direction direction) {
      this.direction = direction;
    }

    @Override
    public NamedImage doInBackground() {
      if (direction == Direction.FORWARD) {
        return logic.forward();
      }
      if (direction == Direction.BACKWARD) {
        return logic.backward();
      }
      // should never happen
      return null;
    }

    @Override
    public void done() {
      try {
        NamedImage image = get();
        // was the advance successful?
        if (image == null) {
          // the advance was not successful
          // nothing changed, return
          canExecute = true;
          return;
        }
        // the advance was successful
        // update image and title
        MyLogger.info("Showing image: " + image.getName());
        imagePanel.setImg(image.getImage());
        frame.setTitle(image.getName());
        frame.repaint();
      }
      catch (InterruptedException | ExecutionException e) {
        MyLogger.severe(e);
        throw new Error(e);
      }
      canExecute = true;
    }
  }
}
