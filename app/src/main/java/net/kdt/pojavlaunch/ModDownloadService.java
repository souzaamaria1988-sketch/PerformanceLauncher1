package net.kdt.pojavlaunch;

import android.app.*;
import android.content.Intent;
import android.os.*;
import androidx.core.app.NotificationCompat;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ModDownloadService extends Service {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_FILENAME = "filename";
    public static final String EXTRA_TYPE = "type"; // mod, shader, resourcepack

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private NotificationManager notifManager;
    private static final int NOTIF_ID = 1001;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String url = intent.getStringExtra(EXTRA_URL);
        String filename = intent.getStringExtra(EXTRA_FILENAME);
        String type = intent.getStringExtra(EXTRA_TYPE);

        if (url != null && filename != null) {
            executor.submit(() -> downloadFile(url, filename, type));
        }
        return START_NOT_STICKY;
    }

    private void downloadFile(String urlStr, String filename, String type) {
        try {
            updateNotification("Baixando " + filename, 0);

            File dir = getDir(type);
            dir.mkdirs();
            File output = new File(dir, filename);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "PerformanceLauncher/1.0");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);

            int total = conn.getContentLength();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            FileOutputStream out = new FileOutputStream(output);

            byte[] buffer = new byte[8192];
            int read, downloaded = 0;
            long lastUpdate = 0;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                downloaded += read;

                if (System.currentTimeMillis() - lastUpdate > 500 && total > 0) {
                    int pct = (int) (downloaded * 100L / total);
                    updateNotification("Baixando " + filename + " (" + pct + "%)", pct);
                    lastUpdate = System.currentTimeMillis();
                }
            }

            out.flush();
            out.close();
            in.close();
            conn.disconnect();

            updateNotification("✔ " + filename + " instalado!", 100);
            notifyComplete(filename);

        } catch (Exception e) {
            updateNotification("✗ Erro: " + e.getMessage(), -1);
        }
    }

    private File getDir(String type) {
        File base = getExternalFilesDir(null);
        if (base == null) base = getFilesDir();
        File mcDir = new File(base, ".minecraft");
        switch (type != null ? type : "mod") {
            case "shader": return new File(mcDir, "shaderpacks");
            case "resourcepack": return new File(mcDir, "resourcepacks");
            default: return new File(mcDir, "mods");
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel("downloads",
                "Downloads", NotificationManager.IMPORTANCE_LOW);
            notifManager.createNotificationChannel(ch);
        }
    }

    private void updateNotification(String text, int progress) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, "downloads")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("PerformanceLauncher")
            .setContentText(text)
            .setOngoing(progress >= 0 && progress < 100);

        if (progress >= 0 && progress < 100) {
            b.setProgress(100, progress, false);
        } else if (progress == 100) {
            b.setSmallIcon(android.R.drawable.stat_sys_download_done);
        }

        notifManager.notify(NOTIF_ID, b.build());
    }

    private void notifyComplete(String filename) {
        new Handler(Looper.getMainLooper()).post(() ->
            android.widget.Toast.makeText(this, filename + " instalado!",
                android.widget.Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
