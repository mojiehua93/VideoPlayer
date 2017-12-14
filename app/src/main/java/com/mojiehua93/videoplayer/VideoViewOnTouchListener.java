package com.mojiehua93.videoplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by MOJIEHUA93 on 2017/12/7.
 */

public class VideoViewOnTouchListener implements View.OnTouchListener {
    public static final String TAG = "VideoViewOnTouchListener";

    private boolean mIsAdjust;
    private int mThresHold = 4;
    private MainActivity mMainActivity;
    private int mScreenWidth;
    private float lastX = 0;
    private float lastY = 0;

    public VideoViewOnTouchListener(Context context, int screenWidth) {
        mMainActivity = (MainActivity) context;
        mScreenWidth = screenWidth;
    }
    @SuppressLint("LongLogTag")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.d(TAG, "onTouch: x = " + x);
        Log.d(TAG, "onTouch: y = " + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.d(TAG, "onTouch: ACTION_DOWN");
                lastX = x;
                lastY = y;
                Log.d(TAG, "onTouch: ACTION_DOWN lastX = " + lastX);
                Log.d(TAG, "onTouch: ACTION_DOWN lastY = " + lastY);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Log.d(TAG, "onTouch: action_move x = " + x);
                Log.d(TAG, "onTouch: action_move lastX = " + lastX);
                float deltaX = x - lastX;
                float deltaY = y - lastY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                Log.d(TAG, "onTouch: absDeltaX = " + absDeltaX);
                Log.d(TAG, "onTouch: absDeltaY = " + absDeltaY);

                if (absDeltaX > mThresHold && absDeltaY > mThresHold) {
                    if (absDeltaX < absDeltaY) {
                        mIsAdjust = true;
                    } else {
                        mIsAdjust = false;
                    }
                } else if (absDeltaX < mThresHold && absDeltaY > mThresHold) {
                    mIsAdjust = true;
                } else if (absDeltaX > mThresHold && absDeltaY < mThresHold) {
                    mIsAdjust = false;
                }

                if (mIsAdjust) {
                    Log.d(TAG, "onTouch: mIsAdjust");
                    if (x < mScreenWidth / 2) {
                        if (deltaY > 0) {
                            //TODO lower brightness
                        } else {
                            //TODO higher brightness
                        }
                        mMainActivity.changeBrightness(-deltaY);
                    } else {
                        if (deltaY > 0) {
                            //TODO lower volume
                        } else {
                            //TODO higher volume
                        }
                        Log.d(TAG, "onTouch: changeVolume");
                        mMainActivity.changeVolume(-deltaY);
                    }
                }
                lastX = x;
                lastY = y;
                Log.d(TAG, "onTouch: action_move lastX1 = " + lastX);
                break;
            }

            case MotionEvent.ACTION_UP:
                mMainActivity.setTouchProgressLayoutVisibility(View.INVISIBLE);
                break;
        }

        return true;
    }
}
