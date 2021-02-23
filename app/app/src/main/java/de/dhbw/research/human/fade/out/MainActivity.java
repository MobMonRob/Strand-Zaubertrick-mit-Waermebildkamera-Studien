package de.dhbw.research.human.fade.out;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.dhbw.research.human.fade.out.flir.FlirDevice;

public class MainActivity extends AppCompatActivity {

    private String[] neededPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private FlirDevice flirDevice;

    private FloatingActionButton startButton;
    private TextView currentTemperatureView;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);

        createFlirDevice();

        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartButtonClicked();
            }
        });

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingsButtonClicked();
            }
        });

        currentTemperatureView = findViewById(R.id.current_temperature);
        currentTemperatureView.setText(String.format(getString(R.string.temperature_current_value), sharedPreferences.getInt(getString(R.string.temperature_value_key), 0)));
        ((SeekBar) findViewById(R.id.temperature_seek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onTemperatureChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

    private void onSettingsButtonClicked() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void onTemperatureChanged(int value) {
        int temperature = 29815 + (value * 10);

        currentTemperatureView.setText(String.format(getString(R.string.temperature_current_value), temperature));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.temperature_value_key), temperature);
        editor.apply();
    }

    private void enableStartedState() {
        startButton.setEnabled(false);
    }

    private void enableStoppedState() {
        startButton.setEnabled(true);
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
