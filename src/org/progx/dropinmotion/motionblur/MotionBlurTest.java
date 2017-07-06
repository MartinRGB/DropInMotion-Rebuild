package org.progx.dropinmotion.motionblur;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.progx.dropinmotion.ui.HeaderPanel;

public class MotionBlurTest extends JFrame {
    private BouncingIcon[] bouncers = new BouncingIcon[3];
    
    public MotionBlurTest() throws HeadlessException {
        super("Motion Blur Test");

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCinemaScreen(), BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.EAST);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
    }

    private Component buildControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        
        JButton button;
        panel.add(button = new JButton("Start Animation"),
                  new GridBagConstraints(0, 0,
                                         1, 1,
                                         1.0, 0.0,
                                         GridBagConstraints.CENTER,
                                         GridBagConstraints.NONE, 
                                         new Insets(6, 6, 0, 6),
                                         0, 0));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (BouncingIcon bouncer: bouncers) {
                    if (bouncer != null) {
                        bouncer.startAnimation();
                    }
                }
            }
        });
        
        panel.add(Box.createVerticalGlue(),
                  new GridBagConstraints(0, panel.getComponentCount(),
                                         1, 1,
                                         1.0, 1.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.NONE, 
                                         new Insets(0, 0, 0, 0),
                                         0, 0));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.WEST);
        wrapper.add(panel);

        return wrapper;
    }

    private Component buildCinemaScreen() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBackground(Color.WHITE);

        panel.add(bouncers[0] = new BouncingIcon());
        panel.add(bouncers[1] = new BasicMotionBlurIcon());
        panel.add(bouncers[2] = new RealisticMotionBlurIcon());

        return panel;
    }

    private Component buildHeader() {
        ImageIcon icon = new ImageIcon(getClass().getResource("images/motion.png"));
        HeaderPanel header = new HeaderPanel(icon,
                                             "Motion Blur",
                                             "Show two different motion blur implementations.",
                                             "From top to bottom: no blur, basic blur and realistic blur.");
        return header;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MotionBlurTest frame = new MotionBlurTest();
                frame.setVisible(true);
            }
        });
    }
}
