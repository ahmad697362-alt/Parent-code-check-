package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Iterator;

public class AlertsActivity extends Activity {

    String deviceCode;
    LinearLayout layout;

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
        setTitle("Alerts");

        addLoadingText();
        loadAlerts();
    }

    private void addLoadingText() {
        TextView tv = new TextView(this);
        tv.setText("Loading alerts...");
        tv.setTextColor(0xFF888888);
        tv.setTextSize(14);
        layout.addView(tv);
    }

    private void loadAlerts() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/alerts.json");

            runOnUiThread(() -> layout.removeAllViews());

            if (json != null && !json.equals("null") && !json.isEmpty()) {
                try {
                    JSONObject alerts = new JSONObject(json);

                    if (alerts.has("title")) {
                        String title = alerts.optString("title", "");
                        String body = alerts.optString("body", "");
                        String time = alerts.optString("time", "");
                        runOnUiThread(() -> addAlert(title, body, time));
                    } else {
                        Iterator<String> keys = alerts.keys();
                        boolean hasAlerts = false;
                        while (keys.hasNext()) {
                            hasAlerts = true;
                            String key = keys.next();
                            try {
                                JSONObject alert = alerts.getJSONObject(key);
                                String title = alert.optString("title", "");
                                String body = alert.optString("body", "");
                                String time = alert.optString("time", "");
                                runOnUiThread(() -> addAlert(title, body, time));
                            } catch (Exception e) {}
                        }
                        if (!hasAlerts) {
                            runOnUiThread(() -> addNoAlertsText());
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> addErrorText("Failed to parse alerts"));
                }
            } else {
                runOnUiThread(() -> addNoAlertsText());
            }
        }).start();
    }

    private void addAlert(String title, String body, String time) {
        TextView tv = new TextView(this);
        tv.setText("⚠️ " + title + "\n" + body + "\n🕐 " + time + "\n");
        tv.setTextSize(14);
        tv.setTextColor(0xFFFFFFFF);
        tv.setPadding(0, 12, 0, 12);
        layout.addView(tv);
    }

    private void addNoAlertsText() {
        TextView tv = new TextView(this);
        tv.setText("No alerts yet.");
        tv.setTextColor(0xFF888888);
        tv.setTextSize(14);
        layout.addView(tv);
    }

    private void addErrorText(String msg) {
        TextView tv = new TextView(this);
        tv.setText("Error: " + msg);
        tv.setTextColor(0xFFFF4444);
        tv.setTextSize(14);
        layout.addView(tv);
    }
}
