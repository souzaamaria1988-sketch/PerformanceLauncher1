package net.kdt.pojavlaunch;

import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ModrinthApi {

    private static final String BASE = "https://api.modrinth.com/v2";

    public static class ModResult {
        public String id, title, description, downloads, iconUrl, projectType;
        public List<String> versions;
    }

    public static class VersionFile {
        public String url, filename, versionName;
        public long size;
    }

    // Buscar mods/shaders/resourcepacks
    public static List<ModResult> search(String query, String type, String mcVersion) throws Exception {
        String facets = String.format(
            "[[\"project_type:%s\"],[\"versions:%s\"]]",
            type, mcVersion
        );
        String urlStr = BASE + "/search?query=" + URLEncoder.encode(query, "UTF-8")
            + "&facets=" + URLEncoder.encode(facets, "UTF-8")
            + "&limit=20";

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("User-Agent", "PerformanceLauncher/1.0");
        conn.setConnectTimeout(10000);

        if (conn.getResponseCode() != 200) throw new Exception("HTTP " + conn.getResponseCode());

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        JSONObject json = new JSONObject(sb.toString());
        JSONArray hits = json.getJSONArray("hits");
        List<ModResult> results = new ArrayList<>();

        for (int i = 0; i < hits.length(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            ModResult r = new ModResult();
            r.id = hit.optString("project_id");
            r.title = hit.optString("title");
            r.description = hit.optString("description");
            r.downloads = formatDownloads(hit.optLong("downloads"));
            r.iconUrl = hit.optString("icon_url");
            r.projectType = hit.optString("project_type");
            r.versions = new ArrayList<>();
            JSONArray vers = hit.optJSONArray("versions");
            if (vers != null) for (int j = 0; j < vers.length(); j++) r.versions.add(vers.getString(j));
            results.add(r);
        }
        return results;
    }

    // Pegar arquivos da última versão compatível
    public static List<VersionFile> getFiles(String projectId, String mcVersion) throws Exception {
        String urlStr = BASE + "/project/" + projectId + "/version?game_versions=[\"" + mcVersion + "\"]&limit=1";

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("User-Agent", "PerformanceLauncher/1.0");
        conn.setConnectTimeout(10000);

        if (conn.getResponseCode() != 200) throw new Exception("HTTP " + conn.getResponseCode());

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        JSONArray versions = new JSONArray(sb.toString());
        List<VersionFile> files = new ArrayList<>();

        if (versions.length() > 0) {
            JSONObject ver = versions.getJSONObject(0);
            JSONArray verFiles = ver.getJSONArray("files");
            String verName = ver.optString("version_number", "unknown");

            for (int i = 0; i < verFiles.length(); i++) {
                JSONObject f = verFiles.getJSONObject(i);
                if ("required".equals(f.optString("primary")) || i == 0) {
                    VersionFile vf = new VersionFile();
                    vf.url = f.getString("url");
                    vf.filename = f.getString("filename");
                    vf.versionName = verName;
                    vf.size = f.optLong("size");
                    files.add(vf);
                }
            }
        }
        return files;
    }

    private static String formatDownloads(long d) {
        if (d >= 1000000) return String.format("%.1fM", d / 1000000.0);
        if (d >= 1000) return String.format("%.1fK", d / 1000.0);
        return String.valueOf(d);
    }
}
