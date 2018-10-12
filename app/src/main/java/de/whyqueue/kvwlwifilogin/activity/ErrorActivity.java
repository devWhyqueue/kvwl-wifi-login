package de.whyqueue.kvwlwifilogin.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.time.Duration;

import de.whyqueue.kvwlwifilogin.R;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        displayErrorMsg();
        redirectToLoginAfter(Duration.ofSeconds(3));
    }

    private void displayErrorMsg(){
        TextView errorMsg = (TextView)findViewById(R.id.errorMsg);
        String msg = getIntent().getStringExtra("ERROR");
        errorMsg.setText(msg);
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
