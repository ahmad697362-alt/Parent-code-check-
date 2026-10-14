package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScreenTimeActivity extends Activity {

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

        setTitle("Screen Time");

        // Request screen time data
        new Thread(() -> {
            try {
                JSONObject cmd = new JSONObject();
                cmd.put("cmd", "get_screen_time");
                cmd.put("arg", "");
                FirebaseHelper.put("devices/" + deviceCode + "/commands.json", cmd.toString());
            } catch (Exception e) {}
        }).start();

        // Wait then load
        new android.os.Handler().postDelayed(() -> loadScreenTime(), 4000);
    }

    private void loadScreenTime() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/screen_time.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONArray arr = new JSONArray(json);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject app = arr.getJSONObject(i);
                        final String pkg = app.optString("pkg", "");
                        final long ms = app.optLong("time_ms", 0);
                        final int mins = (int) (ms / 60000);
                        runOnUiThread(() -> addEntry(pkg, mins));
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    private void addEntry(String pkg, int mins) {
        TextView tv = new TextView(this);
        tv.setText(pkg + ": " + mins + " min");
        tv.setTextSize(14);
        tv.setPadding(0, 8, 0, 8);
        layout.addView(tv);
    }
}
