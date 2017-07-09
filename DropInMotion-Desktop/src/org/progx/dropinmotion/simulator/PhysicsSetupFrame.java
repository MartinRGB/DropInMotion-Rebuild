package org.progx.dropinmotion.simulator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.progx.dropinmotion.physics.DampingOscillatorEquation;
import org.progx.dropinmotion.physics.DropBouncerEquation;
import org.progx.dropinmotion.physics.BouncerEquation;
import org.progx.dropinmotion.physics.SpringEquartion;
import org.progx.dropinmotion.ui.HeaderPanel;

public class PhysicsSetupFrame extends JFrame {
    private DampingOscillatorEquation damping;
    private DropBouncerEquation dropbouncer;
    private SpringEquartion spring;
    private BouncerEquation bouncer;

    public PhysicsSetupFrame(DampingOscillatorEquation damping,
                             DropBouncerEquation dropbouncer, SpringEquartion spring, BouncerEquation bouncer) throws HeadlessException {
        super("Drop in Motion - Physics Setup");
        this.damping = damping;
        this.dropbouncer = dropbouncer;
        this.spring = spring;
        this.bouncer = bouncer;

        add(buildHeader(), BorderLayout.NORTH);
        add(buildControlPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private Component buildHeader() {
        ImageIcon icon = new ImageIcon(getClass().getResource("images/simulator.png"));
        HeaderPanel header = new HeaderPanel(icon,
                                             "Physics Setup",
                                             "Fine-tune physics using the controls on the right.",
                                             "Check the result with the simulator at the bottom of the window.");
        return header;
    }

    private Component buildControlPanel() {
        return new EquationsControlPanel(damping, dropbouncer,spring,bouncer);
    }
}
