package com.kidsguard.parent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;

public class LiveStreamActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private WebSocketClient wsClient;
    private String deviceCode;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceCode = getIntent().getStringExtra("device_code");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0D1626);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(0xFF1A2235);
        header.setPadding(24, 20, 24, 20);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Live Screen Stream");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tvTitle.setLayoutParams(titleParams);
        header.addView(tvTitle);

        Button btnClose = new Button(this);
        btnClose.setText("CLOSE");
        btnClose.setTextColor(Color.WHITE);
        btnClose.setBackgroundColor(0xFFD32F2F);
        btnClose.setOnClickListener(v -> finish());
        header.addView(btnClose);
        root.addView(header);

        tvStatus = new TextView(this);
        tvStatus.setText("Connecting...");
        tvStatus.setTextColor(0xFF00D4FF);
        tvStatus.setTextSize(12);
        tvStatus.setPadding(24, 12, 24, 12);
        tvStatus.setBackgroundColor(0xFF131C2D);
        root.addView(tvStatus);

        surfaceView = new SurfaceView(this);
        LinearLayout.LayoutParams svParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        surfaceView.setLayoutParams(svParams);
        root.addView(surfaceView);

        setContentView(root);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        connectWebSocket();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        disconnect();
    }

    private void connectWebSocket() {
        try {
            URI uri = new URI("wss://kiddroid-server.onrender.com");
            wsClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    updateStatus("Connected! Streaming...", 0xFF00FF88);
                    try {
                        JSONObject reg = new JSONObject();
                        reg.put("type", "parent");
                        reg.put("id", deviceCode);
                        reg.put("childId", deviceCode);
                        send(reg.toString());
                    } catch (Exception e) {}
                }

                @Override
                public void onMessage(String message) {
                    try {
                        JSONObject json = new JSONObject(message);
                        if (json.has("h264")) {
                            byte[] data = Base64.decode(json.getString("h264"), Base64.DEFAULT);
                            decodeFrame(data, (byte) 0);
                        }
                    } catch (Exception e) {}
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    try {
                        byte[] data = new byte[bytes.remaining()];
                        bytes.get(data);
                        decodeFrame(data, (byte) 0);
                    } catch (Exception e) {}
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    updateStatus("Disconnected: " + reason, 0xFFFF4444);
                }

                @Override
                public void onError(Exception ex) {
                    updateStatus("Error: " + ex.getMessage(), 0xFFFF4444);
                }
            };
            wsClient.connect();
        } catch (Exception e) {
            updateStatus("Error: " + e.getMessage(), 0xFFFF4444);
        }
    }

    private void decodeFrame(byte[] data, byte frameType) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap == null) return;
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                Rect dst = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
                canvas.drawBitmap(bitmap, src, dst, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
            bitmap.recycle();
        } catch (Exception e) {}
    }

    private void updateStatus(final String text, final int color) {
        runOnUiThread(() -> {
            if (tvStatus != null) {
                tvStatus.setText(text);
                tvStatus.setTextColor(color);
            }
        });
    }

    private void disconnect() {
        if (wsClient != null) {
            try { wsClient.close(); } catch (Exception ignored) {}
            wsClient = null;
        }
    }

    @Override
    protected void onDestroy() {
        disconnect();
        super.onDestroy();
    }
}
