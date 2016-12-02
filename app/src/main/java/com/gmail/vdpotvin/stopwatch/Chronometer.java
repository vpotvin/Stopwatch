package com.gmail.vdpotvin.stopwatch;

/**
 * Created by vdpotvin on 11/30/16.
 * Edited Android Chronometer Widget to count in milliseconds.
 * Formatting is app-specific for the Stopwatch.
 * Added functionality for tracking laps.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DecimalFormat;


public class Chronometer extends TextView {
    private static final String TAG = "Chronometer";

    /**
     * A callback that notifies when the chronometer has incremented on its own.
     */
    public interface OnChronometerTickListener {

        /**
         * Notification that the chronometer has changed.
         */
        void onChronometerTick(Chronometer chronometer);

    }

    private long mBase;



    private long mNow; // the currently displayed time
    private long lapTime; // the time a lap was recorded.
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private boolean paused;
    private OnChronometerTickListener mOnChronometerTickListener;


    private static final int TICK_WHAT = 2;

    /**
     * Initialize this Chronometer object.
     * Sets the base to the current time.
     */
    public Chronometer(Context context) {
        this(context, null, 0);
    }

    /**
     * Initialize with standard view layout information.
     * Sets the base to the current time.
     */
    public Chronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initialize with standard view layout information and style.
     * Sets the base to the current time.
     */
    public Chronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);

        init();
    }


    private void init() {
        mBase = SystemClock.elapsedRealtime();
        lapTime = 0;
        paused = false;
        updateText(mBase);
    }



    public void setBase(long base) {
        mBase = base;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }



    public long getBase() {
        return mBase;
    }




    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        mOnChronometerTickListener = listener;
    }


    public OnChronometerTickListener getOnChronometerTickListener() {
        return mOnChronometerTickListener;
    }

    public void start() {
        mStarted = true;
        updateRunning();
    }

    /**
     * Edited to include boolean value paused. Original configuration kept counting while stopped.
     */
    public void stop() {
        mStarted = false;
        paused = true;
        updateRunning();
    }

    //get lap formatted as a string.
    public String getLap(boolean lapClick) {
        String lap;
        if(lapTime != 0) lap = getTimeAsString(mNow - lapTime);
        else lap = getTimeAsString(mNow - mBase);
        if(lapClick) lapTime = mNow;
        return lap;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    //add value for seconds in milliseconds - vp
    private static final int SEC_IN_MILLI = 1000;
    private static final int MIN_IN_MILLI = SEC_IN_MILLI * 60;
    private static final int HOUR_IN_MILLI = MIN_IN_MILLI * 60;

    /*
    Integral change from Android Chronometer. Had this method been public I would have simply
    extended the class.
     */
    private synchronized void updateText(long now) {
        mNow = now;
        long elapsed = now - mBase;

        setText(getTimeAsString(elapsed));
    }

    /*
    Custom method that returns a long as a String in the format "mm:ss.SS"
     */
    private String getTimeAsString(long time) {
        DecimalFormat df = new DecimalFormat("00");

        String text = "";
        int remaining;

        int hours = (int) (time / HOUR_IN_MILLI);
        if(hours > 0) {
            text += df.format(hours);
            text += ":";
        }

        int minutes = (int) (time / MIN_IN_MILLI);
        remaining = (int) (time % MIN_IN_MILLI);

        int seconds = remaining / SEC_IN_MILLI;
        remaining = remaining % SEC_IN_MILLI;

        int milliseconds = remaining / 10;

        text += df.format(minutes);
        text += ":";
        text += df.format(seconds);
        text += ".";
        text += df.format(milliseconds);
        return text;
    }

    private void updateRunning() {
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                long now = SystemClock.elapsedRealtime();
                long difference = now - mNow;
                if(paused) {
                    mBase += difference;
                    lapTime += difference;
                    updateText(now);
                    paused = false;
                }
                else updateText(now);
                dispatchChronometerTick();
                //Change tick to 100 - vp
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 10);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                long now = SystemClock.elapsedRealtime();
                long difference = now - mNow;
                if(paused) {
                    mBase += difference;
                    lapTime += difference;
                    updateText(now);
                    paused = false;
                }
                else updateText(now);
                dispatchChronometerTick();
                //Change tick to 100 - vp
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 10);
            }
        }
    };

    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

}

