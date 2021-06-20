package czechbol.dynamicchestshop;

import czechbol.dynamicchestshop.staticshop.Handler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import czechbol.dynamicchestshop.staticshop.ChestShop;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DynamicChestShop extends JavaPlugin {
    FileConfiguration config = getConfig();
    public static HashMap<Location, ChestShop> shops = new HashMap<>();
    private static Economy econ = null;
    private static Permission perms = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("DynamicChestShop has started UP.");
        Bukkit.getServer().getPluginManager().registerEvents(new Handler(), this);

        //Vault hooks
        setupPermissions();
        setupEconomy();

        //Setup Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static Permission getPerms() {
        return perms;
    }
}
