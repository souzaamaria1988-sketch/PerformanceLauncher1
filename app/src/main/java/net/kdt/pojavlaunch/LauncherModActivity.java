package net.kdt.pojavlaunch;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherModActivity extends AppCompatActivity {
    private static final String[] VERSIONS={"1.21.1","1.20.6","1.20.4","1.20.1","1.19.4","1.19.2","1.18.2","1.17.1","1.16.5","1.15.2","1.14.4","1.13.2","1.12.2","1.11.2","1.10.2","1.9.4","1.8.9","1.7.10"};

    private PerformanceConfig perfConfig;
    private Spinner spVersion, spPreset;
    private TextView textConfigInfo;

    @Override
    protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_launcher_mod);

        perfConfig = new PerformanceConfig();
        perfConfig.loadPrefs(this);

        spVersion = findViewById(R.id.spinnerVersion);
        spVersion.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, VERSIONS));

        // Seletor de Preset
        spPreset = findViewById(R.id.spinnerPreset);
        String[] presetNames = new String[PerformanceConfig.Preset.values().length];
        for (int i = 0; i < PerformanceConfig.Preset.values().length; i++) {
            presetNames[i] = PerformanceConfig.Preset.values()[i].label;
        }
        spPreset.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, presetNames));
        spPreset.setSelection(perfConfig.getPreset().ordinal());

        textConfigInfo = findViewById(R.id.textConfigInfo);
        updateConfigInfo();

        spPreset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
                PerformanceConfig.Preset preset = PerformanceConfig.Preset.values()[pos];
                perfConfig.applyPreset(preset, LauncherModActivity.this);
                updateConfigInfo();
                Toast.makeText(LauncherModActivity.this,
                    "Preset " + preset.label + " aplicado!", Toast.LENGTH_SHORT).show();
            }
            public void onNothingSelected(AdapterView<?> p) {}
        });

        // Botões
        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            requestOverlayPermission();
            startFloatingSettings();
            String ver = spVersion.getSelectedItem().toString();
            Toast.makeText(this, "Iniciando Minecraft " + ver +
                " [" + perfConfig.getPreset().label + "]", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnMods).setOnClickListener(v ->
            startActivity(new Intent(this, ModBrowserActivity.class)));

        findViewById(R.id.btnDownload).setOnClickListener(v -> {
            String ver = spVersion.getSelectedItem().toString();
            Toast.makeText(this, "Baixando " + ver + "...", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateConfigInfo() {
        PerformanceConfig.Preset p = perfConfig.getPreset();
        textConfigInfo.setText(String.format(
            "Res: %dx%d | RAM: %dMB | Dist: %d | FPS: %s",
            p.width, p.height, p.maxRamMB, p.renderDistance,
            perfConfig.isShowFps() ? "ON" : "OFF"
        ));
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())));
        }
    }

    private void startFloatingSettings() {
        startService(new Intent(this, FloatingSettingsService.class));
    }
}
