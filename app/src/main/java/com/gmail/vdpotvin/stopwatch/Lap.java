package com.gmail.vdpotvin.stopwatch;

/**
 * Created by vdpotvin on 11/30/16.
 */

public class Lap {
    private String lapCount;
    private String time;

    public Lap(String time, String lapCount) {
        this.time = time;
        this.lapCount = lapCount;
    }

    public String getLapCount() {
        return lapCount;
    }

    public String getTime() {
        return time;
    }
}
