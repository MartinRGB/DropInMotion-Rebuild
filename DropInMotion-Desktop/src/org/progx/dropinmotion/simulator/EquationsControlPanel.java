package org.progx.dropinmotion.simulator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.progx.dropinmotion.equation.EquationDisplay;
import org.progx.dropinmotion.physics.*;


public class EquationsControlPanel extends JPanel implements PropertyChangeListener {
    private static final int DELAY_ANIMATION = 1000;

    private EquationDisplay display;

    private DampingOscillatorEquation damping;
    private DropBouncerEquation dropbouncer;
    private SpringEquartion spring;
    private BouncerEquation bouncer;
    private DHOEquation dho;

    private JLabel amplitudeLabel;
    private JLabel phaseLabel;
    private JLabel stiffnessLabel;
    private JLabel massLabel;
    private JLabel frictionLabel;

    private JLabel springLabel;

    private JLabel dhoVelocityLabel;
    private JLabel dhoStiffnessLabel;
    private JLabel dhoMassLabel;
    private JLabel dhoDampingLabel;



    private EquationSimulator dampingSimulator;
    private EquationSimulator bouncerSimulator;
    private EquationSimulator springSimulator;
    private EquationSimulator dhoSimulator;
    private DropBouncerSimulator dropSimulator;

    private JLabel timeLabel;
    private JLabel timeScaleLabel;
    private JSlider timeSlider;

    private JPanel debugPanel;
    
    private int linesCount = 0;
    
    private int timeScale = 1;
    
    EquationsControlPanel(DampingOscillatorEquation damping,
                          DropBouncerEquation dropbouncer, SpringEquartion spring, BouncerEquation bouncer,DHOEquation dho) {
        super(new BorderLayout());
        
        this.damping = damping;
        this.damping.addPropertyChangeListener(this);

        this.dropbouncer = dropbouncer;
        this.dropbouncer.addPropertyChangeListener(this);

        this.bouncer = bouncer;
        this.bouncer.addPropertyChangeListener(this);

        this.spring = spring;
        this.spring.addPropertyChangeListener(this);

        this.dho = dho;
        this.dho.addPropertyChangeListener(this);


        //InitSize
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height * 2 / 3;
        int width = screenSize.width * 2 / 3;
        setPreferredSize(new Dimension(width, height));



        //Scrollpanel
        JScrollPane mJScrollpane = new JScrollPane(buildDebugControls());
        mJScrollpane.setPreferredSize(new Dimension(300,650));

        add(mJScrollpane, BorderLayout.EAST);
        add(buildEquationDisplay(), BorderLayout.CENTER);
    }
    
    private Container buildEquationDisplay() {
        JPanel panel = new JPanel(new BorderLayout());

        //绘制顶部图表
        display = new EquationDisplay(0.0, 0.0,
                                      -0.2, 1.2, -1.2, 1.2,
                                      0.2, 5,
                                      0.5, 3);
        display.addEquation(dropbouncer, new Color(0.0f, 0.0f, 1.0f, 0.7f));
        display.addEquation(damping, new Color(0.0f, 0.7f, 0.0f, 0.7f));
        display.addEquation(spring,new Color(1.0f,0.f,0.f,0.7f));
        display.addEquation(bouncer,new Color(1.f,0.7f,0.f,0.7f));
        display.addEquation(dho,new Color(0.7f,0.f,1.f,0.7f));

        panel.add(display, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(new JSeparator(),
                    new GridBagConstraints(0, 0,
                                           2, 1,
                                           1.0, 0.0,
                                           GridBagConstraints.LINE_START,
                                           GridBagConstraints.HORIZONTAL, 
                                           new Insets(0, 0, 0, 0),
                                           0, 0));
        wrapper.add(dropSimulator,
                    new GridBagConstraints(0, 1,
                                           1, 1,
                                           1.0, 1.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.BOTH,
                                           new Insets(0, 0, 0, 0),
                                           0, 0));
        wrapper.add(dampingSimulator,
                new GridBagConstraints(1, 1,
                        1, 1,
                        1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0),
                        0, 0));


        wrapper.add(bouncerSimulator,
                    new GridBagConstraints(2, 1,
                                           1, 1,
                                           1.0, 1.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.BOTH,
                                           new Insets(0, 0, 0, 0),
                                           0, 0));

        wrapper.add(springSimulator,
                new GridBagConstraints(3, 1,
                        1, 1,
                        1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0),
                        0, 0));

