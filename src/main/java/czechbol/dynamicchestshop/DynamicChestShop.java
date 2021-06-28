package czechbol.dynamicchestshop;

import czechbol.dynamicchestshop.commands.AddToBalance;
import czechbol.dynamicchestshop.commands.AdminShopDestroy;
import czechbol.dynamicchestshop.commands.ClearBalance;
import czechbol.dynamicchestshop.commands.ReloadCustomConfigs;
import czechbol.dynamicchestshop.customconfig.CustomConfigFile;
import czechbol.dynamicchestshop.customconfig.MaterialPrices;
import czechbol.dynamicchestshop.dynamicshop.AdminShop;
import czechbol.dynamicchestshop.dynamicshop.AdminShopHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DynamicChestShop extends JavaPlugin {
    private static FileConfiguration config;
    private static HashMap<Location, AdminShop> shops = new HashMap<>();
    private static Economy econ = null;
    private static Permission perms = null;
    private static String prefix = null;
    private static HashMap<String, CustomConfigFile> customConfigs = new HashMap<>();
    private static final Logger pluginLogger = Bukkit.getLogger();

    @Override
    public void onEnable() {
        //Setup Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        config = getConfig();
        if (!verifyConfigFile()) { //disable plugin if bad config file
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Setup custom configuration files
        CustomConfigFile materialPrices = new MaterialPrices("material_prices.yml", this);
        customConfigs.put(materialPrices.getFileName(), materialPrices);

        for (String confName : customConfigs.keySet()){
            customConfigs.get(confName).initConfig();
            customConfigs.get(confName).initDefaults();
        }

        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("General.Prefix"));

        //Register listeners and command executors
        Bukkit.getServer().getPluginManager().registerEvents(new AdminShopHandler(), this);
        Bukkit.getServer().getPluginCommand("toggleadmindestroy").setExecutor(new AdminShopDestroy());
        Bukkit.getServer().getPluginCommand("addtobalance").setExecutor(new AddToBalance());
        Bukkit.getServer().getPluginCommand("clearbalance").setExecutor(new ClearBalance());
        Bukkit.getServer().getPluginCommand("reloadconfigs").setExecutor(new ReloadCustomConfigs());

        //Vault hooks, disable plugin if vault hooking failed
        if (!setupEconomy() || !setupPermissions()) Bukkit.getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        try {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            econ = rsp.getProvider();
        } catch (NullPointerException e) {
            getLogger().log(Level.SEVERE, "[DynamicChestShop] Could not load Vault economy");
            return false;
        }
        return true;
    }

    private boolean setupPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            perms = rsp.getProvider();
        } catch (NullPointerException e) {
            getLogger().log(Level.SEVERE, "[DynamicChestShop] Could not load Vault permissions");
            return false;
        }
        return true;
    }

    private boolean verifyConfigFile() {
        String param = null;

        var mode = config.getInt("General.Mode");
        if (mode < 0 || mode > 3) param = "Mode";

        var prefix = config.getString("General.Prefix");
        if (prefix == null) param = "Prefix";

        var steepness = config.getInt("General.Steepness");
        if(steepness < 2000) param = "Steepness";

        var difference = config.getDouble("General.Difference");
        if (difference <= 0 || difference > 1) param = "Difference";

        if (param != null) {
            getLogger().log(Level.SEVERE, "[DynamicChestShop] Could not verify config file parameter: " + param);
            return false;
        }
        return true;
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

    public static String getPluginPrefix() {
        return prefix;
    }

    public static HashMap<String, CustomConfigFile> getCustomConfigs(){
        return customConfigs;
    }

    public static Logger getPluginLogger() {
        return pluginLogger;
    }
}
