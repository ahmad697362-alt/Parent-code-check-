package com.kidsguard.parent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

public class ContactBlockActivity extends Activity {

    String deviceCode;
    LinearLayout layout;
    EditText etNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 40, 40, 40);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Block Phone Numbers");
        tvTitle.setTextSize(20);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, 20);
        root.addView(tvTitle);

        etNumber = new EditText(this);
        etNumber.setHint("Phone number to block");
        etNumber.setTextColor(0xFFFFFFFF);
        etNumber.setHintTextColor(0xFF666666);
        root.addView(etNumber);

        Button btnAdd = new Button(this);
        btnAdd.setText("BLOCK NUMBER");
        btnAdd.setBackgroundColor(0xFFD32F2F);
        btnAdd.setTextColor(0xFFFFFFFF);
        btnAdd.setOnClickListener(v -> addNumber());
        root.addView(btnAdd);

        ScrollView scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 30, 0, 0);
        scroll.addView(layout);
        root.addView(scroll);

        setContentView(root);
        setTitle("Contact Block");
        loadNumbers();
    }

    private void loadNumbers() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/blockedcontacts.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONArray arr = new JSONArray(json);
                    runOnUiThread(() -> {
                        layout.removeAllViews();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                addNumberRow(arr.getString(i));
                            } catch (Exception e) {}
                        }
                    });
                } catch (Exception e) {}
            }
        }).start();
    }

    private void addNumberRow(String number) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv = new TextView(this);
        tv.setText(number);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(16);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tv.setLayoutParams(tvParams);
        row.addView(tv);

        Button btnDel = new Button(this);
        btnDel.setText("X");
        btnDel.setBackgroundColor(0xFFD32F2F);
        btnDel.setTextColor(0xFFFFFFFF);
        btnDel.setOnClickListener(v -> removeNumber(number));
        row.addView(btnDel);

        layout.addView(row);
    }

    private void addNumber() {
        String number = etNumber.getText().toString().trim();
        if (number.isEmpty()) return;

        FirebaseHelper.sendCommand(deviceCode, "block_contact", number);
        etNumber.setText("");
        Toast.makeText(this, "Number blocked", Toast.LENGTH_SHORT).show();

        // Also save to firebase list
        new Thread(() -> {
            try {
                String json = FirebaseHelper.get("devices/" + deviceCode + "/blockedcontacts.json");
                JSONArray arr;
                if (json != null && !json.equals("null")) {
                    arr = new JSONArray(json);
                } else {
                    arr = new JSONArray();
                }
                arr.put(number);
                FirebaseHelper.put("devices/" + deviceCode + "/blockedcontacts.json", arr.toString());
                runOnUiThread(this::loadNumbers);
            } catch (Exception e) {}
        }).start();
    }

    private void removeNumber(String number) {
        FirebaseHelper.sendCommand(deviceCode, "unblock_contact", number);
        new Thread(() -> {
            try {
                String json = FirebaseHelper.get("devices/" + deviceCode + "/blockedcontacts.json");
                if (json != null && !json.equals("null")) {
                    JSONArray arr = new JSONArray(json);
                    JSONArray newArr = new JSONArray();
                    for (int i = 0; i < arr.length(); i++) {
                        if (!arr.getString(i).equals(number)) {
                            newArr.put(arr.getString(i));
                        }
                    }
                    FirebaseHelper.put("devices/" + deviceCode + "/blockedcontacts.json", newArr.toString());
                    runOnUiThread(this::loadNumbers);
                }
            } catch (Exception e) {}
        }).start();
    }
}
