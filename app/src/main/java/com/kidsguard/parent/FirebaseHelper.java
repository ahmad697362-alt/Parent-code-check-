package com.kidsguard.parent;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseHelper {

    private static final String BASE_URL =
            "https://easy-earn-971e4-default-rtdb.firebaseio.com/";

    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;

    // ==================== PUT (overwrite) ====================
    public static void put(final String path, final String jsonData) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(BASE_URL + path);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                OutputStream os = conn.getOutputStream();
                os.write(jsonData.getBytes("UTF-8"));
                os.flush();
                os.close();
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ==================== POST (append — FIXED: ye naya method hai) ====================
    public static void post(final String path, final String jsonData) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(BASE_URL + path);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                OutputStream os = conn.getOutputStream();
                os.write(jsonData.getBytes("UTF-8"));
                os.flush();
                os.close();
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ==================== GET (with timeout — FIXED) ====================
    public static String get(final String path) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) return null;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ==================== GET with callback (async) ====================
    public interface Callback {
        void onResponse(String json);
    }

    public static void getAsync(final String path, final Callback callback) {
        new Thread(() -> {
            String result = get(path);
            if (callback != null) callback.onResponse(result);
        }).start();
    }

    // ==================== DELETE ====================
    public static void delete(final String path) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(BASE_URL + path);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    // ==================== escapeJson — FIXED: proper escaping ====================
    public static String escapeJson(String s) {
        if (s == null) return "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("v", s);
            String full = obj.toString();
            int start = full.indexOf(":\"") + 2;
            int end = full.lastIndexOf("\"");
            if (start > 1 && end > start) return full.substring(start, end);
            return s;
        } catch (Exception e) {
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f");
        }
    }

    // ==================== sendCommand ====================
    public static void sendCommand(String deviceCode, String cmd, String arg) {
        try {
            JSONObject json = new JSONObject();
            json.put("cmd", cmd);
            json.put("arg", arg != null ? arg : "");
            put("devices/" + deviceCode + "/commands.json", json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
