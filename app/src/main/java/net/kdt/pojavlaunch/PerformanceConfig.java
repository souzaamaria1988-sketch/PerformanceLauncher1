package net.kdt.pojavlaunch;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.File;
import java.io.FileWriter;

public class PerformanceConfig {

    public static final String PREFS_NAME = "perf_config";

    public enum Preset {
        LOW("Baixo", 4, 512, 640, 360, false, false, 2),
        MEDIUM("Médio", 8, 1024, 960, 540, true, false, 4),
        HIGH("Alto", 12, 2048, 1280, 720, true, true, 8),
        ULTRA("Ultra", 16, 3072, 1600, 900, true, true, 12);

        public final String label;
        public final int renderDistance;
        public final int maxRamMB;
        public final int width, height;
        public final boolean vsync;
        public final boolean particles;
        public final int entityDistance;

        Preset(String label, int renderDistance, int maxRamMB,
               int width, int height, boolean vsync, boolean particles, int entityDistance) {
            this.label = label;
            this.renderDistance = renderDistance;
            this.maxRamMB = maxRamMB;
            this.width = width;
            this.height = height;
            this.vsync = vsync;
            this.particles = particles;
            this.entityDistance = entityDistance;
        }
    }

    private Preset currentPreset = Preset.MEDIUM;
    private int fov = 70;
    private String renderer = "opengles3";
    private boolean showFps = true;

    // Aplicar preset
    public void applyPreset(Preset preset, Context ctx) {
        this.currentPreset = preset;
        savePrefs(ctx);
        writeOptionsFile(ctx, preset);
        writeJvmArgs(ctx, preset);
    }

    // Salvar preferências
    private void savePrefs(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putString("preset", currentPreset.name())
            .putInt("fov", fov)
            .putString("renderer", renderer)
            .putBoolean("showFps", showFps)
            .apply();
    }

    // Carregar preferências
    public void loadPrefs(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        try {
            currentPreset = Preset.valueOf(prefs.getString("preset", "MEDIUM"));
        } catch (Exception e) {
            currentPreset = Preset.MEDIUM;
        }
        fov = prefs.getInt("fov", 70);
        renderer = prefs.getString("renderer", "opengles3");
        showFps = prefs.getBoolean("showFps", true);
    }

    // Escrever options.txt do Minecraft
    private void writeOptionsFile(Context ctx, Preset p) {
        try {
            File mcDir = getMinecraftDir(ctx);
            mcDir.mkdirs();
            File options = new File(mcDir, "options.txt");
            FileWriter w = new FileWriter(options);
            w.write("renderDistance:" + p.renderDistance + "\n");
            w.write("fov:" + (fov / 110.0) + "\n");
            w.write("vsync:" + p.vsync + "\n");
            w.write("particles:" + (p.particles ? "all" : "minimal") + "\n");
            w.write("entityDistanceScaling:" + (p.entityDistance / 16.0) + "\n");
            w.write("maxFps:260\n");
            w.write("graphicsMode:fast\n");
            w.write("ao:1\n");
            w.write("mipmapLevels:0\n");
            w.write("entityShadows:false\n");
            w.write("guiScale:2\n");
            w.write("fullscreen:true\n");
            w.write("simulationDistance:" + Math.min(p.renderDistance, 10) + "\n");
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Escrever JVM args otimizados
    private void writeJvmArgs(Context ctx, Preset p) {
        try {
            File mcDir = getMinecraftDir(ctx);
            mcDir.mkdirs();
            File jvmFile = new File(mcDir, "perf_jvm_args.txt");
            FileWriter w = new FileWriter(jvmFile);
            w.write("-Xmx" + p.maxRamMB + "M\n");
            w.write("-Xms" + (p.maxRamMB / 2) + "M\n");
            w.write("-XX:+UseG1GC\n");
            w.write("-XX:G1NewSizePercent=20\n");
            w.write("-XX:G1ReservePercent=20\n");
            w.write("-XX:MaxGCPauseMillis=50\n");
            w.write("-XX:G1HeapRegionSize=32M\n");
            w.write("-XX:+ParallelRefProcEnabled\n");
            w.write("-XX:+AlwaysPreTouch\n");
            w.write("-Dfile.encoding=UTF-8\n");
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getMinecraftDir(Context ctx) {
        File base = ctx.getExternalFilesDir(null);
        if (base == null) base = ctx.getFilesDir();
        return new File(base, ".minecraft");
    }

    // Getters
    public Preset getPreset() { return currentPreset; }
    public int getFov() { return fov; }
    public void setFov(int fov) { this.fov = fov; }
    public String getRenderer() { return renderer; }
    public void setRenderer(String r) { this.renderer = r; }
    public boolean isShowFps() { return showFps; }
    public void setShowFps(boolean b) { this.showFps = b; }

    public int getResolutionWidth() { return currentPreset.width; }
    public int getResolutionHeight() { return currentPreset.height; }
    public int getMaxRamMB() { return currentPreset.maxRamMB; }
    public int getRenderDistance() { return currentPreset.renderDistance; }
}
