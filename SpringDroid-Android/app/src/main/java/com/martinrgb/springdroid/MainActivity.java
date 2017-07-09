package com.martinrgb.springdroid;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martinrgb.springdroid.SpringInterpolator.CustomBouncerInterpolator;
import com.martinrgb.springdroid.SpringInterpolator.CustomDampingInterpolator;
import com.martinrgb.springdroid.SpringInterpolator.CustomJellyInterpolator;
import com.martinrgb.springdroid.SpringInterpolator.CustomSpringInterpolator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragmentConatiner, new PlaceholderFragment()).commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }


    //Action
    private boolean bouncerIsBig = false;
    private boolean dampingIsBig = false;
    private boolean springIsBig = false;
    private boolean jelloIsBig = false;

    public void onBouncerClick(final View v){

        ObjectAnimator objectAnimatorX = (bouncerIsBig) ? ObjectAnimator.ofFloat(v,"scaleX",2.f,1.f) : ObjectAnimator.ofFloat(v,"scaleX",1.f,2.f);
        objectAnimatorX.setDuration(1000);
        objectAnimatorX.setInterpolator((bouncerIsBig) ? new CustomBouncerInterpolator(80.f,0.f) : new CustomBouncerInterpolator() );
        objectAnimatorX.start();

        ObjectAnimator objectAnimatorY = (bouncerIsBig) ? ObjectAnimator.ofFloat(v,"scaleY",2.f,1.f) : ObjectAnimator.ofFloat(v,"scaleY",1.f,2.f);
        objectAnimatorY.setDuration(1000);
        objectAnimatorY.setInterpolator((bouncerIsBig) ? new CustomBouncerInterpolator(80.f,0.f) : new CustomBouncerInterpolator() );
        objectAnimatorY.start();

        bouncerIsBig = !bouncerIsBig;
    }

    public void onDampingClick(final View v){


        ObjectAnimator objectAnimatorX = (dampingIsBig) ? ObjectAnimator.ofFloat(v,"scaleX",2.f,1.f) : ObjectAnimator.ofFloat(v,"scaleX",1.f,2.f);
        objectAnimatorX.setDuration(1000);
        objectAnimatorX.setInterpolator((dampingIsBig) ? new CustomDampingInterpolator(80.f,0.f) : new CustomDampingInterpolator() );
        objectAnimatorX.start();

        ObjectAnimator objectAnimatorY = (dampingIsBig) ? ObjectAnimator.ofFloat(v,"scaleY",2.f,1.f) : ObjectAnimator.ofFloat(v,"scaleY",1.f,2.f);
        objectAnimatorY.setDuration(1000);
        objectAnimatorY.setInterpolator((dampingIsBig) ? new CustomDampingInterpolator(80.f,0.f) : new CustomDampingInterpolator() );
        objectAnimatorY.start();

        dampingIsBig = !dampingIsBig;
    }

    public void onSpringClick(final View v){

        ValueAnimator vaX = (springIsBig) ? ValueAnimator.ofInt(300,150) : ValueAnimator.ofInt(150,300);
        vaX.setDuration(1000);
        vaX.setInterpolator((springIsBig) ? new CustomSpringInterpolator(0.1f) : new CustomSpringInterpolator());
        vaX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().height = value.intValue();
                v.requestLayout();
            }
        });
        vaX.start();

        ValueAnimator vaY = (springIsBig) ? ValueAnimator.ofInt(500,250) : ValueAnimator.ofInt(250,500);
        vaY.setDuration(1000);
        vaY.setInterpolator((springIsBig) ? new CustomSpringInterpolator(0.1f) : new CustomSpringInterpolator());
        vaY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().width = value.intValue();
                v.requestLayout();
            }
        });
        vaY.start();

        springIsBig = !springIsBig;
    }

    public void onJelloClick(final View v){


        ValueAnimator vaX = (jelloIsBig) ? ValueAnimator.ofInt(300,150) : ValueAnimator.ofInt(150,300);
        vaX.setDuration(1000);
        vaX.setInterpolator((jelloIsBig) ? new CustomJellyInterpolator(1.2f) : new CustomJellyInterpolator());
        vaX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().height = value.intValue();
                v.requestLayout();
            }
        });
        vaX.start();

        ValueAnimator vaY = (jelloIsBig) ? ValueAnimator.ofInt(500,250) : ValueAnimator.ofInt(250,500);
        vaY.setDuration(1000);
        vaY.setInterpolator((jelloIsBig) ? new CustomJellyInterpolator(1.2f) : new CustomJellyInterpolator());
        vaY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().width = value.intValue();
                v.requestLayout();
            }
        });
        vaY.start();

        jelloIsBig = !jelloIsBig;
    }
}
