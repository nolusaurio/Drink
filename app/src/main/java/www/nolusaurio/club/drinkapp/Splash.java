package www.nolusaurio.club.drinkapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;


import com.blogspot.atifsoftwares.animatoolib.Animatoo;


public class Splash extends AppCompatActivity {

    public static final long DURATION = 2000;

    private TextView inicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        inicio = (TextView)findViewById(R.id.ini);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        animation.setStartOffset(300);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        inicio.startAnimation(animation);

        Log.e("SPLASH", "oncreate");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                irMain();
            }
        }, DURATION);


    }

    private void irMain(){
        Intent i = new Intent(Splash.this, MainActivity.class);
        startActivity(i);
        Animatoo.animateShrink(this);
        finish();
    }
}
