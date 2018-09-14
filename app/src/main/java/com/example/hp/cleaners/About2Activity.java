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

public class About2Activity extends AppCompatActivity {


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 7000;
    private TextView fmBasic, fmPro;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_about2);

        fmBasic = findViewById(R.id.fullHomeBasicCleaningTxt);
        fmPro = findViewById(R.id.fullHomeProcleanTxt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fmBasic.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            fmPro.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. *
                Intent mainIntent = new Intent(About2Activity.this, MainActivity.class);
                //SplashActivity.this.startActivity(mainIntent);
                About2Activity.this.startActivity(mainIntent);
                //SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
        */
    }


    public void nextAbt(View view) {
        /* Create an Intent that will start the Main-Activity. */
        Intent mainIntent = new Intent(About2Activity.this, MainActivity.class);
        //SplashActivity.this.startActivity(mainIntent);
        About2Activity.this.startActivity(mainIntent);
        //SplashActivity.this.finish();
    }

    public void prevtAbt(View view) {
        /* Create an Intent that will start the Next-Activity. */
        Intent mainIntent = new Intent(About2Activity.this, Aboutctivity.class);
        //SplashActivity.this.startActivity(mainIntent);
        About2Activity.this.startActivity(mainIntent);
        //SplashActivity.this.finish();
    }
}
