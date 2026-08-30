package net.kdt.pojavlaunch;

import android.content.Intent;
import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class ModBrowserActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ModAdapter adapter;
    private EditText editSearch;
    private Button btnInstall, btnSearchApi;
    private Spinner spinnerMcVersion;
    private ProgressBar progressBar;
    private List<ModrinthApi.ModResult> results = new ArrayList<>();
    private Set<String> selectedIds = new HashSet<>();
    private String currentType = "mod";
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String[] MC_VERSIONS = {"1.21.1","1.20.6","1.20.4","1.20.1","1.19.2","1.18.2","1.16.5","1.12.2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_browser);

        recycler = findViewById(R.id.recyclerMods);
        editSearch = findViewById(R.id.editSearch);
        btnInstall = findViewById(R.id.btnInstallSelected);
        btnSearchApi = findViewById(R.id.btnSearchApi);
        progressBar = findViewById(R.id.progressSearch);

        spinnerMcVersion = findViewById(R.id.spinnerMcVersion);
        spinnerMcVersion.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, MC_VERSIONS));

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModAdapter(results, this::onModSelected);
        recycler.setAdapter(adapter);

        btnSearchApi.setOnClickListener(v -> searchOnline());

        btnInstall.setOnClickListener(v -> installSelected());

        findViewById(R.id.btnFabric).setOnClickListener(v -> {
            currentType = "mod";
            searchOnline();
        });

        findViewById(R.id.btnForge).setOnClickListener(v -> {
            currentType = "shader";
            searchOnline();
        });

        // Buscar populares ao abrir
        searchOnline();
    }

    private void searchOnline() {
        String query = editSearch.getText().toString().trim();
        String mcVer = spinnerMcVersion.getSelectedItem().toString();

        progressBar.setVisibility(android.view.View.VISIBLE);
        btnSearchApi.setEnabled(false);

        new Thread(() -> {
            try {
                List<ModrinthApi.ModResult> found;
                if (query.isEmpty()) {
                    found = ModrinthApi.search("optimization", currentType, mcVer);
                } else {
                    found = ModrinthApi.search(query, currentType, mcVer);
                }

                mainHandler.post(() -> {
                    results.clear();
                    results.addAll(found);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(android.view.View.GONE);
                    btnSearchApi.setEnabled(true);
                    Toast.makeText(this, found.size() + " resultados", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnSearchApi.setEnabled(true);
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void onModSelected(ModrinthApi.ModResult mod, boolean selected) {
        if (selected) selectedIds.add(mod.id);
        else selectedIds.remove(mod.id);
        btnInstall.setText("Instalar (" + selectedIds.size() + ")");
        btnInstall.setEnabled(!selectedIds.isEmpty());
    }

    private void installSelected() {
        String mcVer = spinnerMcVersion.getSelectedItem().toString();
        btnInstall.setEnabled(false);
        btnInstall.setText("Instalando...");

        new Thread(() -> {
            int installed = 0;
            for (String id : selectedIds) {
                try {
                    List<ModrinthApi.VersionFile> files = ModrinthApi.getFiles(id, mcVer);
                    for (ModrinthApi.VersionFile f : files) {
                        Intent dl = new Intent(this, ModDownloadService.class);
                        dl.putExtra(ModDownloadService.EXTRA_URL, f.url);
                        dl.putExtra(ModDownloadService.EXTRA_FILENAME, f.filename);
                        dl.putExtra(ModDownloadService.EXTRA_TYPE, currentType);
                        startService(dl);
                        installed++;
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    mainHandler.post(() ->
                        Toast.makeText(this, "Erro em " + id + ": " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
                }
            }
            final int count = installed;
            mainHandler.post(() -> {
                btnInstall.setText("Instalar (" + selectedIds.size() + ")");
                btnInstall.setEnabled(true);
                selectedIds.clear();
                Toast.makeText(this, count + " arquivo(s) baixando...", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}
