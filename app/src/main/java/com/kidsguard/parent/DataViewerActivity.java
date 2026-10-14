package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class DataViewerActivity extends Activity {

    String deviceCode;
    String path;
    String fallbackPath;
    String title;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");
        path = getIntent().getStringExtra("path");
        fallbackPath = getIntent().getStringExtra("fallback_path");
        title = getIntent().getStringExtra("title");

        ScrollView scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        scroll.addView(layout);
        setContentView(scroll);
        setTitle(title != null ? title : "Data");

        addLoadingText();
        loadData();
    }

    private void addLoadingText() {
        TextView tv = new TextView(this);
        tv.setText("Loading data...");
        tv.setTextColor(0xFF888888);
        tv.setTextSize(14);
        layout.addView(tv);
    }

    private void loadData() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/" + path);

            if ((json == null || json.equals("null") || json.isEmpty())
                    && fallbackPath != null && !fallbackPath.isEmpty()) {
                json = FirebaseHelper.get("devices/" + deviceCode + "/" + fallbackPath);
            }

            final String finalJson = json;
            runOnUiThread(() -> {
                layout.removeAllViews();
                if (finalJson != null && !finalJson.equals("null") && !finalJson.isEmpty()) {
                    displayData(finalJson);
                } else {
                    addNoDataText();
                }
            });
        }).start();
    }

    private void displayData(String json) {
        try {
            if (json.startsWith("{")) {
                JSONObject obj = new JSONObject(json);
                displayJsonObject(obj, 0);
            } else if (json.startsWith("[")) {
                JSONArray arr = new JSONArray(json);
                displayJsonArray(arr);
            } else {
                TextView tv = new TextView(this);
                tv.setText(json);
                tv.setTextSize(14);
                tv.setTextColor(0xFFFFFFFF);
                layout.addView(tv);
            }
        } catch (Exception e) {
            TextView tv = new TextView(this);
            tv.setText(json);
            tv.setTextSize(13);
            tv.setTextColor(0xFFCCCCCC);
            layout.addView(tv);
        }
    }

    private void displayJsonObject(JSONObject obj, int indent) {
        try {
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = obj.get(key);

                TextView tv = new TextView(this);
                String padding = "";
                for (int i = 0; i < indent; i++) padding += "  ";

                if (value instanceof JSONObject) {
                    tv.setText(padding + "📁 " + key + ":");
                    tv.setTextColor(0xFF44AAFF);
                    tv.setTextSize(14);
                    layout.addView(tv);
                    displayJsonObject((JSONObject) value, indent + 1);
                } else if (value instanceof JSONArray) {
                    tv.setText(padding + "📋 " + key + " [" + ((JSONArray) value).length() + " items]:");
                    tv.setTextColor(0xFF44AAFF);
                    tv.setTextSize(14);
                    layout.addView(tv);
                    displayJsonArray((JSONArray) value);
                } else {
                    tv.setText(padding + key + ": " + value.toString());
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setTextSize(13);
                    tv.setPadding(0, 4, 0, 4);
                    layout.addView(tv);
                }
            }
        } catch (Exception e) {}
    }

    private void displayJsonArray(JSONArray arr) {
        try {
            for (int i = 0; i < arr.length(); i++) {
                Object item = arr.get(i);
                if (item instanceof JSONObject) {
                    TextView separator = new TextView(this);
                    separator.setText("─────────────────");
                    separator.setTextColor(0xFF444444);
                    layout.addView(separator);
                    displayJsonObject((JSONObject) item, 1);
                } else {
                    TextView tv = new TextView(this);
                    tv.setText("  • " + item.toString());
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setTextSize(13);
                    layout.addView(tv);
                }
            }
        } catch (Exception e) {}
    }

    private void addNoDataText() {
        TextView tv = new TextView(this);
        tv.setText("No data found.\nThe command may still be processing.\nGo back and try again in a few seconds.");
        tv.setTextColor(0xFF888888);
        tv.setTextSize(14);
        layout.addView(tv);
    }
}
