package com.kidsguard.parent;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdminControlActivity extends Activity {

    String deviceCode;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(30, 30, 30, 30);
        scroll.addView(container);
        setContentView(scroll);

        setTitle("Device Admin & Policy Manager");
        buildUI();
    }

    private void buildUI() {
        TextView header = new TextView(this);
        header.setText("Device Admin Control");
        header.setTextSize(20);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setTextColor(0xFFFFFFFF);
        header.setPadding(0, 0, 0, 20);
        container.addView(header);

        addBtn("CHECK ADMIN STATUS", "get_admin_status", "");
        addBtn("DPM INFO", "get_dpm_info", "");
        addBtn("INSTANT LOCK SCREEN", "lock_screen", "");
        addBtn("REMOTE LOCK", "remote_lock", "");
        addBtn("SET / RESET SCREEN PIN", "set_pin", "INPUT");
        addBtn("RESET PASSWORD", "reset_password", "");
        addBtn("DISABLE CAMERA", "disable_camera", "");
        addBtn("ENABLE CAMERA", "set_camera_disabled", "false");
        addBtn("DISABLE SCREEN CAPTURE", "disable_screen_capture", "");
        addBtn("ENABLE SCREEN CAPTURE", "set_screen_capture_disabled", "false");
        addBtn("SET AUTO LOCK TIMEOUT", "set_lock_timeout", "30");
        addBtn("SET SCREEN TIMEOUT", "set_screen_timeout", "30000");
        addBtn("SET MAX FAILED PASSWORDS", "set_max_failed_passwords", "5");
        addBtn("SET LOCK SCREEN MESSAGE", "set_lockscreen_message", "INPUT");
        addBtn("SET PASSWORD QUALITY", "set_password_quality", "");
        addBtn("SET PASSWORD RULES", "set_password_rules", "");
        addBtn("SET ENCRYPTION", "set_storage_encryption", "");
        addBtn("DISABLE KEYGUARD", "set_keyguard_disabled", "");
        addBtn("START KIOSK MODE", "set_kiosk_mode", "");
        addBtn("EXIT KIOSK MODE", "start_lock_task", "");
        addBtn("ADD USER RESTRICTION", "add_user_restriction", "INPUT");
        addBtn("SET RESTRICTION", "set_restriction", "INPUT");
        addBtn("CLEAR RESTRICTION", "clear_user_restriction", "INPUT");
        addBtn("WIPE DEVICE", "wipe_device", "");
        addBtn("FACTORY RESET", "factory_reset", "");
        addBtn("REBOOT DEVICE", "reboot_device", "");

        Button btnClose = new Button(this);
        btnClose.setText("CLOSE ADMIN PANEL");
        btnClose.setBackgroundColor(0xFF37474F);
        btnClose.setTextColor(0xFFFFFFFF);
        btnClose.setOnClickListener(v -> finish());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 20, 0, 20);
        btnClose.setLayoutParams(lp);
        container.addView(btnClose);
    }

    private void addBtn(String label, String cmd, String arg) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setTextColor(0xFFFFFFFF);
        btn.setTextSize(13);
        btn.setBackgroundColor(0xFFD32F2F);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 6, 0, 6);
        btn.setLayoutParams(lp);
        btn.setPadding(20, 24, 20, 24);

        btn.setOnClickListener(v -> {
            if ("INPUT".equals(arg)) {
                showInputDialog(label, cmd);
            } else {
                FirebaseHelper.sendCommand(deviceCode, cmd, arg);
                Toast.makeText(this, "Sent: " + cmd, Toast.LENGTH_SHORT).show();
            }
        });

        container.addView(btn);
    }

    private void showInputDialog(String label, String cmd) {
        final EditText et = new EditText(this);
        et.setHint("Enter value");
        et.setPadding(50, 30, 50, 30);

        new AlertDialog.Builder(this)
                .setTitle(label)
                .setView(et)
                .setPositiveButton("SEND", (dialog, which) -> {
                    String val = et.getText().toString().trim();
                    if (!val.isEmpty()) {
                        FirebaseHelper.sendCommand(deviceCode, cmd, val);
                        Toast.makeText(this, "Sent: " + cmd, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }
}
