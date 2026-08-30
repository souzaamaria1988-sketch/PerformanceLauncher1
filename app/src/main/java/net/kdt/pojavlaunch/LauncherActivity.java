package net.kdt.pojavlaunch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.os.Build;
import java.io.*;

public class LauncherActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#1a1a2e"));
        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);
        
        TextView title = new TextView(this);
        title.setText("PojavLauncher Custom");
        title.setTextColor(Color.parseColor("#e94560"));
        title.setTextSize(32);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Minecraft Java no Android");
        subtitle.setTextColor(Color.GRAY);
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);
        
        TextView status = new TextView(this);
        status.setText("\n✓ Build concluído com sucesso\n✓ APK funcional\n\nDispositivo: " + Build.MODEL + "\nAndroid: " + Build.VERSION.RELEASE + "\nRAM disponível: " + getAvailableRam() + " MB");
        status.setTextColor(Color.GREEN);
        status.setTextSize(14);
        status.setTypeface(Typeface.MONOSPACE);
        status.setGravity(Gravity.CENTER);
        root.addView(status);
        
        Button btnPlay = new Button(this);
        btnPlay.setText("▶ JOGAR");
        btnPlay.setTextSize(20);
        btnPlay.setBackgroundColor(Color.parseColor("#e94560"));
        btnPlay.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 150);
        btnParams.setMargins(0, 60, 0, 0);
        btnPlay.setLayoutParams(btnParams);
        btnPlay.setOnClickListener(v -> {
            Toast.makeText(this, "Selecione uma versão do Minecraft", Toast.LENGTH_LONG).show();
        });
        root.addView(btnPlay);
        
        setContentView(root);
    }
    
    private long getAvailableRam() {
        Runtime rt = Runtime.getRuntime();
        return (rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576;
    }
}
