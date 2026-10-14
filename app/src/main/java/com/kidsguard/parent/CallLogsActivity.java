package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class CallLogsActivity extends Activity {

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

        setTitle("Call Logs");

        new Thread(() -> {
            try {
                JSONObject cmd = new JSONObject();
                cmd.put("cmd", "get_call_logs");
                cmd.put("arg", "");
                FirebaseHelper.put("devices/" + deviceCode + "/commands.json", cmd.toString());
            } catch (Exception e) {}
        }).start();

        new android.os.Handler().postDelayed(() -> loadData(), 4000);
    }

    private void loadData() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/call_logs.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONArray arr = new JSONArray(json);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject call = arr.getJSONObject(i);
                        final String number = call.optString("number", "");
                        final String type = call.optString("type", "");
                        final String duration = call.optString("duration", "");
                        runOnUiThread(() -> addEntry(number, type, duration));
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    private void addEntry(String number, String type, String duration) {
        String typeStr;
        switch (type) {
            case "1": typeStr = "Incoming"; break;
            case "2": typeStr = "Outgoing"; break;
            case "3": typeStr = "Missed"; break;
            default: typeStr = type;
        }
        TextView tv = new TextView(this);
        tv.setText(typeStr + ": " + number + " (" + duration + "s)\n");
        tv.setTextSize(14);
        tv.setPadding(0, 8, 0, 8);
        layout.addView(tv);
    }
}
