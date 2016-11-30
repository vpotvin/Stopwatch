package com.gmail.vdpotvin.stopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class MainActivity extends Activity {

    Chronometer mStopwatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStopwatch = (Chronometer) findViewById(R.id.stopwatch_text);
        mStopwatch.setBase(SystemClock.elapsedRealtime());
        mStopwatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

            }
        });
    }

    public void onStartStopClick(View view) {
        Button button = (Button) view;

        if(button.getText() == getResources().getString(R.string.start)) {
            mStopwatch.start();
            button.setText(getResources().getString(R.string.stop));
        }
    }
}
