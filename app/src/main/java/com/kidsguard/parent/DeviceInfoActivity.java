package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

public class DeviceInfoActivity extends Activity {

    String deviceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        final TextView tv = new TextView(this);
        tv.setTextSize(15);
        tv.setPadding(40, 40, 40, 40);
        tv.setText("Loading...");
        setContentView(tv);

        setTitle("Device Info");

        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/info.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONObject info = new JSONObject(json);
                    final String text = "Model: " + info.optString("model") +
                            "\nManufacturer: " + info.optString("manufacturer") +
                            "\nAndroid: " + info.optString("android") +
                            "\nSDK: " + info.optString("sdk") +
                            "\nDevice ID: " + info.optString("device_id") +
                            "\nLast Update: " + info.optString("time");
                    runOnUiThread(() -> tv.setText(text));
                } catch (Exception e) {}
            }
        }).start();
    }
}
