package czechbol.dynamicchestshop;

import czechbol.dynamicchestshop.dynamicshop.AdminShopDestroyCmd;
import czechbol.dynamicchestshop.dynamicshop.AdminShopHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import czechbol.dynamicchestshop.dynamicshop.AdminShop;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

public final class DynamicChestShop extends JavaPlugin {
    private static FileConfiguration config;
    public static HashMap<Location, AdminShop> shops = new HashMap<>();
    private static Economy econ = null;
    private static Permission perms = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("[DynamicChestShop] Has started UP.");
        Bukkit.getServer().getPluginManager().registerEvents(new AdminShopHandler(), this);
        Bukkit.getServer().getPluginCommand("toggleadmindestroy").setExecutor(new AdminShopDestroyCmd());

        //Vault hooks
        setupPermissions();
        setupEconomy();

        //Setup Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        config = getConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setupEconomy() {
        try {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            econ = rsp.getProvider();
        } catch (NullPointerException e) {
            getLogger().log(Level.SEVERE, "[DynamicChestShop] Could not load Vault economy");
        }
    }

    private void setupPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            perms = rsp.getProvider();
        } catch (NullPointerException e) {
            getLogger().log(Level.SEVERE, "[DynamicChestShop] Could not load Vault permissions");
        }
    }

    public static Economy getEcon() {
        return econ;
    }

    public static Permission getPerms() {
        return perms;
    }

    public static FileConfiguration getConf() {
        return config;
    }
}
