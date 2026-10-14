package com.kidsguard.parent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class DataViewerActivity extends Activity {

    String deviceCode, path, title;
    LinearLayout layout;
    ScrollView scroll;
    Handler handler = new Handler();
    boolean active = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviceCode = getIntent().getStringExtra("device_code");
        path = getIntent().getStringExtra("path");
        title = getIntent().getStringExtra("title");

        scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        scroll.addView(layout);
        setContentView(scroll);

        setTitle(title != null ? title : "Data Viewer");
        loadData();
    }

    private void loadData() {
        layout.removeAllViews();

        // Add refresh button at top
        Button btnRefresh = new Button(this);
        btnRefresh.setText("🔄 Refresh / Check Result");
        btnRefresh.setOnClickListener(v -> loadData());
        layout.addView(btnRefresh);

        TextView loading = new TextView(this);
        loading.setText("\nWaiting for device response...");
        loading.setTextSize(15);
        loading.setTextColor(0xFF00FF88);
        layout.addView(loading);

        new Thread(() -> {
            try {
                final String json = FirebaseHelper.get("devices/" + deviceCode + "/" + path);
                runOnUiThread(() -> {
                    if (!active) return;
                    // Remove loading view if present
                    if (layout.getChildCount() > 1) {
                        layout.removeViewAt(1);
                    }

                    if (json == null || json.equals("null") || json.isEmpty()) {
                        TextView tv = new TextView(this);
                        tv.setText("\n[No data or result yet]\n\nNote: Make sure the child device is online and connected. Tap 'Refresh' once the command is executed.");
                        tv.setTextColor(0xFFFFAA00);
                        tv.setTextSize(14);
                        layout.addView(tv);
                        return;
                    }
                    displayJson(json);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (!active) return;
                    TextView tv = new TextView(this);
                    tv.setText("\nError fetching data:\n" + e.getMessage());
                    tv.setTextSize(14);
                    tv.setTextColor(0xFFFF5555);
                    layout.addView(tv);
                });
            }
        }).start();
    }

    private void displayJson(String json) {
        try {
            if (json.startsWith("{")) {
                JSONObject obj = new JSONObject(json);
                displayObject(obj);
            } else if (json.startsWith("[")) {
                JSONArray arr = new JSONArray(json);
                displayArray(arr);
            } else {
                TextView tv = new TextView(this);
                tv.setText("\nResult:\n" + json);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(14);
                tv.setPadding(0, 8, 0, 8);
                layout.addView(tv);
            }
        } catch (Exception e) {
            TextView tv = new TextView(this);
            tv.setText("\nRaw Result:\n" + json);
            tv.setTextColor(0xFFFFFFFF);
            tv.setTextSize(14);
            layout.addView(tv);
        }
    }

    private void displayObject(JSONObject obj) {
        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                Object val = obj.get(key);
                if (val instanceof JSONObject) {
                    TextView tv = new TextView(this);
                    tv.setText(key + ":");
                    tv.setTextColor(0xFF00FF88);
                    tv.setTextSize(14);
                    tv.setPadding(0, 12, 0, 4);
                    layout.addView(tv);
                    displayObject((JSONObject) val);
                } else if (val instanceof JSONArray) {
                    TextView tv = new TextView(this);
                    tv.setText(key + ":");
                    tv.setTextColor(0xFF00FF88);
                    tv.setTextSize(14);
                    tv.setPadding(0, 12, 0, 4);
                    layout.addView(tv);
                    displayArray((JSONArray) val);
                } else {
                    String str = val.toString();
                    if (str.length() > 500 && !str.contains(" ") && !str.contains("{")) {
                        try {
                            byte[] decoded = Base64.decode(str, Base64.NO_WRAP);
                            Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                            if (bmp != null) {
                                ImageView iv = new ImageView(this);
                                iv.setImageBitmap(bmp);
                                iv.setAdjustViewBounds(true);
                                iv.setPadding(0, 8, 0, 8);
                                layout.addView(iv);
                                continue;
                            }
                        } catch (Exception ignored) {}
                    }
                    TextView tv = new TextView(this);
                    tv.setText(key + ": " + str);
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setTextSize(13);
                    tv.setPadding(0, 4, 0, 4);
                    layout.addView(tv);
                }
            } catch (Exception e) {}
        }
    }

    private void displayArray(JSONArray arr) {
        try {
            for (int i = 0; i < arr.length(); i++) {
                Object val = arr.get(i);
                if (val instanceof JSONObject) {
                    displayObject((JSONObject) val);
                    addDivider();
                } else if (val instanceof JSONArray) {
                    displayArray((JSONArray) val);
                } else {
                    TextView tv = new TextView(this);
                    tv.setText("• " + val.toString());
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setTextSize(13);
                    tv.setPadding(0, 4, 0, 4);
                    layout.addView(tv);
                }
            }
        } catch (Exception e) {}
    }

    private void addDivider() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        v.setBackgroundColor(0xFF333333);
        v.setPadding(0, 8, 0, 8);
        layout.addView(v);
    }

    @Override protected void onResume() { super.onResume(); active = true; }
    @Override protected void onPause() { super.onPause(); active = false; }
}
