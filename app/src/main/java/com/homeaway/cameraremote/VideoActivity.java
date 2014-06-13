package com.homeaway.cameraremote;

        import java.io.File;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.Timer;
        import java.util.TimerTask;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.SharedPreferences.Editor;
        import android.content.pm.PackageManager;
        import android.media.CamcorderProfile;
        import android.media.MediaRecorder;
        import android.media.MediaRecorder.OnErrorListener;
        import android.media.MediaRecorder.OnInfoListener;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Looper;
        import android.os.PowerManager;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.Toast;


//*******************************************************
//*******************************************************
// CamaraView
//*******************************************************
//*******************************************************
public class VideoActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {
    MediaRecorder mRecorder;
    SurfaceHolder mHolder;
    boolean mRecording = false;
    boolean mStop = false;
    boolean mPrepared = false;
    boolean mLoggedIn = false;
//    private PowerManager.WakeLock mWakeLock;
    private ArrayList<String> mFiles = new ArrayList<String>();
    private static final String APP_KEY = "YOUR_APP_KEY";
    private static final String APP_SECRET = "YOUR_APP_SECRET";
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private Timer mStopTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );

        mRecorder = new MediaRecorder();
        initRecorder();
        setContentView(R.layout.camera);

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.surface_camera);
        mHolder = cameraView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);

//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Ping");
//        mWakeLock.acquire();

    }


    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
//            mWakeLock.release();
        }
        catch (Exception e) {
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
    }

    private void initRecorder() {
        mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        mRecorder.setMaxDuration((int) 60000);
        mRecorder.setVideoSize(320, 240);
        mRecorder.setVideoFrameRate(15);

        File mediaStorageDir = new File("/sdcard/Surveillance/");
        if ( !mediaStorageDir.exists() ) {
            if ( !mediaStorageDir.mkdirs() ){
            }
        }
        String filePath = "/sdcard/Surveillance/" + "VID_"+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        mFiles.add(filePath);
        mRecorder.setOutputFile(filePath);
    }

    private void prepareRecorder() {
        mRecorder.setPreviewDisplay(mHolder.getSurface());

        try {
            mRecorder.prepare();
            mPrepared = true;
        } catch (IllegalStateException e) {
            //finish();
        } catch (IOException e) {
            //finish();
        } catch( Exception e) {
        }

    }

    public void onClick(View v) {
        try {
            if (mRecording) {
                mRecorder.reset();
                mPrepared = false;
                mRecording = false;
                this.initRecorder();
                this.prepareRecorder();
                mStopTimer.cancel();
            }
            else {
                //prepareRecorder();
                mRecorder.start();
                mRecording = true;
                mStopTimer = new Timer();
                mStopTimer.schedule( new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        restartRecorder();
                    }
                }, 60 * 1000 );
            }
        }
        catch (Exception e) {
        }
    }

    public void restartRecorder() {
        mRecorder.reset();
        mPrepared = false;
        mRecording = false;
        initRecorder();
        prepareRecorder();
        //Start uploading the last file
        mRecorder.start();
        mRecording = true;
        mStopTimer = new Timer();
        mStopTimer.schedule( new TimerTask() {
            @Override
            public void run() {
                Looper.prepare();
                restartRecorder();
            }
        }, 60 * 1000 );
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if( !mPrepared ) {
            prepareRecorder();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mRecording) {
            mRecorder.stop();
            mRecording = false;
            //mRecorder.release();
        }
        mRecorder.release();
    }
}