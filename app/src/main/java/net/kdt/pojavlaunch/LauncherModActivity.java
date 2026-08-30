package net.kdt.pojavlaunch;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherModActivity extends AppCompatActivity {
    private static final String[] VERSIONS={"1.21.1","1.20.6","1.20.1","1.19.2","1.18.2","1.16.5","1.12.2","1.8.9","1.7.10"};

    @Override
    protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_launcher_mod);

        Spinner sp=findViewById(R.id.spinnerVersion);
        sp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,VERSIONS));

        findViewById(R.id.btnPlay).setOnClickListener(v->{
            startFloatingSettings();
            Toast.makeText(this,"Iniciando "+sp.getSelectedItem(),Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnMods).setOnClickListener(v->
            startActivity(new Intent(this, ModBrowserActivity.class)));
    }

    private void startFloatingSettings() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Permita sobreposição nas configurações", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())));
            return;
        }
        startService(new Intent(this, FloatingSettingsService.class));
    }
}
