package net.kdt.pojavlaunch;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherModActivity extends AppCompatActivity {
    
    private Spinner spinnerVersion;
    private Button btnPlay, btnMods, btnDownload;
    
    private static final String[] VERSIONS = {
        "1.21.1", "1.21", "1.20.6", "1.20.4", "1.20.2", "1.20.1",
        "1.19.4", "1.19.2", "1.18.2", "1.17.1", "1.16.5", "1.15.2",
        "1.14.4", "1.13.2", "1.12.2", "1.11.2", "1.10.2", "1.9.4",
        "1.8.9", "1.7.10"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_mod);
        setupVersionSelector();
        setupButtons();
    }
    
    private void setupVersionSelector() {
        spinnerVersion = findViewById(R.id.spinnerVersion);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_dropdown_item, VERSIONS
        );
        spinnerVersion.setAdapter(adapter);
        spinnerVersion.setSelection(0);
    }
    
    private void setupButtons() {
        btnPlay = findViewById(R.id.btnPlay);
        btnMods = findViewById(R.id.btnMods);
        btnDownload = findViewById(R.id.btnDownload);
        
        btnPlay.setOnClickListener(v -> {
            String version = spinnerVersion.getSelectedItem().toString();
            Toast.makeText(this, "Iniciando Minecraft " + version, Toast.LENGTH_SHORT).show();
        });
        
        btnMods.setOnClickListener(v -> {
            Toast.makeText(this, "Mod browser (Fase 2)", Toast.LENGTH_SHORT).show();
        });
        
        btnDownload.setOnClickListener(v -> {
            String version = spinnerVersion.getSelectedItem().toString();
            Toast.makeText(this, "Baixando " + version + "...", Toast.LENGTH_SHORT).show();
        });
    }
}
