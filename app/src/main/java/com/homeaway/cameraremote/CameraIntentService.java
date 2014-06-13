package com.homeaway.cameraremote;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Camera;

/**
 * I was too lazy on 6/12/14 to bother with Javadoc.
 * If you are reading this and you aren't kmiller
 * then please, for the love of everything holy,
 * publicly shame me. I SAID GOOD DAY
 */
public class CameraIntentService extends IntentService {

    private Camera mCamera;

    @Override
    protected void onHandleIntent(Intent intent) {
        mCamera  = Camera.open(100);
        destroyCamera();
    }

    private void destroyCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
