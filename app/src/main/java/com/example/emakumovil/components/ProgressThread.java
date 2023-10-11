package com.example.emakumovil.components;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ProgressThread extends Thread {	
    
    // Class constants defining state of the thread
    public final static int DONE = 0;
    final static int RUNNING = 1;
    
    private Handler mHandler;
    private int mState;
    private int total;
    private  int delay;
    // Constructor with an argument that specifies Handler on main thread
    // to which messages will be sent by this thread.
    
    public ProgressThread(Handler h,int maxBarValue,int delay) {
        mHandler = h;
        this.total=maxBarValue;
        this.delay=delay;
    }
    
    // Override the run() method that will be invoked automatically when 
    // the Thread starts.  Do the work required to update the progress bar on this
    // thread but send a message to the Handler on the main UI thread to actually
    // change the visual representation of the progress. In this example we count
    // the index total down to zero, so the horizontal progress bar will start full and
    // count down.
    
    @Override
    public void run() {
        mState = RUNNING;   
        while (mState == RUNNING) {
            try {
                // Control speed of update (but precision of delay not guaranteed)
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Log.e("ERROR", "Thread was Interrupted");
            }
            
            // Send message (with current value of  total as data) to Handler on UI thread
            // so that it can update the progress bar.
            
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("total", total);
            msg.setData(b);
            mHandler.sendMessage(msg);
            
            total--;    // Count down
        }
    }
    
    // Set current state of thread (use state=ProgressThread.DONE to stop thread)
    public void setState(int state) {
    	System.out.println("cambiando estado "+state);
        mState = state;
    }
}
