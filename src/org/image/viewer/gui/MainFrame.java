package org.image.viewer.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 *
 * @author EvanStefan
 */
public class MainFrame implements Runnable {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 480;
  private static final String TITLE = "Image Viewer";

  private JFileChooser fileChooser;
  private JFrame frame;
  
  public MainFrame() { }
  
  private JMenuBar createMenuBar() {
    JMenuItem open = new JMenuItem("Open");
    open.addActionListener(new MenuItemHandler());
    JMenu folder = new JMenu("Folder");
    folder.add(open);

    JMenuBar bar = new JMenuBar();
    bar.add(folder);
    return bar;
  }

  @Override
  public void run() {
    fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    frame = new JFrame();
    frame.setLayout(new GridLayout());
    frame.setSize(WIDTH, HEIGHT);
    frame.setTitle(TITLE);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setJMenuBar(this.createMenuBar());
    frame.setVisible(true);    
  }
  
  private class MenuItemHandler implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e) {
      int val = fileChooser.showOpenDialog(frame);
      if (val == JFileChooser.APPROVE_OPTION) {
        SwingUtilities.invokeLater(new MainGui(
          fileChooser.getSelectedFile().toPath()
        ));          
      }
    }
  }

}
