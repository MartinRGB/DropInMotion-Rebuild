package org.progx.dropinmotion.motionblur;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Test extends JFrame {
    private class MovieComponent extends JComponent {
        private int blurness = 10;
        private int currentFrame = 0;
        private int end = 440;
        private int frameToShow = -1;
        private List<Image> pictures = new ArrayList<Image>(end / blurness);
        
        private MovieComponent() {
            new Thread(new Runnable() {
                public void run() {
                    currentFrame = 0;
                    BufferedImage[] images = new BufferedImage[blurness];
                    byte[][] rasters = new byte[images.length][];

                    while (currentFrame < end) {
                        BufferedImage result = null;
                        for (int i = 0; i < images.length; i++) {
                            try {
                                images[i] = ImageIO.read(new File("images/terrain" + (currentFrame + i + 1) + ".jpg"));
                                rasters[i] = ((DataBufferByte) images[i].getRaster().getDataBuffer()).getData();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        currentFrame += images.length;
                        
                        result = new BufferedImage(images[0].getWidth(),
                                                   images[0].getHeight(),
                                                   BufferedImage.TYPE_INT_RGB);
                        int[] buffer = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
    
                        for (int x = 0; x < result.getWidth(); x++) {
                            for (int y = 0; y < result.getHeight(); y++) {
                                int r = 0;
                                int g = 0;
                                int b = 0;
                                
                                int offset = (y * result.getWidth() + x) * 3;
                                
                                for (byte[] raster: rasters) {
                                    int rr = raster[offset + 2];
                                    if (rr < 0) rr += 256;
                                    int rg = raster[offset + 1];
                                    if (rg < 0) rg += 256;
                                    int rb = raster[offset + 0];
                                    if (rb < 0) rb += 256;
    
                                    r += rr;
                                    g += rg;
                                    b += rb;
                                }
    
                                r /= rasters.length;
                                g /= rasters.length;
                                b /= rasters.length;
    
                                buffer[y * result.getWidth() + x] = r << 16 | g << 8 | b;
                            }
                        }
                        pictures.add(result);
                    }
                    button.setEnabled(true);
                }
            }).start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            if (frameToShow != -1) {
                g.drawImage(pictures.get(frameToShow), 0, 0, null);
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(267, 150);
        }
        
        private void loadNextFrame() {
            frameToShow++;
            if (frameToShow >= pictures.size()) {
                button.setEnabled(true);
                frameToShow = 0;
            }
            repaint();
        }
    }

    private MovieComponent movie;
    private JButton button;

    public Test() throws HeadlessException {
        super("Motion Blur Test");

        add(buildMotionBlur(), BorderLayout.CENTER);
        add(buildButton(), BorderLayout.SOUTH);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
    }
    
    private Component buildButton() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JSeparator(), BorderLayout.NORTH);
        panel.add(button = new JButton("Start"));
        button.setEnabled(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                Timer timer = new Timer(1000 / 25, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        movie.loadNextFrame();
                    }
                });
                timer.start();
            }
        });
        return panel;
    }

    private Component buildMotionBlur() {
        movie = new MovieComponent();
        return movie;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Test frame = new Test();
                frame.setVisible(true);
            }
        });
    }
}
