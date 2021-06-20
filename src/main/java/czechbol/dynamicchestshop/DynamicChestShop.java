package czechbol.dynamicchestshop;

import czechbol.dynamicchestshop.staticshop.Handler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import czechbol.dynamicchestshop.staticshop.ChestShop;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DynamicChestShop extends JavaPlugin {
    FileConfiguration config = getConfig();
    public static HashMap<Location, ChestShop> shops = new HashMap<>();


    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("DynamicChestShop has started UP.");
        Bukkit.getServer().getPluginManager().registerEvents(new Handler(), this);

        //Setup Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
