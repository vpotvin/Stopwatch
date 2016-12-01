package com.gmail.vdpotvin.stopwatch;

/**
 * Created by vdpotvin on 11/30/16.
 * Edited Android Chronometer Widget to count in milliseconds.
 * Some localization and formatting features lost due to inability to access internal resources.
 * Formatting is app-specific for the Stopwatch.
 */


//ToDo: Bug where chronometer does not properly stop.
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
    private StringBuilder mFormatBuilder;
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

        //Remove reference to Android internals - vp
        init();
    }


    private void init() {
        mBase = SystemClock.elapsedRealtime();
        lapTime = 0;
        paused = false;
        updateText(mBase);
    }

    /**
     * Set the time that the count-up timer is in reference to.
     *
     * @param base Use the {@link SystemClock#elapsedRealtime} time base.
     */

    public void setBase(long base) {
        mBase = base;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    /**
     * Return the base time as set through {@link #setBase}.
     */
    public long getBase() {
        return mBase;
    }




    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        mOnChronometerTickListener = listener;
    }

    /**
     * @return The listener (may be null) that is listening for chronometer change
     *         events.
     */
    public OnChronometerTickListener getOnChronometerTickListener() {
        return mOnChronometerTickListener;
    }

    /**
     * Start counting up.  This does not affect the base as set from {@link #setBase}, just
     * the view display.
     *
     * Chronometer works by regularly scheduling messages to the handler, even when the
     * Widget is not visible.  To make sure resource leaks do not occur, the user should
     * make sure that each start() call has a reciprocal call to {@link #stop}.
     */
    public void start() {
        mStarted = true;
        updateRunning();
    }

    /**
     * Stop counting up.  This does not affect the base as set from {@link #setBase}, just
     * the view display.
     *
     * This stops the messages to the handler, effectively releasing resources that would
     * be held as the chronometer is running, via {@link #start}.
     */
    public void stop() {
        mStarted = false;
        paused = true;
        updateRunning();
    }

    //get lap formatted as a string.
    public String getLap(boolean lapClick) {
        String lap;
        if(lapTime !=0) lap = getTimeAsString(mNow - lapTime);
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
    //ToDo: determine if hours option is needed
    private static final int HOUR_IN_MILLI = MIN_IN_MILLI * 60;

    /*
    Integral change from Android Chronometer. Had this method been public I would have simply
    extended the class. Completely rewritten to correctly account for milliseconds and return the
    results in a formatted string.
     */
    private synchronized void updateText(long now) {
        mNow = now;
        long elapsed = now - mBase;

        setText(getTimeAsString(elapsed));
    }

    private String getTimeAsString(long time) {
        DecimalFormat df = new DecimalFormat("00");

        String text = "";

        int minutes = (int) (time / MIN_IN_MILLI);
        int remaining = (int) (time % MIN_IN_MILLI);

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
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 100);
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
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 100);
            }
        }
    };

    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

}

