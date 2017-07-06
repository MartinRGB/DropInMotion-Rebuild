package org.progx.dropinmotion.motionblur;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;

public class BouncingIcon extends JComponent implements ActionListener {
    protected Color backgroundColor = Color.WHITE;
    protected BufferedImage image;
    protected Timer timer;
    protected int duration;
    protected long start;
    protected int x;
    protected int y;
    
    public BouncingIcon() {
        duration = 1000;
        try {
            image = ImageIO.read(getClass().getResource("images/item.png"));
        } catch (Exception e) { }
    }
    
    public void startAnimation() {
        if (timer == null || !timer.isRunning()) {
            timer = new Timer(1000 / 60, this);
            start = System.currentTimeMillis();
            timer.start();
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed >= duration) {
            x = 200;
            timer.stop();
        } else {
            x = (int) (((double) elapsed / (double) duration) * 200.0);
        }
        y = (int) (Math.sin(x / 20) * 8);
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;

        setupGraphics(g2);
        drawBackground(g2);
        drawItem(g2);
    }
    
    protected void drawItem(Graphics2D g2) {
        int item_x = ((getWidth() - 200) / 2) + x - image.getWidth() / 2;
        int item_y = y + (getHeight() / 2) - image.getHeight() / 2;
        g2.drawImage(image, null, item_x, item_y);
    }

    protected void drawBackground(Graphics2D g2) {
        g2.setColor(backgroundColor);
        g2.fill(g2.getClip());
    }

    protected void setupGraphics(Graphics2D g2) {
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth() + 200,
                             image.getHeight() * 2);
    }
}
