package net.kdt.pojavlaunch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Color;
import android.graphics.Typeface;

public class LauncherActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setText("PojavLauncher Custom\n\nBuild concluído com sucesso!\n\nEste é um APK de teste.\nO JRE será baixado no primeiro uso.");
        tv.setTextColor(Color.GREEN);
        tv.setBackgroundColor(Color.BLACK);
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.MONOSPACE);
        
        setContentView(tv);
    }
}
