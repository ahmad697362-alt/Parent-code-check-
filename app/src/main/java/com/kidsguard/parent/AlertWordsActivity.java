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

public class AlertWordsActivity extends Activity {

    String deviceCode;
    LinearLayout layout;
    EditText etWord;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 40, 40, 40);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Alert Words Monitor");
        tvTitle.setTextSize(20);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, 20);
        root.addView(tvTitle);

        etWord = new EditText(this);
        etWord.setHint("Add alert word");
        etWord.setTextColor(0xFFFFFFFF);
        etWord.setHintTextColor(0xFF666666);
        root.addView(etWord);

        btnAdd = new Button(this);
        btnAdd.setText("ADD WORD");
        btnAdd.setOnClickListener(v -> addWord());
        root.addView(btnAdd);

        ScrollView scroll = new ScrollView(this);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 30, 0, 0);
        scroll.addView(layout);
        root.addView(scroll);

        setContentView(root);
        setTitle("Alert Words");
        loadWords();
    }

    private void loadWords() {
        new Thread(() -> {
            String json = FirebaseHelper.get("devices/" + deviceCode + "/alertwords.json");
            if (json != null && !json.equals("null")) {
                try {
                    JSONArray arr = new JSONArray(json);
                    runOnUiThread(() -> {
                        layout.removeAllViews();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                addWordRow(arr.getString(i));
                            } catch (Exception e) {}
                        }
                    });
                } catch (Exception e) {}
            }
        }).start();
    }

    private void addWordRow(String word) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv = new TextView(this);
        tv.setText(word);
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
        btnDel.setOnClickListener(v -> removeWord(word));
        row.addView(btnDel);

        layout.addView(row);
    }

    private void addWord() {
        String word = etWord.getText().toString().trim();
        if (word.isEmpty()) return;

        new Thread(() -> {
            try {
                String json = FirebaseHelper.get("devices/" + deviceCode + "/alertwords.json");
                JSONArray arr;
                if (json != null && !json.equals("null")) {
                    arr = new JSONArray(json);
                } else {
                    arr = new JSONArray();
                }
                arr.put(word);
                FirebaseHelper.put("devices/" + deviceCode + "/alertwords.json", arr.toString());
                runOnUiThread(() -> {
                    etWord.setText("");
                    loadWords();
                    Toast.makeText(this, "Word added", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {}
        }).start();
    }

    private void removeWord(String word) {
        new Thread(() -> {
            try {
                String json = FirebaseHelper.get("devices/" + deviceCode + "/alertwords.json");
                if (json != null && !json.equals("null")) {
                    JSONArray arr = new JSONArray(json);
                    JSONArray newArr = new JSONArray();
                    for (int i = 0; i < arr.length(); i++) {
                        if (!arr.getString(i).equals(word)) {
                            newArr.put(arr.getString(i));
                        }
                    }
                    FirebaseHelper.put("devices/" + deviceCode + "/alertwords.json", newArr.toString());
                    runOnUiThread(this::loadWords);
                }
            } catch (Exception e) {}
        }).start();
    }
}
