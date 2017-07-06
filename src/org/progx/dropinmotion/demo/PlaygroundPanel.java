package org.progx.dropinmotion.demo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.progx.dropinmotion.equation.AbstractEquation;
import org.progx.dropinmotion.shadow.ShadowFactory;
import org.progx.dropinmotion.simulator.AbstractSimulator;

public class PlaygroundPanel extends AbstractSimulator {
    private static final int DELAY_ANIMATION = 1000;
    
    private static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    private static final Color FOREGROUND_COLOR = new Color(0xC0C0C0);

    private static final int MARK_SPACING_X = 40;
    private static final int MARK_SPACING_Y = 40;

    private BufferedImage image;
    private BufferedImage shadow;
    
    private float angle = 90;
    private int distance = 20;

    // cached values for fast painting
    private int distance_x = 0;
    private int distance_y = 0;
    
    private int position_x = 0;
    private int position_y = 0;
    
    private boolean isDragging = false;
    
    private int delta_x;
    private int delta_y;
    
    public PlaygroundPanel(AbstractEquation equation) {
        super(equation);
        setTime(1.0);
        
        try {
            image = ImageIO.read(PlaygroundPanel.class.getResource("images/icon.png"));
            ShadowFactory factory = new ShadowFactory(5, 0.5f, Color.BLACK);
            shadow = factory.createShadow(image);
            
            Dimension size = getPreferredSize();
            position_x = (int) ((size.width - image.getWidth() / 2.0) / 2.0);
            position_y = (int) ((size.height - image.getHeight() / 2.0) / 2.0);
        } catch (Exception e) { }
        
        addMouseListener(new IconPicker());
        addMouseMotionListener(new IconDragger());
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(520, 320);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!isVisible()) {
            return;
        }
        
        Graphics2D g2 = setupGraphics(g);
        drawBackground(g2);
        drawItem(g2);
    }

    private void drawItem(Graphics2D g2) {
        double position = equation.compute(time * timeScale);

        int width = (int) (shadow.getWidth() / 2 * (1.0 + position));
        int height = (int) (shadow.getHeight() / 2 * (1.0 + position));

        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   1.0f - (0.5f * (float) position)));

        int x = (int) (position_x - delta_x * position);
        int y = (int) (position_y - delta_y * position);
        
        computeShadowPosition((position * distance) + 1.0);
        g2.drawImage(shadow,
                     x + distance_x, y + distance_y,
                     width, height, null);
        
        g2.setComposite(composite);
        
        width = (int) (image.getWidth() / 2 * (1.0 + position));
        height = (int) (image.getHeight() / 2 * (1.0 + position));

        g2.drawImage(image, x, y, width, height, null);
    }
    
    private void computeShadowPosition(double distance) {
        double angleRadians = Math.toRadians(angle);
        distance_x = (int) (Math.cos(angleRadians) * distance);
        distance_y = (int) (Math.sin(angleRadians) * distance);
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(BACKGROUND_COLOR);
        g2.fill(g2.getClip());
        drawMarks(g2);
    }
    
    private void drawMarks(Graphics2D g2) {
        g2.setColor(FOREGROUND_COLOR);
        for (int x = MARK_SPACING_X; x < getWidth(); x += MARK_SPACING_X) {
            for (int y = MARK_SPACING_Y; y < getHeight(); y += MARK_SPACING_Y) {
                g2.drawLine(x - 2, y, x + 2, y);
                g2.drawLine(x, y - 2, x, y + 2);
            }
        }
    }

    private Graphics2D setupGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        return g2;
    }
    
    private void startAnimation() {
        Timer animation = new Timer(1000 / 60, new Animation());
        animation.start();
    }
    
    private class Animation implements ActionListener {
        private long start;

        private Animation() {
            start = System.currentTimeMillis();
        }

        public void actionPerformed(ActionEvent e) {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > DELAY_ANIMATION * timeScale) {
                ((Timer) e.getSource()).stop();
                setTime(1.0);
                isDragging = false;
            } else {
                double time = (double) elapsed / (double) (DELAY_ANIMATION * timeScale);
                setTime(time);
            }
        }
    }

    private class IconPicker extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && hasMouseHit(e) && !isDragging) {
                delta_x = e.getX() - position_x;
                delta_y = e.getY() - position_y;
                setTime(0.0);
                isDragging = true;
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && isDragging) {
                startAnimation();
            }
        }



        private boolean hasMouseHit(MouseEvent e) {
            return e.getX() >= position_x && e.getX() < position_x + image.getWidth() / 2 &&
                   e.getY() >= position_y && e.getY() < position_y + image.getHeight() / 2;
        }
    }

    private class IconDragger extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && isDragging) {
                position_x = e.getX() - delta_x;
                position_y = e.getY() - delta_y;
                repaint();
            }
        }
    }
}
