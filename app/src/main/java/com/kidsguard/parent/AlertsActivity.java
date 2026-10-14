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
        loadAlerts();
    }

    private void loadAlerts() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/alerts.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONObject alerts = new JSONObject(json);
                    Iterator<String> keys = alerts.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject alert = alerts.getJSONObject(key);
                        final String title = alert.optString("title", "");
                        final String body = alert.optString("body", "");
                        final String time = alert.optString("time", "");
                        runOnUiThread(() -> addAlert(title, body, time));
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    private void addAlert(String title, String body, String time) {
        TextView tv = new TextView(this);
        tv.setText(title + "\n" + body + "\n" + time + "\n");
        tv.setTextSize(14);
        tv.setPadding(0, 12, 0, 12);
        layout.addView(tv);
    }
}
