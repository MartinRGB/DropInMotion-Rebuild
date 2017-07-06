package org.progx.dropinmotion.motionblur;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class RealisticMotionBlurIcon extends BasicMotionBlurIcon {
    protected int frameCount;
    
    public RealisticMotionBlurIcon() {
        super();
        maxHistory = 5;
    }
    
    @Override
    public void startAnimation() {
        if (timer == null || !timer.isRunning()) {
            frameCount = 0;
            super.startAnimation();
        }
    }
    
    @Override
    protected void drawItem(Graphics2D g2) {
        int item_x;
        int item_y;

        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                              1.0f / (float) (maxHistory - 2));
        g2.setComposite(composite);

        if (history != null && historyCount > 0) {
            for (int i = maxHistory - historyCount; i < maxHistory; i++) {
                Point point = history[i];
                item_x = ((getWidth() - 200) / 2) + point.x - image.getWidth() / 2;
                item_y = point.y + (getHeight() / 2) - image.getHeight() / 2;
                
                g2.drawImage(image, null, item_x, item_y);
            }
        } else {
            g2.setComposite(AlphaComposite.SrcOver);
        }

        item_x = ((getWidth() - 200) / 2) + x - image.getWidth() / 2;
        item_y = y + (getHeight() / 2) - image.getHeight() / 2;
        
        g2.drawImage(image, null, item_x, item_y);
    }
    
    protected void drawItem2(Graphics2D g2) {
        int item_x;
        int item_y;

        if (history != null && historyCount > 0) {
            BufferedImage[] images = new BufferedImage[maxHistory];
            int[][] rasters = new int[images.length][];
    
            for (int i = maxHistory - historyCount; i < maxHistory; i++) {
                Point point = history[i];
                item_x = ((getWidth() - 200) / 2) + point.x - image.getWidth() / 2;
                item_y = point.y + (getHeight() / 2) - image.getHeight() / 2;
    
                BufferedImage temp = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = temp.createGraphics();
                setupGraphics(g2d);
                g2d.setClip(0, 0, getWidth(), getHeight());
                drawBackground(g2d);
                g2d.drawImage(image, null, item_x, item_y);
                g2d.dispose();
                images[i] = temp;
                rasters[i] = ((DataBufferInt) images[i].getRaster().getDataBuffer()).getData();
            }
    
            BufferedImage result = new BufferedImage(images[maxHistory - 1].getWidth(),
                                                     images[maxHistory - 1].getHeight(),
                                                     BufferedImage.TYPE_INT_RGB);
            int[] buffer = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
    
            for (int x = 0; x < result.getWidth(); x++) {
                for (int y = 0; y < result.getHeight(); y++) {
                    int r = 0;
                    int g = 0;
                    int b = 0;
                    
                    int offset = y * result.getWidth() + x;
                    
                    for (int i = maxHistory - historyCount; i < maxHistory; i++) {
                        int pixel = rasters[i][offset];
                        r += 0xFF & (pixel >> 16);
                        g += 0xFF & (pixel >>  8);
                        b += 0xFF & (pixel >>  0);
                    }
    
                    r /= historyCount;
                    g /= historyCount;
                    b /= historyCount;

                    buffer[offset] = r << 16 | g << 8 | b;
                }
            }
            
            g2.drawImage(result, null, 0, 0);
        } else {
            item_x = ((getWidth() - 200) / 2) + x - image.getWidth() / 2;
            item_y = y + (getHeight() / 2) - image.getHeight() / 2;
            
            g2.drawImage(image, null, item_x, item_y);
        }
    }
}
