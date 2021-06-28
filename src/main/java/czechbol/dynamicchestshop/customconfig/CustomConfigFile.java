package czechbol.dynamicchestshop.customconfig;

import org.bukkit.StructureType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class CustomConfigFile {
    private final String fileName;
    private FileConfiguration config;
    private final Plugin plugin;
    private File confFile;

    public CustomConfigFile(String fileName, Plugin plugin) {
        this.fileName = fileName;
        this.plugin = plugin;
    }

    public boolean initConfig() {
        try {
             confFile = new File(plugin.getDataFolder().getPath() + "/" + fileName);

            if (!confFile.exists()) confFile.createNewFile();

            config = new YamlConfiguration();
            config.load(confFile);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public abstract void initDefaults();

    public boolean reloadConfig() {
        try {
            config.load(confFile);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveConfig(){
        try {
            config.save(confFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getFileName() {
        return fileName;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
