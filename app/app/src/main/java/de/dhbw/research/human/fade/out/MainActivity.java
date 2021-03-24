package de.dhbw.research.human.fade.out;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appyvet.materialrangebar.RangeBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.dhbw.research.human.fade.out.flir.FlirDevice;

public class MainActivity extends AppCompatActivity {

    private String[] neededPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private FlirDevice flirDevice;

    private FloatingActionButton startButton;
    private TextView lowerTemperatureView;
    private TextView upperTemperatureView;

    private FloatingActionButton stopButton;
    private FloatingActionButton captureButton;
    private FloatingActionButton photoButton;
    private FloatingActionButton resetButton;
    private View temperatureSelection;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

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

        captureButton = findViewById(R.id.capture_video);
        photoButton = findViewById(R.id.take_photo);
        resetButton = findViewById(R.id.reset);
        temperatureSelection = findViewById(R.id.temperature_selection);

//        findViewById(R.id.action_settings).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onSettingsButtonClicked();
//            }
//        });

        lowerTemperatureView = findViewById(R.id.lower_temperature);
        float lowerTemperature = (sharedPreferences.getInt(getString(R.string.lower_temperature_value_key), 29815) - 27315) / 100F;
        lowerTemperatureView.setText(String.format(getString(R.string.temperature_current_value), lowerTemperature));

        upperTemperatureView = findViewById(R.id.upper_temperature);
        float upperTemperature = (sharedPreferences.getInt(getString(R.string.upper_temperature_value_key), 30815) - 27315) / 100F;
        upperTemperatureView.setText(String.format(getString(R.string.temperature_current_value), upperTemperature));

        RangeBar rangeBar = findViewById(R.id.temperature_seek);
        rangeBar.setRangePinsByValue(lowerTemperature, upperTemperature);
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {
                double lowerValue = Double.parseDouble(rangeBar.getLeftPinValue());
                double upperValue = Double.parseDouble(rangeBar.getRightPinValue());
                onTemperatureChanged(lowerValue, upperValue);
            }
        });

        while (!permissionsGranted()) {
            requestPermissions();
        }

        enableStoppedState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        flirDevice.stop();
        flirDevice = null;

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                onSettingsButtonClicked();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
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

    private void onTemperatureChanged(double lowerValue, double upperValue) {
        int lowerTemperature = (int) (27315 + (lowerValue * 100));
        int upperTemperature = (int) (27315 + (upperValue * 100));

        lowerTemperatureView.setText(String.format(getString(R.string.temperature_current_value), lowerValue));
        upperTemperatureView.setText(String.format(getString(R.string.temperature_current_value), upperValue));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.lower_temperature_value_key), lowerTemperature);
        editor.putInt(getString(R.string.upper_temperature_value_key), upperTemperature);
        editor.apply();
    }

    private void enableStartedState() {
        startButton.setEnabled(false);
        startButton.hide();

        stopButton.show();
        captureButton.show();
        photoButton.show();
        resetButton.show();
        temperatureSelection.setVisibility(View.VISIBLE);

    }

    private void enableStoppedState() {
        startButton.setEnabled(true);
        startButton.show();

        stopButton.hide();
        captureButton.hide();
        photoButton.hide();
        resetButton.hide();
        temperatureSelection.setVisibility(View.INVISIBLE);

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
