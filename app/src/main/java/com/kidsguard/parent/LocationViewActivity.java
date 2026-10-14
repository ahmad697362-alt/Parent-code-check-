package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import org.json.JSONObject;

public class LocationViewActivity extends Activity {

    String deviceCode;
    TextView tvLoc;
    Handler handler = new Handler();
    boolean active = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        tvLoc = new TextView(this);
        tvLoc.setTextSize(16);
        tvLoc.setPadding(40, 40, 40, 40);
        tvLoc.setText("Loading location...");
        setContentView(tvLoc);

        setTitle("Live Location");
        startRefresh();
    }

    private void startRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!active) return;
                refresh();
                handler.postDelayed(this, 5000);
            }
        }, 500);
    }

    private void refresh() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/location.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONObject loc = new JSONObject(json);
                    final String text = "Latitude: " + loc.optString("lat") +
                            "\nLongitude: " + loc.optString("lng") +
                            "\n\nAddress: " + loc.optString("address") +
                            "\n\nUpdated: " + loc.optString("time");
                    runOnUiThread(() -> tvLoc.setText(text));
                } catch (Exception e) {}
            }
        }).start();
    }

    @Override
    protected void onResume() { super.onResume(); active = true; }

    @Override
    protected void onPause() { super.onPause(); active = false; }
}
