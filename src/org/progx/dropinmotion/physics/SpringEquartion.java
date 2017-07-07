package org.progx.dropinmotion.physics;

import org.progx.dropinmotion.equation.AbstractEquation;

public class SpringEquartion extends AbstractEquation {


    public static final String PROPERTY_SPRINGFACTOR = "factor";

    private static final double DEFAULT_FACTOR = 0.5;
    private double factor;

    public SpringEquartion(float factor) {

        this.factor = factor;
        getFactor();
    }

    public SpringEquartion(){
        factor = DEFAULT_FACTOR;
        getFactor();
    }

    public double compute(double x) {

        getFactor();
        double y =  (Math.pow(2, -10 * x) * Math.sin((x - factor / 4.0d) * (2.0d * Math.PI) / factor) + 1);
        return -y+1;
    }

    public double getFactor() {
        return  factor;
    }

    public void setFactor(double factor) {
        double oldValue = this.factor;
        this.factor = (float) factor;
        firePropertyChange(PROPERTY_SPRINGFACTOR, oldValue, factor);
    }

}
