package de.whyqueue.kvwlwifilogin.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.time.Duration;

import de.whyqueue.kvwlwifilogin.R;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        redirectToLoginAfter(Duration.ofSeconds(3));
    }

    private void redirectToLoginAfter(Duration duration){
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, duration.toMillis());
    }
}
