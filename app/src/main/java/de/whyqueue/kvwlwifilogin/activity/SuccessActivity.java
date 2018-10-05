package de.whyqueue.kvwlwifilogin.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.time.Duration;

import de.whyqueue.kvwlwifilogin.R;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        finishAfter(Duration.ofSeconds(1));
    }

    private void finishAfter(Duration duration){
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, duration.toMillis());
    }
}
