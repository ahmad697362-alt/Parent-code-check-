package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class AppBlockActivity extends Activity {

    String deviceCode;
    LinearLayout layout;
    Set<String> blocked = new HashSet<>();
    Handler handler = new Handler();
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        ScrollView scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);
        scroll.addView(layout);
        setContentView(scroll);
        setTitle("App Blocking");

        addLoadingText("Loading blocked apps...");
        loadBlocked();
    }

    private void addLoadingText(String msg) {
        runOnUiThread(() -> {
            layout.removeAllViews();
            TextView tv = new TextView(this);
            tv.setText(msg);
            tv.setTextColor(0xFF888888);
            tv.setTextSize(14);
            layout.addView(tv);
        });
    }

    private void loadBlocked() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/blockedapps.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONArray arr = new JSONArray(json);
                    for (int i = 0; i < arr.length(); i++) {
                        blocked.add(arr.getString(i));
                    }
                } catch (Exception e) {}
            }
            runOnUiThread(() -> loadApps());
        }).start();
    }

    private void loadApps() {
        FirebaseHelper.sendCommand(deviceCode, "get_installed_apps", "");
        addLoadingText("Requesting app list from child device...\nPlease wait...");

        retryCount = 0;
        handler.postDelayed(this::tryLoadApps, 3000);
    }

    private void tryLoadApps() {
        new Thread(() -> {
            String json = FirebaseHelper.get(
                    "devices/" + deviceCode + "/installed_apps.json");

            if (json != null && !json.equals("null")) {
                try {
                    JSONArray arr = new JSONArray(json);
                    runOnUiThread(() -> showApps(arr));
                    return;
                } catch (Exception e) {}
            }

            retryCount++;
            if (retryCount < MAX_RETRIES) {
                runOnUiThread(() -> addLoadingText(
                        "Waiting for child device... (attempt " + (retryCount + 1) + "/" + MAX_RETRIES + ")"));
                handler.postDelayed(this::tryLoadApps, 3000);
            } else {
                runOnUiThread(() -> {
                    layout.removeAllViews();
                    TextView tv = new TextView(this);
                    tv.setText("Failed to load app list.\nMake sure child device is online.\nTry again later.");
                    tv.setTextColor(0xFFFF4444);
                    tv.setTextSize(14);
                    layout.addView(tv);
                });
            }
        }).start();
    }

    private void showApps(JSONArray apps) {
        layout.removeAllViews();

        Button btnUnblockAll = new Button(this);
        btnUnblockAll.setText("UNBLOCK ALL APPS");
        btnUnblockAll.setBackgroundColor(0xFF2E7D32);
        btnUnblockAll.setTextColor(0xFFFFFFFF);
        btnUnblockAll.setOnClickListener(v -> {
            FirebaseHelper.sendCommand(deviceCode, "unblock_all", "");
            blocked.clear();
            Toast.makeText(this, "All apps unblocked", Toast.LENGTH_SHORT).show();
        });
        layout.addView(btnUnblockAll);

        try {
            for (int i = 0; i < apps.length(); i++) {
                JSONObject app = apps.getJSONObject(i);
                final String name = app.optString("name", "");
                final String pkg = app.optString("pkg", "");

                CheckBox cb = new CheckBox(this);
                cb.setText(name + "\n" + pkg);
                cb.setTextSize(14);
                cb.setTextColor(0xFFFFFFFF);
                cb.setPadding(0, 16, 0, 16);
                cb.setChecked(blocked.contains(pkg));

                cb.setOnCheckedChangeListener((btn, checked) -> {
                    if (checked) {
                        FirebaseHelper.sendCommand(deviceCode, "block_app", pkg);
                        blocked.add(pkg);
                    } else {
                        FirebaseHelper.sendCommand(deviceCode, "unblock_app", pkg);
                        blocked.remove(pkg);
                    }
                });

                layout.addView(cb);
            }
        } catch (Exception e) {
            TextView tv = new TextView(this);
            tv.setText("Error loading apps: " + e.getMessage());
            tv.setTextColor(0xFFFF4444);
            layout.addView(tv);
        }
    }
}
