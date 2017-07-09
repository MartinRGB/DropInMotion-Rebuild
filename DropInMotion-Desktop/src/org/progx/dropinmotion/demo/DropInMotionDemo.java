package org.progx.dropinmotion.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.progx.dropinmotion.physics.DampingOscillatorEquation;
import org.progx.dropinmotion.physics.DropBouncerEquation;
import org.progx.dropinmotion.physics.BouncerEquation;
import org.progx.dropinmotion.physics.SpringEquartion;
import org.progx.dropinmotion.simulator.PhysicsSetupFrame;
import org.progx.dropinmotion.ui.HeaderPanel;

public class DropInMotionDemo extends JFrame {
    private DampingOscillatorEquation damping;
    private DropBouncerEquation dropbouncer;
    private BouncerEquation bouncer;
    private SpringEquartion spring;

    public DropInMotionDemo() throws HeadlessException {
        super("Drop in Motion");

        setupEquations();
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);
        add(buildButtonsPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private Component buildButtonsPanel() {
        JButton button;

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        panel.setBackground(Color.WHITE);

        panel.add(button = new JButton("Setup",
                                       new ImageIcon(getClass().getResource("images/stock_exec-16.png"))));
        Dimension preferredSize = button.getPreferredSize();
        button.addActionListener(new ActionListener() {
            private PhysicsSetupFrame setupFrame = null;

            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (setupFrame == null || !setupFrame.isDisplayable()) {
                            setupFrame = new PhysicsSetupFrame(damping, dropbouncer,spring,bouncer);
                            setupFrame.setVisible(true);
                        } else {
                            setupFrame.toFront();
                        }
                    }
                });
            }
        });
        
        panel.add(button = new JButton("Exit",
                                       new ImageIcon(getClass().getResource("images/stock_stop-16.png"))));
        button.setPreferredSize(preferredSize);
        button.addActionListener(new ActionListener() {
            // pure laziness
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JSeparator(), BorderLayout.NORTH);
        wrapper.add(panel);
        
        return wrapper;
    }

    private Component buildHeader() {
        ImageIcon icon = new ImageIcon(getClass().getResource("images/demo.png"));
        HeaderPanel header = new HeaderPanel(icon,
                                             "Drop in Motion",
                                             "<html><body><strike>Drag the icon around and behold the motion blur!</strike></body></html>",
                                             "Drop the icon and go into raptures over the animation!");
        return header;
    }

    private Component buildContentPanel() {
        PlaygroundPanel panel = new PlaygroundPanel(dropbouncer);
        return panel;
    }

    private void setupEquations() {
        damping = new DampingOscillatorEquation(1.0, 0.3, 0.058, 12.0, 0.0);
        dropbouncer = new DropBouncerEquation(1.0, 0.3, 0.058, 12.0, 0.0);
        spring = new SpringEquartion();
        bouncer = new BouncerEquation(1.0, 0.3, 0.058, 12.0, 0.0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DropInMotionDemo frame = new DropInMotionDemo();
                frame.setVisible(true);
            }
        });
    }
}
