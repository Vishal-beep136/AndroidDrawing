package com.example.androiddrawing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.util.StatusBarUtil;
import com.viaviapp.androiddrawingdemo.R;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ActivitySplash extends AppCompatActivity {

    protected boolean active = true;
    protected int splashTime = 2000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtil.setStatusBarGradiant(ActivitySplash.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        Thread splashThread = new Thread() {
            public void run() {
                try {
                    int waited = 0;

                    while (active && (waited < splashTime)) {
                        sleep(100);
                        if (active) {
                            waited += 100;
                        }
                    }
                } catch (Exception e) {
                    e.toString();
                } finally {
                    Intent int1 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(int1);
                    finish();

                }
            }
        };

        splashThread.start();
    }
}
