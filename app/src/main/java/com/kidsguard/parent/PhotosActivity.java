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
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Iterator;

public class PhotosActivity extends Activity {

    String deviceCode;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        ScrollView scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        scroll.addView(layout);
        setContentView(scroll);

        setTitle("Captured Photos");
        loadPhotos();
    }

    private void loadPhotos() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/photos.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONObject photos = new JSONObject(json);
                    Iterator<String> keys = photos.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject photo = photos.getJSONObject(key);
                        final String b64 = photo.optString("image", "");
                        final String time = photo.optString("time", "");

                        if (!b64.isEmpty()) {
                            runOnUiThread(() -> addPhoto(b64, time));
                        }
                    }
                } catch (Exception e) {}
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "No photos yet", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void addPhoto(String b64, String time) {
        try {
            byte[] decoded = Base64.decode(b64, Base64.NO_WRAP);
            Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);

            TextView tvTime = new TextView(this);
            tvTime.setText("Time: " + time);
            tvTime.setTextSize(12);
            tvTime.setPadding(0, 20, 0, 5);
            layout.addView(tvTime);

            ImageView iv = new ImageView(this);
            iv.setImageBitmap(bmp);
            iv.setAdjustViewBounds(true);
            iv.setPadding(0, 0, 0, 30);
            layout.addView(iv);

        } catch (Exception e) {}
    }
}
