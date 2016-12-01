package com.gmail.vdpotvin.stopwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vdpotvin on 11/30/16.
 */

public class LapAdapter extends ArrayAdapter<Lap> {

    public LapAdapter(Context context,  List<Lap> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lap lap = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item,
                    parent, false);
        }

        TextView lapCountText = (TextView) convertView.findViewById(R.id.lap_count_text);
        TextView lapTimeText = (TextView) convertView.findViewById(R.id.lap_time_text);

        lapCountText.setText(lap.getLapCount());
        lapTimeText.setText(lap.getTime());

        return convertView;
    }
}
