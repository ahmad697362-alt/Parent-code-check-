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
        loadRecordings();
    }

    private void loadRecordings() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/recordings.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONObject recs = new JSONObject(json);
                    Iterator<String> keys = recs.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject rec = recs.getJSONObject(key);
                        final String file = rec.optString("file", "");
                        final String time = rec.optString("time", "");
                        runOnUiThread(() -> addRecording(file, time));
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    private void addRecording(String file, String time) {
        TextView tv = new TextView(this);
        tv.setText("Recording at: " + time + "\nFile: " + file + "\n");
        tv.setTextSize(14);
        tv.setPadding(0, 10, 0, 10);
        layout.addView(tv);
    }
}
