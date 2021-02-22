package de.dhbw.research.human.fade.out;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView serverIpInput;
    private TextView serverPortInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = this.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);

        findViewById(R.id.save_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButonClicked();
            }
        });

        serverIpInput = findViewById(R.id.server_ip_input);
        serverIpInput.setText(sharedPreferences.getString(getString(R.string.server_ip_key), ""));

        serverPortInput = findViewById(R.id.server_port_input);
        serverPortInput.setText(sharedPreferences.getInt(getString(R.string.server_port_key), 0));
    }

    private void onSaveButonClicked() {
        saveServerPort();
        saveServerIp();
    }

    private void saveServerIp() {
        String value = serverIpInput.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.server_ip_key), value);
        editor.apply();
    }


    private void saveServerPort() {
        int value = Integer.parseInt(serverPortInput.getText().toString());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.server_port_key), value);
        editor.apply();
    }
}
