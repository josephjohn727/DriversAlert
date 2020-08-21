package com.example.driversalert;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayVideoActivity extends AppCompatActivity {

    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mVideoView = findViewById(R.id.videoView);

        Intent intent = getIntent();
        Uri reference_name = (Uri) intent.getExtras().get("uri_reference");
        //Toast toast = Toast.makeText(getApplicationContext(),reference_name,Toast.LENGTH_SHORT);
        //toast.show();
       // String videoURL = "https://firebasestorage.googleapis.com/v0/b/drivers-alert-51fbe.appspot.com/o/videos%2F" + reference_name + "alt=media&token=06fa8572-471c-46cc-b304-1356624c38ee";

        //Uri uri = Uri.parse(videoURL);
        mVideoView.setVideoURI(reference_name);
        mVideoView.start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mVideoView.isPlaying()) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }
}
