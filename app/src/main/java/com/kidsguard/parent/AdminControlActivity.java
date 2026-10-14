package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class AdminControlActivity extends Activity {

    String deviceCode;
    LinearLayout layout;
    TextView tvStatus;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        ScrollView scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        scroll.addView(layout);
        setContentView(scroll);
        setTitle("Device Admin Control");

        tvStatus = new TextView(this);
        tvStatus.setText("Checking admin status...");
        tvStatus.setTextSize(16);
        tvStatus.setTextColor(0xFFFFFFFF);
        tvStatus.setPadding(0, 0, 0, 30);
        layout.addView(tvStatus);

        checkAdminStatus();
        addControlButtons();
    }

    private void checkAdminStatus() {
        FirebaseHelper.sendCommand(deviceCode, "get_admin_status", "");
        handler.postDelayed(() -> {
            new Thread(() -> {
                String json = FirebaseHelper.get("devices/" + deviceCode + "/admin_status.json");
                runOnUiThread(() -> {
                    if (json != null && !json.equals("null")) {
                        try {
                            JSONObject obj = new JSONObject(json);
                            String status = obj.optString("status", "UNKNOWN");
                            if ("ENABLED".equals(status)) {
                                tvStatus.setText("✅ Device Admin: ACTIVE");
                                tvStatus.setTextColor(0xFF44FF44);
                            } else {
                                tvStatus.setText("❌ Device Admin: DISABLED");
                                tvStatus.setTextColor(0xFFFF4444);
                            }
                        } catch (Exception e) {
                            tvStatus.setText("⚠️ Status unknown");
                        }
                    } else {
                        tvStatus.setText("⚠️ Could not fetch status. Child may be offline.");
                    }
                });
            }).start();
        }, 3000);
    }

    private void addControlButtons() {
        addButton("Lock Screen Now", "lock_screen", "");
        addButton("Disable Camera", "disable_camera", "");
        addButton("Enable Camera", "set_camera_disabled", "false");
        addButton("Set PIN (1234)", "set_pin", "1234");
        addButton("Reset Password", "reset_password", "");
        addButton("Get Admin Status", "get_admin_status", "");
    }

    private void addButton(String label, String cmd, String arg) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setOnClickListener(v -> {
            FirebaseHelper.sendCommand(deviceCode, cmd, arg);
            Toast.makeText(this, "Sent: " + label, Toast.LENGTH_SHORT).show();
        });
        layout.addView(btn);
    }
}
