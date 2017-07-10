package org.progx.dropinmotion.simulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.progx.dropinmotion.equation.AbstractEquation;

public class EquationSimulator extends AbstractSimulator {
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_GROUND = Color.PINK;
    private static final float STROKE_GROUND = 2.0f;
    private static final int SIZE_HASH = 20;
    private static int GREEN = 0;
    private static int YELLOW = 1;
    private static int RED = 2;
    private static int PURPLE = 3;


    private BufferedImage image;

    public EquationSimulator(AbstractEquation equation,int colorInt) {
        super(equation);
        
        try {
            if(colorInt == GREEN){
                image = ImageIO.read(EquationSimulator.class.getResource("images/green.png"));
            }

            if(colorInt == YELLOW){
                image = ImageIO.read(EquationSimulator.class.getResource("images/yellow.png"));
            }

            if(colorInt == RED){
                image = ImageIO.read(EquationSimulator.class.getResource("images/red.png"));
            }

            if(colorInt == PURPLE){
                image = ImageIO.read(EquationSimulator.class.getResource("images/purple.png"));
            }

        } catch (Exception e) { }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;

        setupGraphics(g2);
        drawBackground(g2);
        drawGround(g2);
        drawItem(g2);
    }

    private void drawItem(Graphics2D g2) {
        double position = equation.compute(time * timeScale);
        double height = position * getHeight() / 2;

        int x = (getWidth() - image.getWidth()) / 2;
        int y = getHeight() * 3 / 4;

        y -= height;
        y -= image.getHeight() - 2;

        g2.drawImage(image, null, x, y);
    }

    private void drawGround(Graphics2D g2) {
        int width = getWidth() * 2 / 3;
        int x = (getWidth() - width) / 2;
        int y = getHeight() * 3 / 4;

        Stroke stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(STROKE_GROUND));
        g2.setColor(COLOR_GROUND);

//      绘制地平线
//        for (int i = 0; i < width / SIZE_HASH; i++) {
//            g2.drawLine(x + i * SIZE_HASH, y + SIZE_HASH,
//                        x + (i + 1) * SIZE_HASH, y + 1);
//        }
        g2.drawLine(x, y, x + width, y);
        
        g2.setStroke(stroke);
    }

    private void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(COLOR_BACKGROUND);
        g2.fill(g2.getClipBounds());
    }
        
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 240);
    }
}
