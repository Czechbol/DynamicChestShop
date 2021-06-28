package czechbol.dynamicchestshop.customconfig;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class MaterialPrices extends CustomConfigFile {

    public MaterialPrices(String fileName, Plugin plugin) {
        super(fileName, plugin);
    }

    public void initDefaults() {
        FileConfiguration config = getConfig(); //TODO: Initialize prices of other items
        config.set("STONE.Price", 32);
        config.set("DIAMOND.Price", 3200);
        config.set("ENDER_PEARL.Price", 320);

        saveConfig();
    }
}
