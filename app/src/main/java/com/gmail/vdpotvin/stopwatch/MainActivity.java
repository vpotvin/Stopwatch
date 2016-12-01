package com.gmail.vdpotvin.stopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.vdpotvin.stopwatch.R.string.lap;

public class MainActivity extends Activity {

    private Chronometer stopwatch;
    private List<Lap> laps;
    private ArrayAdapter<Lap> adapter;
    private ListView lapListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        laps = new ArrayList<>();
        adapter = new LapAdapter(this, laps);
        lapListView = (ListView) findViewById(R.id.lap_list);
        lapListView.setAdapter(adapter);



        stopwatch = (Chronometer) findViewById(R.id.stopwatch_text);
        stopwatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(!laps.isEmpty()) {
                    laps.get(0).setTime(stopwatch.getText().toString());
                }
            }
        });
    }

    public void onStartStopClick(View view) {
        Button button = (Button) view;

        if(button.getText() == getResources().getString(R.string.start)) {
            stopwatch.setBase(SystemClock.elapsedRealtime());
            stopwatch.start();
            button.setText(getResources().getString(R.string.stop));
        } else {
            stopwatch.stop();
            button.setText(getResources().getString(R.string.start));
            ((Button) findViewById(R.id.lap_reset_btn)).setText(
                    getResources().getString(R.string.reset));
        }
    }

    public void onLapResetClick(View view) {
        Button button = (Button) view;
        String lapString = getResources().getString(lap);
        if(button.getText() == lapString) {
            laps.add(0, new Lap(lapString + " " + laps.size() + 1, stopwatch.getText().toString()));
            adapter.notifyDataSetChanged();
        } else {
            laps.clear();
            adapter.notifyDataSetChanged();
            
        }
    }
}
