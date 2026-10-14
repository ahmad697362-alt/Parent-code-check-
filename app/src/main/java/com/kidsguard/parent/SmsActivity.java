package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class SmsActivity extends Activity {

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

        setTitle("SMS Messages");

        new Thread(() -> {
            try {
                JSONObject cmd = new JSONObject();
                cmd.put("cmd", "get_sms");
                cmd.put("arg", "");
                FirebaseHelper.put("devices/" + deviceCode + "/commands.json", cmd.toString());
            } catch (Exception e) {}
        }).start();

        new android.os.Handler().postDelayed(() -> loadData(), 4000);
    }

    private void loadData() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/sms.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONArray arr = new JSONArray(json);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject sms = arr.getJSONObject(i);
                        final String from = sms.optString("from", "");
                        final String body = sms.optString("body", "");
                        runOnUiThread(() -> addEntry(from, body));
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    private void addEntry(String from, String body) {
        TextView tv = new TextView(this);
        tv.setText("From: " + from + "\n" + body + "\n");
        tv.setTextSize(13);
        tv.setPadding(0, 8, 0, 8);
        layout.addView(tv);
    }
}
