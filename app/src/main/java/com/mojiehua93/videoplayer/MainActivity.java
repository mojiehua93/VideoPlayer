package com.mojiehua93.videoplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String LOCAL_VIDEO_PATH = Environment.getExternalStorageDirectory()
            + "/DCIM/Camera/IMG_0928.MOV";

    private static final int UPDATE_PROGRESS_DELAY = 500;
    private static final int UPDATE_PROGRESS_MESSAGE = 001;
    private static final int PORTRAIT_SCREEN_WIDTH = 240;

    private MyApplication mMyApplication;
    private RelativeLayout mVideoLayout;
    private VideoView mVideoView;
    private MediaController mMediaController;
    private LinearLayout mControllerLayout;
    private ImageView mPlayControllerView;
    private ImageView mScreenChangeView;
    private ImageView mVolumeView;
    private TextView mCurrentTimeView;
    private TextView mTotalTimeView;
    private SeekBar mProgressSeekBar;
    private SeekBar mVolumeSeekBar;
    private int mScreenWidth;
    private int mScreenHeight;
    private DisplayMetrics mMetrics;
    private AudioManager mAudioManager;
    private boolean mIsFullScreen;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_PROGRESS_MESSAGE) {
                int currentPosition = mVideoView.getCurrentPosition();
                int totalPosition = mVideoView.getDuration();
                Log.d(TAG, "handleMessage: currentPosition = " + currentPosition);
                Log.d(TAG, "handleMessage: totalPosition = " + totalPosition);

                updateProgressTime(mCurrentTimeView, currentPosition);
                updateProgressTime(mTotalTimeView, totalPosition);

                mProgressSeekBar.setMax(totalPosition);
                mProgressSeekBar.setProgress(currentPosition);
                sendEmptyMessageDelayed(UPDATE_PROGRESS_MESSAGE, UPDATE_PROGRESS_DELAY);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_main);
        mMyApplication = new MyApplication(this.getApplicationContext());
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initView();
        setPlayEvent();
        mVideoView.start();
        mHandler.sendEmptyMessage(UPDATE_PROGRESS_MESSAGE);
//        initMediaController();
        playLocalVideo();
//        playInternetVideo();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(UPDATE_PROGRESS_MESSAGE);
    }

    private void setPlayEvent() {
        mPlayControllerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoView.isPlaying()) {
                    mPlayControllerView.setImageResource(R.drawable.play_selector);
                    mVideoView.pause();
                    mHandler.removeMessages(UPDATE_PROGRESS_MESSAGE);
                } else {
                    mPlayControllerView.setImageResource(R.drawable.pause_selector);
                    mVideoView.start();
                    mHandler.sendEmptyMessage(UPDATE_PROGRESS_MESSAGE);
                }
            }
        });
        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateProgressTime(mCurrentTimeView, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(UPDATE_PROGRESS_MESSAGE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mVideoView.seekTo(progress);
                mHandler.sendEmptyMessage(UPDATE_PROGRESS_MESSAGE);
            }
        });
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mScreenChangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: mIsFullScreen = " + mIsFullScreen);
                if (mIsFullScreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mScreenChangeView.setImageResource(R.drawable.jc_enlarge);
                    mIsFullScreen = false;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mScreenChangeView.setImageResource(R.drawable.jc_shrink);
                    mIsFullScreen = true;
                }
            }
        });
    }

    private void updateProgressTime(TextView textView, int millisecond) {
//        Log.d(TAG, "updateProgressTime: millisecond = " + millisecond);
        int second = millisecond / 1000;
        int hh = second / (60 * 60);
        int mm = second % (60 * 60) / 60;
        int ss = second % 60;

//        Log.d(TAG, "updateProgressTime: second = " + second);
//        Log.d(TAG, "updateProgressTime: hh = " + hh);
//        Log.d(TAG, "updateProgressTime: mm = " + mm);
//        Log.d(TAG, "updateProgressTime: ss = " + ss);

        String time = null;
        if (hh != 0) {
            time = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            time = String.format("%02d:%02d", mm, ss);
        }
//        Log.d(TAG, "updateProgressTime: time = " + time);
        textView.setText(time);
    }

    private void initMediaController() {
        mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);
        mMediaController.setMediaPlayer(mVideoView);
    }

    private void playInternetVideo() {
        mVideoView.setVideoURI(Uri.parse("http://221.228.226.23/6/n/a/y/l/"
                + "naylspkwvsujoltcqursegarxzowax/hd.yinyuetai.com"
                + "/C02F015B377EA255563C19FBEF88B071.mp4"));
        mVideoView.start();
    }

    private void playLocalVideo() {
        Log.d(TAG, "playLocalVideo: LOCAL_VIDEO_PATH = " + LOCAL_VIDEO_PATH);
        mVideoView.setVideoPath(LOCAL_VIDEO_PATH);
    }

    private void initView() {
        mVideoLayout = findViewById(R.id.video_layout);
        mVideoView = findViewById(R.id.video_view);
        mControllerLayout = findViewById(R.id.controller_bar);
        mPlayControllerView = findViewById(R.id.pause_image);
        mScreenChangeView = findViewById(R.id.screen_change);
        mCurrentTimeView = findViewById(R.id.time_current);
        mTotalTimeView = findViewById(R.id.time_total);
        mProgressSeekBar = findViewById(R.id.progress_bar);
        mVolumeView = findViewById(R.id.volume_image);
        mVolumeSeekBar = findViewById(R.id.volume_seekbar);
        mMetrics = getResources().getDisplayMetrics();
        mScreenWidth = mMetrics.widthPixels;
        mScreenHeight = mMetrics.heightPixels;
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumeSeekBar.setMax(streamMaxVolume);
        mVolumeSeekBar.setProgress(streamVolume);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: newConfig = " + newConfig);
        Log.d(TAG, "onConfigurationChanged: mIsFullScreen" + mIsFullScreen);
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mVolumeView.setVisibility(View.VISIBLE);
            mVolumeSeekBar.setVisibility(View.VISIBLE);
            mIsFullScreen = true;
            mScreenChangeView.setImageResource(R.drawable.jc_shrink);
        } else {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.dp2px(PORTRAIT_SCREEN_WIDTH));
            mVolumeView.setVisibility(View.INVISIBLE);
            mVolumeSeekBar.setVisibility(View.INVISIBLE);
            mIsFullScreen = false;
            mScreenChangeView.setImageResource(R.drawable.jc_enlarge);
        }
    }

    private void setVideoViewScale(int width, int height) {
        ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
        videoLayoutParams.width = width;
        videoLayoutParams.height = height;
        mVideoLayout.setLayoutParams(videoLayoutParams);

        ViewGroup.LayoutParams videoViewLayoutParams= mVideoView.getLayoutParams();
        videoViewLayoutParams.width = width;
        videoViewLayoutParams.height = height;
        mVideoView.setLayoutParams(videoViewLayoutParams);
    }
}
