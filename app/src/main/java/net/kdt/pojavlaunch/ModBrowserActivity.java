package net.kdt.pojavlaunch;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class ModBrowserActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ModAdapter adapter;
    private EditText editSearch;
    private Button btnInstall;
    private List<ModItem> allMods = new ArrayList<>();
    private List<ModItem> selectedMods = new ArrayList<>();

    // Mods populares pré-carregados (Fase 3: buscar da API real)
    private static final String[][] POPULAR_MODS = {
        {"Sodium", "Otimização de renderização - até 3x mais FPS", "4.2M"},
        {"Lithium", "Otimização de lógica do jogo e IA", "3.8M"},
        {"Starlight", "Reescrita do sistema de iluminação", "2.1M"},
        {"Entity Culling", "Não renderiza entidades invisíveis", "1.9M"},
        {"FerriteCore", "Reduz uso de RAM em até 40%", "2.5M"},
        {"ModernFix", "Corrige problemas de performance", "1.7M"},
        {"Iris Shaders", "Suporte a shaders com Sodium", "3.2M"},
        {"Indium", "Compatibilidade Fabric Rendering API", "1.4M"},
        {"LazyDFU", "Inicialização mais rápida", "1.1M"},
        {"Smooth Boot", "Carregamento suave na inicialização", "980K"},
        {"Dynamic FPS", "Reduz FPS quando em segundo plano", "1.3M"},
        {"Enhanced Block Entities", "Otimiza renderização de blocos", "1.6M"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_browser);

        recycler = findViewById(R.id.recyclerMods);
        editSearch = findViewById(R.id.editSearch);
        btnInstall = findViewById(R.id.btnInstallSelected);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        for (String[] m : POPULAR_MODS) {
            allMods.add(new ModItem(m[0], m[1], m[2], false));
        }

        adapter = new ModAdapter(allMods, item -> {
            updateInstallButton();
        });
        recycler.setAdapter(adapter);

        editSearch.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) { filter(s.toString()); }
            public void afterTextChanged(android.text.Editable s) {}
        });

        btnInstall.setOnClickListener(v -> installSelected());
        findViewById(R.id.btnFabric).setOnClickListener(v ->
            Toast.makeText(this, "Filtrando: Fabric", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnForge).setOnClickListener(v ->
            Toast.makeText(this, "Filtrando: Forge", Toast.LENGTH_SHORT).show());
    }

    private void filter(String query) {
        List<ModItem> filtered = new ArrayList<>();
        for (ModItem m : allMods) {
            if (m.name.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(m);
            }
        }
        adapter.updateList(filtered);
    }

    private void updateInstallButton() {
        selectedMods.clear();
        for (ModItem m : allMods) {
            if (m.selected) selectedMods.add(m);
        }
        btnInstall.setText("Instalar Selecionados (" + selectedMods.size() + ")");
        btnInstall.setEnabled(!selectedMods.isEmpty());
    }

    private void installSelected() {
        StringBuilder sb = new StringBuilder("Instalando: ");
        for (ModItem m : selectedMods) {
            sb.append(m.name).append(", ");
        }
        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        // TODO Fase 3: download real dos .jar
    }
}
