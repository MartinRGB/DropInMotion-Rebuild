package org.progx.dropinmotion.physics;

import org.progx.dropinmotion.equation.AbstractEquation;

/**
 * Created by MartinRGB on 2017/7/11.
 */

//Inspired By Frame's SpringDHOAnimator
//Reference - https://github.com/koenbok/Framer/blob/master/framer/Animators/SpringDHOAnimator.coffee
public class DHOEquation extends AbstractEquation {
    public static final String DHO_PROPERTY_VELOCITY = "dho_velocity";
    public static final String DHO_PROPERTY_STIFFNESS = "dho_stiffness";
    public static final String DHO_PROPERTY_MASS = "dho_mass";
    public static final String DHO_PROPERTY_DAMPING = "dho_damping";

    public static final String PROPERTY_TOLERANCE = "dho_tolerance";

    public boolean throwUp = false;
    private boolean mIsFinished = false;

    // exposed parameters

    private double tolerance = 1/10000;

    private double velocity = 0;
    private double stiffness = 50;
    private double mass = 0.2;
    private double damping = 2;

    public DHOEquation(double velocity,double stiffness, double mass, double damping) {

        this.velocity = clamp(velocity,0,100);
        this.stiffness = clamp(stiffness,1,1000);
        this.mass = clamp(mass,1,20);
        this.damping = clamp(damping,1,100);;

    }

    //Optional
    public DHOEquation(double stiffness, double mass, double damping) {

        this.stiffness = clamp(stiffness,1,1000);
        this.mass = clamp(mass,1,20);
        this.damping = clamp(damping,1,100);;

    }



    double preValue = 0;
    double postValue;

    public double compute(double x) {


        //TODO
        double k = -stiffness;
        double b = -damping;
        double F_spring = k*(preValue - 1);
        double F_damper = b*velocity;

        velocity = (F_spring + F_damper)/mass * x;

        postValue = (1+velocity) * x/1000.;
        preValue = postValue;

        return postValue;

//        if(x>0 && Math.abs(velocity) < tolerance){
//            mIsFinished = true;
//
//
//
//        }
//        else {
//            mIsFinished = false;
//        }
//
//        if(mIsFinished){
//            return 1;
//        }
//        else {
//
//            double k = -stiffness;
//            double b = -damping;
//            double F_spring = k*(savedValue - 1);
//            double F_damper = b*velocity;
//
//            velocity = (F_spring + F_damper)/mass * x;
//
//            savedValue = velocity * x;
//
//            return savedValue;
//        }



        //Drop Down
        //return y;
        //Throw Up
//        if(throwUp){
//            return -y+1;
//        }
//        else {
//            return y;
//        }
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        double oldValue = this.velocity;
        this.velocity = clamp(velocity,0,100);
        firePropertyChange(DHO_PROPERTY_VELOCITY, oldValue, velocity);
    }


    public double getStiffness() {
        return stiffness;
    }

    public void setStiffness(double stiffness) {
        double oldValue = this.stiffness;
        this.stiffness = clamp(stiffness,1,1000);
        firePropertyChange(DHO_PROPERTY_STIFFNESS, oldValue, stiffness);
    }


    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        double oldValue = this.mass;
        this.mass = clamp(mass,1,20);
        firePropertyChange(DHO_PROPERTY_MASS, oldValue, mass);
    }

    public double getDamping() {
        return damping;
    }

    public void setDamping(double damping) {
        double oldValue = this.damping;
        this.damping = clamp(damping,1,100);
        firePropertyChange(DHO_PROPERTY_DAMPING, oldValue, damping);
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }


}