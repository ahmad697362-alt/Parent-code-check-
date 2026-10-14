package com.kidsguard.parent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Iterator;

public class PhotoViewerActivity extends Activity {

    String deviceCode;
    String mode = "photos";
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");
        mode = getIntent().getStringExtra("mode");
        if (mode == null) mode = "photos";

        ScrollView scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        scroll.addView(layout);
        setContentView(scroll);
        setTitle(mode.equals("photos") ? "Captured Photos" : "Screenshots");

        addLoadingText();
        load();
    }

    private void addLoadingText() {
        TextView tv = new TextView(this);
        tv.setText("Loading " + mode + "...");
        tv.setTextColor(0xFF888888);
        tv.setTextSize(14);
        layout.addView(tv);
    }

    private void load() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/" + mode + ".json");

            runOnUiThread(() -> layout.removeAllViews());

            if (json != null && !json.equals("null") && !json.isEmpty()) {
                try {
                    JSONObject photos = new JSONObject(json);
                    Iterator<String> keys = photos.keys();
                    boolean hasPhotos = false;

                    while (keys.hasNext()) {
                        hasPhotos = true;
                        String key = keys.next();
                        try {
                            JSONObject photo = photos.getJSONObject(key);
                            String b64 = photo.optString("image", "");
                            String time = photo.optString("time", "");
                            if (!b64.isEmpty()) {
                                runOnUiThread(() -> addPhoto(b64, time));
                            }
                        } catch (Exception e) {}
                    }

                    if (!hasPhotos) {
                        runOnUiThread(() -> addNoDataText());
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> addErrorText("Failed to parse photos"));
                }
            } else {
                runOnUiThread(() -> addNoDataText());
            }
        }).start();
    }

    private void addPhoto(String b64, String time) {
        try {
            byte[] decoded = Base64.decode(b64, Base64.NO_WRAP);
            Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            if (bmp == null) return;

            TextView tvTime = new TextView(this);
            tvTime.setText("🕐 Time: " + time);
            tvTime.setTextColor(0xFFAAAAAA);
            tvTime.setTextSize(12);
            tvTime.setPadding(0, 16, 0, 4);
            layout.addView(tvTime);

            ImageView iv = new ImageView(this);
            iv.setImageBitmap(bmp);
            iv.setAdjustViewBounds(true);
            iv.setPadding(0, 0, 0, 24);
            layout.addView(iv);
        } catch (Exception e) {}
    }

    private void addNoDataText() {
        TextView tv = new TextView(this);
        tv.setText("No " + mode + " found.\nSend 'camera' or 'screenshot' command first.");
        tv.setTextColor(0xFF888888);
        tv.setTextSize(14);
        layout.addView(tv);
    }

    private void addErrorText(String msg) {
        TextView tv = new TextView(this);
        tv.setText("Error: " + msg);
        tv.setTextColor(0xFFFF4444);
        tv.setTextSize(14);
        layout.addView(tv);
    }
}
