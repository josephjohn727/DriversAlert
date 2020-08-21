package com.example.driversalert;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;


public class UploadVideo extends IntentService {

    private static final String TAG = "UploadVideo";

    private static final String VIDEO_FILE_NAME = "com.example.driversalert.video_url";

    private static final String VIDEO_FILE_FORMAT = ".mp4";

    private static String mBaseFilePath;

    public UploadVideo() {
        super("UploadVideo");
    }


    public static void startUpload(Context context, String param1) {
        File file = new File(context.getExternalFilesDir(null), "DriversAlert");
        mBaseFilePath = file.getPath();
        Intent intent = new Intent(context, UploadVideo.class);
        intent.putExtra(VIDEO_FILE_NAME, param1);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String url = intent.getStringExtra(VIDEO_FILE_NAME);
            firebaseVideoUploading(url);
        }
    }

    private void firebaseVideoUploading(String url) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        String fileName = url.substring(0, url.length() - 4);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(fileName));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String format = "%d-%d-%d %d:%d:%d.mp4";

        try {
            StorageReference storageRef = storage.getReference();
            StorageReference videoReference = storageRef.child("videos");
            StorageReference spaceRef = videoReference.child(String.format(Locale.getDefault(), format, day, month, year, hour, minute, second));

            InputStream stream = new FileInputStream(new File(mBaseFilePath + File.separator + url));
            UploadTask uploadTask = spaceRef.putStream(stream);
            makeNotification("Video uploading started");
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "------------ firebaseVideoUploading addOnSuccessListener onSuccess -----------------");
                    makeNotification("Video uploaded");
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Log.d(TAG, "------------ firebaseVideoUploading addOnCanceledListener onCanceled -----------------");
                    makeNotification("Video uploading cancelled");
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void makeNotification(String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("DriverAlert")
                        .setContentText(message)
                        .setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}