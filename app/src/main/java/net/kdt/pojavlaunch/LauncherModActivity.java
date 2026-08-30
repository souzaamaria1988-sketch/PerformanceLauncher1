package net.kdt.pojavlaunch;

import android.content.Intent;
import android.os.Bundle;
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

        findViewById(R.id.btnPlay).setOnClickListener(v->
            Toast.makeText(this,"Iniciando "+sp.getSelectedItem(),Toast.LENGTH_SHORT).show());

        // Fase 2: abre Mod Browser
        findViewById(R.id.btnMods).setOnClickListener(v->
            startActivity(new Intent(this, ModBrowserActivity.class)));
    }
}
