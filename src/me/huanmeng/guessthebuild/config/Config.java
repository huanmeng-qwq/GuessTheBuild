package me.huanmeng.guessthebuild.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {
    private FileConfiguration config;
    private File file;

    public Config(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void setLocation(String path, Location loc) {
        set(path, loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
        save();
    }

    public Location getLocation(String path) {
        String loc = (String) config.get(path);
        String[] locs = loc.split(",");
        Location l = new Location(Bukkit.getWorld(locs[0]), Double.parseDouble(locs[1]), Double.parseDouble(locs[2]), Double.parseDouble(locs[3]), Float.parseFloat(locs[4]), Float.parseFloat(locs[5]));
        return l;
    }

    public List<?> getList(String path) {
        return config.getList(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }
    public String getString(String path){
        return ChatColor.translateAlternateColorCodes('&',getConfig().get(path).toString());
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
