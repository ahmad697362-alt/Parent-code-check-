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
        setTitle("All Remote Commands (180)");

        commandList = generate180Commands();

        ArrayList<String> displayNames = new ArrayList<>();
        for (CommandItem item : commandList) {
            displayNames.add(item.id + ". " + item.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CommandItem cmd = commandList.get(position);
            FirebaseHelper.sendCommand(deviceCode, cmd.commandKey, "");
            Toast.makeText(this, "Executing: " + cmd.name, Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, DataViewerActivity.class);
            i.putExtra("device_code", deviceCode);
            i.putExtra("path", "result.json");
            i.putExtra("title", cmd.name);
            startActivity(i);
        });
    }

    private ArrayList<CommandItem> generate180Commands() {
        ArrayList<CommandItem> list = new ArrayList<>();
        String[] cats = {
            "Location & GPS", "Camera & Capture", "Audio & Microphone", "Calls & SMS",
            "WhatsApp & Social", "Files & Storage", "Apps & Usage", "System & Device",
            "Security & Lock", "Network & WiFi", "Browser & Internet", "Notifications & Alerts"
        };

        int count = 1;
        for (String cat : cats) {
            for (int i = 1; i <= 15; i++) {
                if (count > 180) break;
                String name = cat + " - Action " + i;
                String key = "cmd_" + count + "_" + cat.toLowerCase().replaceAll("[^a-z]", "");
                list.add(new CommandItem(count, name, key));
                count++;
            }
        }
        while (count <= 180) {
            list.add(new CommandItem(count, "General System Action " + count, "cmd_" + count));
            count++;
        }
        return list;
    }

    static class CommandItem {
        int id;
        String name;
        String commandKey;

        CommandItem(int id, String name, String commandKey) {
            this.id = id;
            this.name = name;
            this.commandKey = commandKey;
        }
    }
}
