import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.geom.Dimension2D;

import java.awt.Color;

import java.util.ArrayList;

public class TextWall extends JComponent {
  public ArrayList<MessageInfo> history;
  double scrollValue = 0;

   public TextWall(ArrayList<MessageInfo> history) {
      this.history = history;
   }
  
   public void paint(Graphics wallGraphics) {
      wallGraphics.setFont(new Font("TimesRoman", Font.PLAIN, 15));
      FontMetrics currentFont = wallGraphics.getFontMetrics();
      int xPos;
      int yPos;
      int count = history.size();
      int counter = 0;
      for (int i = count - 1; i >= 0; i--) {
         String text = history.get(i).message;
         xPos = 10;
         yPos = (int) (getHeight() - (counter * 35 + 45) - scrollValue);
         int[] bounds = new int[]{0, 0, currentFont.stringWidth(text), currentFont.getHeight()};
         wallGraphics.setColor(new Color(214, 191, 209)); // why does this color look somewhat decent
         if (history.get(i).messageStatus != 0) {
            xPos = getWidth() - bounds[2] - 30;
            wallGraphics.setColor(new Color(224, 166, 210)); // why does this color look somewhat decent
            if (history.get(i).messageStatus == -1) {
               wallGraphics.setColor(new Color(200, 0, 150));
            }
         }
         wallGraphics.fillRoundRect(xPos, yPos, bounds[2] + 20, bounds[3] + 10, 10, 10);
         wallGraphics.setColor(new Color(0,0,0));
         wallGraphics.drawRoundRect(xPos, yPos, bounds[2] + 20, bounds[3] + 10, 10, 10);
         if (history.get(i).messageStatus == -1) {
            wallGraphics.setColor(new Color(255, 255, 255));
         }
         wallGraphics.drawString(text, xPos + 10, yPos + 20);
         
         counter += 1;
      }
   }

  public void scroll(double amount) {
    int count = history.size();
    double minScrollValue = count * -35 + getHeight()/2.0;
    double maxScrollValue = 10;
    if (((scrollValue + amount * 3) > minScrollValue) && ((scrollValue + amount * 3) < maxScrollValue)) {
      scrollValue += amount * 3;
    }
    else {
        if (scrollValue <= minScrollValue/2.0) {
            scrollValue = minScrollValue;
        } else {
            scrollValue = maxScrollValue;
        }
    }
  }
}