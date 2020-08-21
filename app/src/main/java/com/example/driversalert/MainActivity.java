package com.example.driversalert;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.driversalert.camera.CameraSource;
import com.example.driversalert.camera.CameraSourcePreview;
import com.example.driversalert.camera.facedetection.FaceDetectionProcessor;
import com.example.driversalert.camera.facedetection.GraphicOverlay;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements FaceDetectionProcessor.OnEyeDetectionListener {

    private CameraSource mCameraSource;
    private CameraSourcePreview mCameraSourcePreview;
    private GraphicOverlay mGraphicOverlay;
    private Button btnStopAlert;

    private List<Float> mLeftEyeLevelValues;
    private List<Float> mRightEyeLevelValues;

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraSourcePreview = findViewById(R.id.camera_source_preview_main);
        mGraphicOverlay = findViewById(R.id.graphic_overlay_main);
        btnStopAlert = findViewById(R.id.btn_stop_alert);

        mLeftEyeLevelValues = new ArrayList<>();
        mRightEyeLevelValues = new ArrayList<>();

        mCameraSource = new CameraSource(this, mGraphicOverlay);
        mCameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
        mCameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor(MainActivity.this));

        mMediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(100, 100);

        btnStopAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer == null) return;

                mMediaPlayer.pause();
                startFaceDetection();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFaceDetection();
    }

    private void startFaceDetection() {
        try {
            mCameraSourcePreview.start(mCameraSource, mGraphicOverlay);
            btnStopAlert.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraSourcePreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void eyeOpenLevel(FirebaseVisionFace face) {
        float leftEye = face.getLeftEyeOpenProbability();
        float rightEye = face.getRightEyeOpenProbability();

        if (mLeftEyeLevelValues.size() == 5) {
            processEyeValues();
            mLeftEyeLevelValues.clear();
            mRightEyeLevelValues.clear();
        }
        mLeftEyeLevelValues.add(leftEye);
        mRightEyeLevelValues.add(rightEye);

        Log.d("Left", "MainActivity -> eyeOpenLevel -> " + leftEye);
        Log.d("Right", "MainActivity -> eyeOpenLevel -> " + rightEye);
    }

    private void processEyeValues() {
        boolean sleeping = true;
        for (int i = 0; i < mLeftEyeLevelValues.size(); i++) {
            if (mLeftEyeLevelValues.get(i) > 0.30) {
                sleeping = false;
            }
        }

        
        if (sleeping) {
            Log.d("Sleeping", "true");
            mMediaPlayer.start();
            btnStopAlert.setVisibility(View.VISIBLE);
            mCameraSourcePreview.stop();
            return;
        }

        sleeping = true;

        for (int i = 0; i < mRightEyeLevelValues.size(); i++) {
            if (mRightEyeLevelValues.get(i) > 0.30) {
                sleeping = false;
            }
        }

        if (sleeping) {
            Log.d("Sleeping", "true");
            mMediaPlayer.start();
            mCameraSourcePreview.stop();
            btnStopAlert.setVisibility(View.VISIBLE);
        }
    }
}
