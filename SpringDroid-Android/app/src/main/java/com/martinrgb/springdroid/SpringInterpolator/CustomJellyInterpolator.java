package com.martinrgb.springdroid.SpringInterpolator;

import android.view.animation.Interpolator;

public class CustomJellyInterpolator implements Interpolator {

    private float factor = 0.6f;

    public CustomJellyInterpolator(float factor) {
        this.factor = factor;
    }

    public CustomJellyInterpolator() {

    }

    @Override
    public float getInterpolation(float ratio) {
        if (ratio == 0.0f || ratio == 1.0f)
            return ratio;
        else {
            float two_pi = (float) (Math.PI * 2.7f);
            return (float) Math.pow(2.0f, -10.0f * ratio) * (float) Math.sin((ratio - (factor/5.0f)) * two_pi/factor) + 1.0f;
        }
    }
}