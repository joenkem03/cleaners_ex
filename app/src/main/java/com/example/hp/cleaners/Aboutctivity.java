package com.example.hp.cleaners;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class Aboutctivity extends AppCompatActivity {

    private TextView homeClean, deepFull;

    //@Override
    //protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_aboutctivity);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        /** Duration of wait **/
        //private final int SPLASH_DISPLAY_LENGTH = 7000;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            setContentView(R.layout.activity_aboutctivity);
            homeClean = findViewById(R.id.HomeCleaningTxt);
            deepFull = findViewById(R.id.fullHomeBasicCleaningTxt);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                homeClean.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                deepFull.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            }

            /* New Handler to start the Menu-Activity
             * and close this Splash-Screen after some seconds.*
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    /* Create an Intent that will start the Menu-Activity. *
                    Intent mainIntent = new Intent(Aboutctivity.this, About2Activity.class);
                    //SplashActivity.this.startActivity(mainIntent);
                    Aboutctivity.this.startActivity(mainIntent);
                    //SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
            */
        }

    public void skipAbt(View view) {
        /* Create an Intent that will start the Main-Activity. */
        Intent mainIntent = new Intent(Aboutctivity.this, MainActivity.class);
        //SplashActivity.this.startActivity(mainIntent);
        Aboutctivity.this.startActivity(mainIntent);
        //SplashActivity.this.finish();
    }

    public void nextAbt(View view) {
        /* Create an Intent that will start the Next-Activity. */
        Intent mainIntent = new Intent(Aboutctivity.this, About2Activity.class);
        //SplashActivity.this.startActivity(mainIntent);
        Aboutctivity.this.startActivity(mainIntent);
        //SplashActivity.this.finish();
    }
}
