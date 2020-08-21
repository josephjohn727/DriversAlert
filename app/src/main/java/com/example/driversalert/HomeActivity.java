package com.example.driversalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private Button btnDashCam;
    private Button btnSleepDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnDashCam = (Button) findViewById(R.id.dash_Cam_Button);
        btnSleepDetector = (Button) findViewById(R.id.sleep_detector_button);

        btnSleepDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        btnDashCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DashCamActivity.class);
                startActivity(intent);

            }
        });

//        findViewById(R.id.btn_list_video).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),VideoActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
