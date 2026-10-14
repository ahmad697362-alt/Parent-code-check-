package com.kidsguard.parent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    EditText etDeviceCode;
    Button btnConnect;
    TextView tvStatus;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("parent_prefs", 0);
        etDeviceCode = findViewById(R.id.etDeviceCode);
        btnConnect = findViewById(R.id.btnConnect);
        tvStatus = findViewById(R.id.tvStatus);

        String saved = prefs.getString("device_code", "");
        if (!saved.isEmpty()) {
            openDashboard(saved);
            return;
        }

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etDeviceCode.getText().toString().trim();
                if (code.length() != 6) {
                    Toast.makeText(MainActivity.this,
                            "Enter 6-digit code", Toast.LENGTH_SHORT).show();
                    return;
                }
                verifyAndConnect(code);
            }
        });
    }

    private void verifyAndConnect(final String code) {
        tvStatus.setText("Connecting...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = FirebaseHelper.get(
                        "devices/" + code + "/info.json");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null && !result.equals("null") && !result.isEmpty()) {
                            prefs.edit().putString("device_code", code).apply();
                            Toast.makeText(MainActivity.this,
                                    "Connected!", Toast.LENGTH_SHORT).show();
                            openDashboard(code);
                        } else {
                            tvStatus.setText("Device not found.");
                        }
                    }
                });
            }
        }).start();
    }

    private void openDashboard(String code) {
        Intent i = new Intent(this, DashboardActivity.class);
        i.putExtra("device_code", code);
        startActivity(i);
        finish();
    }
}
