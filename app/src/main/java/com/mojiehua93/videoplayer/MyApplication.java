package com.mojiehua93.videoplayer;

import android.app.Application;
import android.content.Context;

/**
 * Created by MOJIEHUA93 on 2017/12/3.
 */

public class MyApplication extends Application {
    public static Context mContext;

    public MyApplication(Context context) {
        mContext = context.getApplicationContext();
    }
    public static Context getContext() {
        return mContext;
    }
}
