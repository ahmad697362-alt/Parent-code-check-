package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContactsActivity extends Activity {

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

        setTitle("Contacts");

        // Request data
        new Thread(() -> {
            try {
                JSONObject cmd = new JSONObject();
                cmd.put("cmd", "get_contacts");
                cmd.put("arg", "");
                FirebaseHelper.put("devices/" + deviceCode + "/commands.json", cmd.toString());
            } catch (Exception e) {}
        }).start();

        new android.os.Handler().postDelayed(() -> loadData(), 4000);
    }

    private void loadData() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/contacts.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONArray arr = new JSONArray(json);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject c = arr.getJSONObject(i);
                        final String name = c.optString("name", "");
                        final String phone = c.optString("phone", "");
                        runOnUiThread(() -> addEntry(name, phone));
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    private void addEntry(String name, String phone) {
        TextView tv = new TextView(this);
        tv.setText(name + "\n" + phone + "\n");
        tv.setTextSize(14);
        tv.setPadding(0, 8, 0, 8);
        layout.addView(tv);
    }
}
