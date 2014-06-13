package com.homeaway.cameraremote;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int notificationId = 001;
        Intent viewIntent = new Intent(this, CameraIntentService.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Take a picture")
                        .setContentText("MEOW!")
                        .addAction(R.drawable.ic_take_video, "Record Video", getRecordVideoIntent())
                        .addAction(R.drawable.ic_take_picture, "Take Picture", getTakePictureIntent())
                        .setContentIntent(viewPendingIntent);

        Notification notification = new WearableNotifications.Builder(notificationBuilder).build();

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notification);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private PendingIntent getRecordVideoIntent() {
        PendingIntent pendingIntent;
        Intent intent = new Intent();
        intent.setClass(this, CameraReceiver.class);
        intent.putExtra(Intents.RECORD_VIDEO, true);
        pendingIntent =  PendingIntent.getActivity(this, 0, intent, 0);
        return pendingIntent;
    }

    private PendingIntent getTakePictureIntent() {
        PendingIntent pendingIntent;
        Intent intent = new Intent();
        intent.setClass(this, CameraReceiver.class);
        intent.putExtra(Intents.TAKE_PICTURE, true);
        pendingIntent =  PendingIntent.getActivity(this, 0, intent, 0);
        return pendingIntent;
    }


}
