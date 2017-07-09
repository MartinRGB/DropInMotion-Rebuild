package org.progx.dropinmotion.motionblur;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;

public class BasicMotionBlurIcon extends BouncingIcon {
    protected int maxHistory = 5;
    protected Point[] history;
    protected int historyCount;
    
    @Override
    public void startAnimation() {
        if (timer == null || !timer.isRunning()) {
            history = new Point[maxHistory];
            historyCount = -1;
            super.startAnimation();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        long elapsed = System.currentTimeMillis() - start;
        
        if (historyCount == -1) {
            historyCount++;
        } else {
            for (int i = 0; i < maxHistory - 1; i++) {
               history[i] = history[i + 1];
            }
            history[maxHistory - 1] = new Point(x, y);
            if (historyCount < maxHistory) {
                historyCount++;
            }
        }
        
        if (elapsed >= duration) {
            x = 200;
            historyCount = 0;
            timer.stop();
        } else {
            x = (int) (((double) elapsed / (double) duration) * 200.0);
        }
        y = (int) (Math.sin(x / 20) * 8);
        repaint();
    }
    
    @Override
    protected void drawItem(Graphics2D g2) {
        int item_x;
        int item_y;
        
        if (history != null && historyCount > 0) {
            for (int i = maxHistory - historyCount; i < maxHistory; i++) {
                Point point = history[i];
                item_x = ((getWidth() - 200) / 2) + point.x - image.getWidth() / 2;
                item_y = point.y + (getHeight() / 2) - image.getHeight() / 2;
                
                AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                                      i / (float) maxHistory / 2.0f);
                g2.setComposite(composite);
                g2.drawImage(image, null, item_x, item_y);
            }
        }
        
        item_x = ((getWidth() - 200) / 2) + x - image.getWidth() / 2;
        item_y = y + (getHeight() / 2) - image.getHeight() / 2;
        
        g2.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(image, null, item_x, item_y);
    }
}
