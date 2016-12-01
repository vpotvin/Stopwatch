package com.gmail.vdpotvin.stopwatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vdpotvin on 11/30/16.
 */

public class Lap {
    private String lapCount;
    private long time;

    public Lap(long time, String lapCount) {
        this.time = time;
        this.lapCount = lapCount;
    }

    public String getLapCount() {
        return lapCount;
    }

    public long getTime() {
        return time;
    }

    public String getTimeString() {
        Date date = new Date(time);
        DateFormat df = new SimpleDateFormat("mm:ss:SSS");
        return df.format(date);
    }

    public void setTime(long time) {
        this.time = time;
    }
}