        wrapper.add(dhoSimulator,
                new GridBagConstraints(4, 1,
                        1, 1,
                        1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0),
                        0, 0));


        panel.add(wrapper, BorderLayout.CENTER);
        
        return panel;
    }

    private Component buildDebugControls() {
        //bouncerSimulator = new BouncerSimulator(bouncer);
        //dropSimulator = new DropSimulator(bouncer);


        dropSimulator = new DropBouncerSimulator(dropbouncer);

        dampingSimulator = new EquationSimulator(damping,0);
        bouncerSimulator = new EquationSimulator(bouncer,1);
        springSimulator = new EquationSimulator(spring,2);
        dhoSimulator = new EquationSimulator(dho,3);



        debugPanel = new JPanel(new GridBagLayout());

        JSlider slider;
        JCheckBox checkBox;


        //点击开关bounce图表格绘制
//        addEmptySpace(debugPanel, 6);
//        checkBox = addDebugCheckBox(debugPanel, "Draw bounce curve", false);
//        checkBox.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (((JCheckBox) e.getSource()).isSelected()) {
//                    display.addEquation(dropbouncer, new Color(0.0f, 0.0f, 1.0f, 0.7f));
//                } else {
//                    display.removeEquation(dropbouncer);
//                }
//            }
//        });

        checkBox = addDebugCheckBox(debugPanel, "Throw Up", false);
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (((JCheckBox) e.getSource()).isSelected()) {
                    bouncer.throwUp = true;
                    spring.throwUp = true;
                    damping.throwUp = true;
                    display.repaint();
                } else {
                    bouncer.throwUp = false;
                    spring.throwUp = false;
                    damping.throwUp = false;
                    display.repaint();
                }
            }
        });
        

        addEmptySpace(debugPanel, 6);
        addSeparator(debugPanel, "Damping");

        slider = addDebugSlider(debugPanel, "Amplitude:", 1, 20, (int) (damping.getAmplitude() * 10));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                damping.setAmplitude(value / 10.0);
                bouncer.setAmplitude(value / 10.0);
                dropbouncer.setAmplitude(value / 10.0);
            }
        });
        
        slider = addDebugSlider(debugPanel, "Phase:", 0, 300, (int) (damping.getPhase() * 10));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                damping.setPhase(value / 10.0);
                bouncer.setPhase(value / 10.0);
                dropbouncer.setPhase(value / 10.0);
            }
        });
        
        slider = addDebugSlider(debugPanel, "Stiffness:", 1, 50, (int) damping.getStiffness());
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                damping.setStiffness(value);
                bouncer.setStiffness(value);
                dropbouncer.setStiffness(value);
            }
        });
        
        slider = addDebugSlider(debugPanel, "Mass:", 1, 500, (int) (damping.getMass() * 1000));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                damping.setMass(value / 1000.0);
                bouncer.setMass(value / 1000.0);
                dropbouncer.setMass(value / 1000.0);
            }
        });
        
        slider = addDebugSlider(debugPanel, "Friction:", 1, 100, (int) (damping.getFrictionMultiplier() * 100));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                damping.setFrictionMultiplier(value / 100.0);
                bouncer.setFrictionMultiplier(value / 100.0);
                dropbouncer.setFrictionMultiplier(value / 100.0);
            }
        });

        amplitudeLabel = addDebugLabel(debugPanel, "Amplitude:", Double.toString(damping.getAmplitude()));
        phaseLabel = addDebugLabel(debugPanel, "Phase:", Double.toString(damping.getPhase()));
        stiffnessLabel = addDebugLabel(debugPanel, "Stiffness:", Double.toString(damping.getStiffness()) +  " N/m");
        massLabel = addDebugLabel(debugPanel, "Mass:", Double.toString(damping.getMass()) + " kg");
        frictionLabel = addDebugLabel(debugPanel, "Friction:", Double.toString(damping.getFrictionMultiplier()));

        addEmptySpace(debugPanel, 6);
        addSeparator(debugPanel, "Animation");
        
        timeSlider = addDebugSlider(debugPanel, "Time:", 0, 100, (int) (bouncerSimulator.getTime() * 100));
        timeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                dampingSimulator.setTime(value/100.0);
                bouncerSimulator.setTime(value / 100.0);
                springSimulator.setTime(value/100.0);
                dropSimulator.setTime(value / 100.0);
                timeLabel.setText(Double.toString(value / 100.0));
            }
        });

        slider = addDebugSlider(debugPanel, "Duration:", 1, 20, bouncerSimulator.getTimeScale());
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                //bouncerSimulator.setTimeScale(value);
                //dropSimulator.setTimeScale(value);
                timeScale = value;
                timeScaleLabel.setText(Integer.toString(value) + " s");
            }
        });

        timeLabel = addDebugLabel(debugPanel, "Time:", Double.toString(bouncerSimulator.getTime()));
        timeScaleLabel = addDebugLabel(debugPanel, "Time Scale:", Integer.toString(bouncerSimulator.getTimeScale()) + " s");


        addEmptySpace(debugPanel, 6);
        addSeparator(debugPanel, "Spring");


        slider = addDebugSlider(debugPanel, "SpringFactor:", 10, 200, (int) (spring.getFactor() * 100));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                spring.setFactor((float) value/100.0);
            }
        });

        springLabel = addDebugLabel(debugPanel, "SpringFactor:", Double.toString(spring.getFactor()));

        addEmptySpace(debugPanel, 6);
        addSeparator(debugPanel, "DHO - Framer");

        slider = addDebugSlider(debugPanel, "Velocity:", 0, 100, (int) (dho.getVelocity() ));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                dho.setVelocity((float) value);
            }
        });

        slider = addDebugSlider(debugPanel, "Stiffness:", 1, 1000, (int) (dho.getStiffness() ));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                dho.setStiffness((float) value);
            }
        });

        slider = addDebugSlider(debugPanel, "Mass:", 1, 20, (int) (dho.getMass() ));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                dho.setMass((float) value);
            }
        });

        slider = addDebugSlider(debugPanel, "Damping:", 1, 100, (int) (dho.getDamping() ));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final int value = ((JSlider) e.getSource()).getValue();
                dho.setDamping((float) value);
            }
        });

        dhoVelocityLabel = addDebugLabel(debugPanel, "Velocity:", Double.toString(dho.getVelocity()));
        dhoStiffnessLabel = addDebugLabel(debugPanel, "Stiffness:", Double.toString(dho.getStiffness()));
        dhoMassLabel = addDebugLabel(debugPanel, "Mass:", Double.toString(dho.getMass()));
        dhoDampingLabel = addDebugLabel(debugPanel, "Damping:", Double.toString(dho.getDamping()));




        JButton button;
        debugPanel.add(button = new JButton("Reset Animation"),
                       new GridBagConstraints(0, linesCount++,
                                              2, 1,
                                              1.0, 0.0,
                                              GridBagConstraints.CENTER,
                                              GridBagConstraints.NONE, 
                                              new Insets(6, 0, 0, 0),
                                              0, 0));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeSlider.setValue(timeSlider.getMinimum());
            }
        });
        
        Dimension size = button.getPreferredSize();
        
        debugPanel.add(button = new JButton("Start Animation"),
                  new GridBagConstraints(0, linesCount++,
                                         2, 1,
                                         1.0, 0.0,
                                         GridBagConstraints.CENTER,
                                         GridBagConstraints.NONE, 
                                         new Insets(3, 0, 0, 0),
                                         0, 0));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeSlider.setValue(timeSlider.getMinimum());
                for (int i = 0; i < debugPanel.getComponentCount(); i++) {
                    debugPanel.getComponent(i).setEnabled(false);
                }
                startAnimation();
            }
        });
        button.setPreferredSize(size);

        addEmptySpace(debugPanel, 12);
        
        debugPanel.add(Box.createVerticalGlue(),
                  new GridBagConstraints(0, linesCount++,
                                         2, 1,
                                         1.0, 1.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.NONE, 
                                         new Insets(0, 0, 0, 0),
                                         0, 0));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.WEST);
        wrapper.add(debugPanel);
        return wrapper;
    }

    private void addEmptySpace(JPanel panel, int size) {
        panel.add(Box.createVerticalStrut(size),
                   new GridBagConstraints(0, linesCount++,
                                          2, 1,
                                          1.0, 0.0,
                                          GridBagConstraints.CENTER,
                                          GridBagConstraints.VERTICAL, 
                                          new Insets(6, 0, 0, 0),
                                          0, 0));
    }

    private void startAnimation() {
        Timer animation = new Timer(1000 / 60, new Animation());
        animation.start();
    }

    private JCheckBox addDebugCheckBox(JPanel panel, String label, boolean checked) {
        JCheckBox checkBox = new JCheckBox(label, checked);
        panel.add(checkBox,
                  new GridBagConstraints(0, linesCount++,
                                         2, 1,
                                         1.0, 0.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.NONE, 
                                         new Insets(0, 6, 0, 0),
                                         0, 0));
        return checkBox;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if (DampingOscillatorEquation.PROPERTY_AMPLITUDE.equals(name)) {
            amplitudeLabel.setText(evt.getNewValue().toString());
        } else if (DampingOscillatorEquation.PROPERTY_PHASE.equals(name)) {
            phaseLabel.setText(evt.getNewValue().toString());
        } else if (DampingOscillatorEquation.PROPERTY_STIFFNESS.equals(name)) {
            stiffnessLabel.setText(evt.getNewValue() + " N/m");
        } else if (DampingOscillatorEquation.PROPERTY_MASS.equals(name)) {
            massLabel.setText(evt.getNewValue() + " kg");
        } else if (DampingOscillatorEquation.PROPERTY_FRICTION_MULTIPLIER.equals(name)) {
            frictionLabel.setText(evt.getNewValue().toString());
        } else if (SpringEquartion.PROPERTY_SPRINGFACTOR.equals(name)) {
            springLabel.setText(evt.getNewValue().toString());
        }
        else if (DHOEquation.DHO_PROPERTY_VELOCITY.equals(name)) {
            dhoVelocityLabel.setText(evt.getNewValue().toString());
        }
        else if (DHOEquation.DHO_PROPERTY_STIFFNESS.equals(name)) {
            dhoStiffnessLabel.setText(evt.getNewValue().toString());
        }
        else if (DHOEquation.DHO_PROPERTY_MASS.equals(name)) {
            dhoMassLabel.setText(evt.getNewValue().toString());
        }
        else if (DHOEquation.DHO_PROPERTY_DAMPING.equals(name)) {
            dhoDampingLabel.setText(evt.getNewValue().toString());
        }
    }
    
    private void addSeparator(JPanel panel, String label) {
        JPanel innerPanel = new JPanel(new GridBagLayout());
        innerPanel.add(new JLabel(label),
                  new GridBagConstraints(0, 0,
                                         1, 1,
                                         0.0, 0.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.NONE, 
                                         new Insets(0, 0, 0, 0),
                                         0, 0));
        innerPanel.add(new JSeparator(),
                  new GridBagConstraints(1, 0,
                                         1, 1,
                                         0.9, 0.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.HORIZONTAL, 
                                         new Insets(0, 6, 0, 6),
                                         0, 0));
        panel.add(innerPanel,
                  new GridBagConstraints(0, linesCount++,
                                         2, 1,
                                         1.0, 0.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.HORIZONTAL, 
                                         new Insets(6, 6, 6, 0),
                                         0, 0));
    }

    private JLabel addDebugLabel(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        panel.add(labelComponent,
                  new GridBagConstraints(0, linesCount,
                                         1, 1,
                                         0.5, 0.0,
                                         GridBagConstraints.LINE_END,
                                         GridBagConstraints.NONE, 
                                         new Insets(0, 6, 0, 0),
                                         0, 0));
        labelComponent = new JLabel(value);
        panel.add(labelComponent,
                  new GridBagConstraints(1, linesCount++,
                                         1, 1,
                                         0.5, 0.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.NONE, 
                                         new Insets(0, 6, 0, 0),
                                         0, 0));
        return labelComponent;
    }
    
    private JSlider addDebugSlider(JPanel panel, String label,
                                    int min, int max, int value) {
        JSlider slider;
        panel.add(new JLabel(label),
                  new GridBagConstraints(0, linesCount++,
                                         2, 1,
                                         1.0, 0.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.NONE, 
                                         new Insets(0, 6, 0, 0),
                                         0, 0));
        panel.add(slider = new JSlider(min, max, value),
                  new GridBagConstraints(0, linesCount++,
                                         2, 1,
                                         0.0, 0.0,
                                         GridBagConstraints.LINE_START,
                                         GridBagConstraints.HORIZONTAL, 
                                         new Insets(0, 6, 0, 6),
                                         0, 0));
        return slider;
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

                dampingSimulator.setTime(1.0);
                bouncerSimulator.setTime(1.0);
                springSimulator.setTime(1.0);
                dhoSimulator.setTime(1.0);

                dropSimulator.setTime(1.0);


                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < debugPanel.getComponentCount(); i++) {
                            debugPanel.getComponent(i).setEnabled(true);
                        }
                        timeSlider.setValue(timeSlider.getMaximum());
                    }
                });
            } else {
                double time = (double) elapsed / (double) (DELAY_ANIMATION * timeScale);

                dampingSimulator.setTime(time);
                bouncerSimulator.setTime(time);
                springSimulator.setTime(time);
                dhoSimulator.setTime(time);
                dropSimulator.setTime(time);
                timeSlider.setValue((int) (time * 100));
            }
        }
    }
}
