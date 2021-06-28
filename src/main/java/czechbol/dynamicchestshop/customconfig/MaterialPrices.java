package czechbol.dynamicchestshop.customconfig;

import org.bukkit.plugin.Plugin;

public class MaterialPrices extends CustomConfigFile {

    public MaterialPrices(String fileName, Plugin plugin) {
        super(fileName, plugin);
    }

    public void initDefaults() {

        getConfig().set("STONE", 10);
        getConfig().set("DIRT", 20);
        getConfig().set("GRASS_BLOCK", 30);

        saveConfig();
    }
}
