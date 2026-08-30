package net.kdt.pojavlaunch;

public class ModItem {
    public String name;
    public String description;
    public String downloads;
    public boolean selected;

    public ModItem(String name, String description, String downloads, boolean selected) {
        this.name = name;
        this.description = description;
        this.downloads = downloads;
        this.selected = selected;
    }
}
