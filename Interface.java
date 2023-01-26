import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import java.beans.EventHandler;

import java.util.ArrayList;

public class Interface {
  // an ArrayList of MessageInfos
  public static ArrayList<MessageInfo> history = new ArrayList<>();
  public static Client client = new Client();
  
  public static void main(String[] args) throws Exception {
    history.add(new MessageInfo("Hello", (byte) 0));

		client.startConnection("", 4352); //put the server ip address in the quotes

    int width = 470;
    Dimension d2 = new Dimension(width, 300); //JFrame
    Dimension d1 = new Dimension(width-20, 300-50); //JPanel

    JFrame frame = new JFrame("Wall");

    JPanel chatPanel = new JPanel(new BorderLayout());
    chatPanel.setSize(d1);
    chatPanel.setMaximumSize(d1);
    chatPanel.setMinimumSize(d1);
    chatPanel.setPreferredSize(d1);
    chatPanel.setBackground(new Color(65, 60, 75));

    JFrame chatWindow = frame;
    chatWindow.setSize(d2);
    chatWindow.setMaximumSize(d2);
    chatWindow.setMinimumSize(d2);
    chatWindow.setPreferredSize(d2);
    chatWindow.setVisible(true);
    //chatWindow.setResizable(false);
    chatWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    JPanel typingAreaPanel = new JPanel(new FlowLayout());
    Dimension f = new Dimension(width-20, 50);
    typingAreaPanel.setSize(f);
    typingAreaPanel.setMaximumSize(f);
    typingAreaPanel.setMinimumSize(f);
    typingAreaPanel.setPreferredSize(f);
    typingAreaPanel.setBackground(new Color(95, 90, 105));
    //typingAreaPanel.setBackground(Color.ORANGE);

    ImageIcon icon = new ImageIcon("button.png");
    JButton sendButton = new JButton();
    Dimension g = new Dimension(70, 40);
    sendButton.setIcon(icon);

    sendButton.setSize(g);
    sendButton.setMaximumSize(g);
    sendButton.setMinimumSize(g);
    sendButton.setPreferredSize(g);
    sendButton.setBackground(Color.blue);
    sendButton.setForeground(Color.blue);
    sendButton.setOpaque(false);
    sendButton.setBorderPainted(false);
    sendButton.setContentAreaFilled(false);
    sendButton.setBorder(null); 

    
   
      


    JTextField textField = new JTextField();
    Dimension e = new Dimension(370, 40);
    textField.setSize(e);
    textField.setMaximumSize(e);
    textField.setMinimumSize(e);
    textField.setPreferredSize(e);
    textField.setBackground(new Color(95, 90, 105));
    textField.setForeground(Color.WHITE);
    //textField.setBackground(Color.red);
    textField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '\n') {
          try {
            if (!textField.getText().trim().isEmpty()) {
              client.sendChat(textField.getText());
              textField.setText("");
            }
          } catch (IOException err) {
          }
        }
      }
      @Override public void keyPressed(KeyEvent e) { }
      @Override public void keyReleased(KeyEvent e) { }
    });
    sendButton.addActionListener(new ActionListener() {
    
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            if (!textField.getText().trim().isEmpty()) {
              client.sendChat(textField.getText());
              textField.setText("");
            }
          } catch (IOException err) {
          }
        }
    });
    

    typingAreaPanel.add(textField);
    typingAreaPanel.add(sendButton);

    chatPanel.add(typingAreaPanel, BorderLayout.SOUTH);
    chatWindow.setContentPane(chatPanel);
    TextWall wallOfText = new TextWall(history);
    chatWindow.add(wallOfText);
    wallOfText.addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent event) {
        wallOfText.scroll(event.getScrollAmount() * event.getUnitsToScroll());
        frame.repaint();
      }
    });
    
    frame.setVisible(true);
    int prevPending = 0;
    while (true) {
      client.tick();
      ArrayList<MessageInfo> infos = client.getNewMessages();
      synchronized (infos) {
        synchronized (history) {
            history.addAll(infos);
            if (!infos.isEmpty())
              frame.repaint();
        }
        infos.clear();
      }
      int pending = client.countPending();
      if (pending != prevPending) {
        prevPending = pending;
        frame.repaint();
      }
    }
  }
}
