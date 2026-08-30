package net.kdt.pojavlaunch;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class FpsOverlayService extends Service {
    
    private TextView fpsText;
    private WindowManager windowManager;
    private Handler handler = new Handler();
    private boolean running = true;
    
    @Override
    public IBinder onBind(Intent intent) { return null; }
    
    @Override
    public void onCreate() {
        super.onCreate();
        createOverlay();
        startFpsMonitor();
    }
    
    private void createOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        fpsText = new TextView(this);
        fpsText.setTextColor(0xFF00FF00);
        fpsText.setTextSize(14);
        fpsText.setPadding(8, 4, 8, 4);
        fpsText.setBackgroundColor(0x80000000);
        fpsText.setText("FPS: --");
        
        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
            : WindowManager.LayoutParams.TYPE_PHONE;
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 10;
        params.y = 10;
        
        windowManager.addView(fpsText, params);
    }
    
    private void startFpsMonitor() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    Runtime rt = Runtime.getRuntime();
                    long ram = (rt.totalMemory() - rt.freeMemory()) / 1048576;
                    fpsText.setText("FPS: -- | RAM: " + ram + "MB");
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        if (fpsText != null) windowManager.removeView(fpsText);
    }
}
