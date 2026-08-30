package net.kdt.pojavlaunch;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.RandomAccessFile;

public class FloatingSettingsService extends Service {

    private WindowManager wm;
    private View bubbleView;
    private View panelView;
    private boolean panelVisible = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    private TextView textFps, textRam, textTemp;
    private int lastFps = 60;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        createBubble();
        startStatsMonitor();
    }

    private int getOverlayFlag() {
        return Build.VERSION.SDK_INT >= 26
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
    }

    // ═══ Bolinha flutuante ═══
    private void createBubble() {
        bubbleView = new TextView(this);
        ((TextView) bubbleView).setText("⚙");
        ((TextView) bubbleView).setTextSize(18);
        ((TextView) bubbleView).setTextColor(0xFFFFFFFF);
        bubbleView.setBackgroundColor(0xE6E94560);
        bubbleView.setPadding(20, 20, 20, 20);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getOverlayFlag(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.END;
        params.x = 20;
        params.y = 200;

        bubbleView.setOnTouchListener(new View.OnTouchListener() {
            private int lastX, lastY, dX, dY;
            private long downTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        downTime = System.currentTimeMillis();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        dX = (int) event.getRawX() - lastX;
                        dY = (int) event.getRawY() - lastY;
                        params.x -= dX;
                        params.y += dY;
                        wm.updateViewLayout(bubbleView, params);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - downTime < 300) {
                            togglePanel();
                        }
                        return true;
                }
                return false;
            }
        });

        wm.addView(bubbleView, params);
    }

    // ═══ Painel de configurações ═══
    private void togglePanel() {
        if (panelVisible) {
            wm.removeView(panelView);
            panelView = null;
            panelVisible = false;
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        panelView = inflater.inflate(R.layout.floating_settings, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getOverlayFlag(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;

        // Stats
        textFps = panelView.findViewById(R.id.textFps);
        textRam = panelView.findViewById(R.id.textRam);
        textTemp = panelView.findViewById(R.id.textTemp);

        // Resolução
        Spinner spinnerRes = panelView.findViewById(R.id.spinnerResolution);
        String[] resolutions = {"1920x1080", "1600x900", "1280x720", "960x540", "800x480", "640x360"};
        spinnerRes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, resolutions));
        spinnerRes.setSelection(2);

        // Renderizador
        Spinner spinnerRenderer = panelView.findViewById(R.id.spinnerRenderer);
        String[] renderers = {"OpenGL ES 3.0", "Vulkan (Zink)", "ANGLE", "Software"};
        spinnerRenderer.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, renderers));

        // FOV
        SeekBar seekFov = panelView.findViewById(R.id.seekFov);
        seekFov.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int p, boolean user) {}
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}
        });

        // Render Distance
        SeekBar seekDist = panelView.findViewById(R.id.seekRenderDist);

        // Fechar
        panelView.findViewById(R.id.btnClosePanel).setOnClickListener(v -> togglePanel());

        // Aplicar
        panelView.findViewById(R.id.btnApply).setOnClickListener(v -> {
            String res = spinnerRes.getSelectedItem().toString();
            String renderer = spinnerRenderer.getSelectedItem().toString();
            int fov = seekFov.getProgress();
            int dist = seekDist.getProgress();
            boolean vsync = ((Switch) panelView.findViewById(R.id.switchVsync)).isChecked();
            boolean particles = ((Switch) panelView.findViewById(R.id.switchParticles)).isChecked();

            // TODO: aplicar configurações ao renderer do PojavLauncher
            Toast.makeText(this,
                "Aplicado: " + res + " | " + renderer + " | FOV " + fov +
                " | Dist " + dist + " | VSync " + vsync,
                Toast.LENGTH_SHORT).show();
        });

        wm.addView(panelView, params);
        panelVisible = true;
    }

    // ═══ Monitor de stats ═══
    private void startStatsMonitor() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateStats();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updateStats() {
        Runtime rt = Runtime.getRuntime();
        long usedRam = (rt.totalMemory() - rt.freeMemory()) / 1048576;
        long maxRam = rt.maxMemory() / 1048576;

        // FPS simulado (TODO: pegar do renderer real)
        lastFps = 30 + (int)(Math.random() * 30);

        float temp = getCpuTemperature();

        if (textFps != null && panelVisible) {
            textFps.setText("FPS: " + lastFps);
            textRam.setText("RAM: " + usedRam + "/" + maxRam + "MB");
            textTemp.setText("TEMP: " + String.format("%.1f°C", temp));
        }
    }

    private float getCpuTemperature() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/sys/class/thermal/thermal_zone0/temp", "r");
            String temp = reader.readLine();
            reader.close();
            return Float.parseFloat(temp) / 1000.0f;
        } catch (Exception e) {
            return 35.0f + (float)(Math.random() * 10);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (bubbleView != null) wm.removeView(bubbleView);
        if (panelView != null && panelVisible) wm.removeView(panelView);
    }
}
