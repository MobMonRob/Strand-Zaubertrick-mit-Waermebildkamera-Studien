package de.dhbw.research.human.fade.out;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.dhbw.research.human.fade.out.flir.FlirDevice;

public class MainActivity extends AppCompatActivity{

    private String[] neededPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private FlirDevice flirDevice;

    private FloatingActionButton startButton;
    private FloatingActionButton stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFlirDevice();

        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartButtonClicked();
            }
        });

        stopButton = findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopButtonClicked();
            }
        });

        while (!permissionsGranted()) {
            requestPermissions();
        }

        enableStoppedState();
    }

    @Override
    protected void onDestroy() {
        flirDevice.stop();
        flirDevice = null;

        super.onDestroy();
    }

    private void onStartButtonClicked() {
        if (flirDevice == null) {
            createFlirDevice();
        }

        flirDevice.start();
        enableStartedState();
    }

    private void createFlirDevice() {
        flirDevice = new FlirDevice(this);
    }

    private void onStopButtonClicked() {
        flirDevice.stop();
        enableStoppedState();
    }

    private void enableStartedState() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void enableStoppedState() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private boolean permissionsGranted() {
        boolean permissionsGranted = true;

        for (String permission : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false;
                break;
            }
        }

        return permissionsGranted;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, neededPermissions, 0);
    }
}
