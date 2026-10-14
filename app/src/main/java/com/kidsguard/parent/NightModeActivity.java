package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class NightModeActivity extends Activity {

    String deviceCode;
    EditText etStart, etEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(60, 60, 60, 60);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Night Mode Lock Schedule");
        tvTitle.setTextSize(20);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, 30);
        root.addView(tvTitle);

        etStart = new EditText(this);
        etStart.setHint("Start time (e.g. 22:00)");
        etStart.setTextColor(0xFFFFFFFF);
        etStart.setHintTextColor(0xFF666666);
        root.addView(etStart);

        etEnd = new EditText(this);
        etEnd.setHint("End time (e.g. 06:00)");
        etEnd.setTextColor(0xFFFFFFFF);
        etEnd.setHintTextColor(0xFF666666);
        root.addView(etEnd);

        Button btnSet = new Button(this);
        btnSet.setText("SET NIGHT MODE");
        btnSet.setBackgroundColor(0xFF7B1FA2);
        btnSet.setTextColor(0xFFFFFFFF);
        btnSet.setOnClickListener(v -> setNightMode());
        root.addView(btnSet);

        Button btnDisable = new Button(this);
        btnDisable.setText("DISABLE NIGHT MODE");
        btnDisable.setBackgroundColor(0xFFD32F2F);
        btnDisable.setTextColor(0xFFFFFFFF);
        btnDisable.setOnClickListener(v -> {
            FirebaseHelper.sendCommand(deviceCode, "set_night_mode", "00:00,00:00");
            Toast.makeText(this, "Night mode disabled", Toast.LENGTH_SHORT).show();
        });
        root.addView(btnDisable);

        setContentView(root);
        setTitle("Night Mode");
        loadCurrent();
    }

    private void loadCurrent() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/nightmode.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONObject obj = new JSONObject(json);
                    final String start = obj.optString("start", "");
                    final String end = obj.optString("end", "");
                    runOnUiThread(() -> {
                        etStart.setText(start);
                        etEnd.setText(end);
                    });
                } catch (Exception e) {}
            }
        }).start();
    }

    private void setNightMode() {
        String start = etStart.getText().toString().trim();
        String end = etEnd.getText().toString().trim();
        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Enter both times", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseHelper.sendCommand(deviceCode, "set_night_mode", start + "," + end);

        // Save to firebase
        try {
            JSONObject obj = new JSONObject();
            obj.put("start", start);
            obj.put("end", end);
            obj.put("enabled", true);
            FirebaseHelper.put("devices/" + deviceCode + "/nightmode.json", obj.toString());
        } catch (Exception e) {}

        Toast.makeText(this, "Night mode set: " + start + " to " + end,
                Toast.LENGTH_SHORT).show();
    }
}
