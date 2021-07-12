package czechbol.dynamicchestshop;

import czechbol.dynamicchestshop.commands.*;
import czechbol.dynamicchestshop.customconfig.CustomConfigFile;
import czechbol.dynamicchestshop.customconfig.MaterialPrices;
import czechbol.dynamicchestshop.dynamicshop.AdminShop;
import czechbol.dynamicchestshop.dynamicshop.AdminShopHandler;
import czechbol.dynamicchestshop.dynamicshop.Price;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
    private static Connection conn = null;

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
        CustomConfigFile materialPrices = new MaterialPrices(config.getString("FileSettings.PricesFile"), this);
        customConfigs.put(materialPrices.getFileName(), materialPrices);

        for (String confName : customConfigs.keySet()) {
            if (customConfigs.get(confName).initConfig())
                customConfigs.get(confName).initDefaults();
        }

        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("General.Prefix"));

        //Register listeners and command executors
        Bukkit.getServer().getPluginManager().registerEvents(new AdminShopHandler(), this);
        Bukkit.getServer().getPluginCommand("toggleadmindestroy").setExecutor(new AdminShopDestroy());
        Bukkit.getServer().getPluginCommand("addtobalance").setExecutor(new AddToBalance());
        Bukkit.getServer().getPluginCommand("clearbalance").setExecutor(new ClearBalance());
        Bukkit.getServer().getPluginCommand("reloadconfigs").setExecutor(new ReloadCustomConfigs());
        Bukkit.getServer().getPluginCommand("genprices").setExecutor(new GenerateItemPrices());

        //Vault hooks, disable plugin if vault hooking failed
        if (!setupEconomy() || !setupPermissions()) Bukkit.getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setupDatabase() {
        var storage_method = config.getString("Storage.StorageMethod");
        assert storage_method != null;
        var address = config.getString("Storage.DatabaseSettings.Address");
        var database = config.getString("Storage.DatabaseSettings.Database");
        var username = config.getString("Storage.DatabaseSettings.Username");
        var password = config.getString("Storage.DatabaseSettings.Password");

        String connector = null;

        try {
            connector = switch (storage_method) {
                case "PostgreSQL" -> {
                    DriverManager.registerDriver(new org.postgresql.Driver());
                    yield "postgresql";
                }
                case "MariaDB" -> {
                    DriverManager.registerDriver(new org.mariadb.jdbc.Driver());
                    yield "mariadb";
                }
                case "MySQL" -> {
                    DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                    yield "mysql";
                }
                case "SQLite" -> {
                    yield "sqlite";
                }
                default -> throw new IllegalStateException();
            };
        } catch (SQLException | IllegalStateException ex) {
            getLogger().log(Level.SEVERE, String.format("[DynamicChestShop] cannot use %s as database", storage_method));
        }

        var db = String.format("jdbc:%s://%s:%s/%s@%s", connector, address, username, password, database);

        try {
            conn = DriverManager.getConnection(db);
        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, "[DynamicChestShop] cannot connect to database");
        }

        try {
            Price.createTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        try {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            econ = rsp.getProvider();
        } catch (NullPointerException e) {
            getLogger().log(Level.SEVERE, "Could not load Vault economy");
            return false;
        }
        return true;
    }

    private boolean setupPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            perms = rsp.getProvider();
        } catch (NullPointerException e) {
            getLogger().log(Level.SEVERE, "Could not load Vault permissions");
            return false;
        }
        return true;
    }

    private boolean verifyConfigFile() {
        String param = null;

        var mode = config.getInt("General.Mode");
        if (mode < 0 || mode > 3) param = " Mode";

        var prefix = config.getString("General.Prefix");
        if (prefix == null) param = " Prefix";

        var capacityT = config.getDouble("General.CapacityThreshold");
        if (capacityT <= 0) param = " CapacityThreshold";

        var blockhighersell = config.getBoolean("General.BlockHigherSellThanBuy");
        var difference = config.getDouble("General.Difference");
        if (difference <= 0 || (difference > 1 && blockhighersell)) param = " Difference";

        var pricesfile = config.getString("FileSettings.PricesFile");
        if (pricesfile == null) param += " PricesFile";

        if (param != null) {
            getLogger().log(Level.SEVERE, "Could not verify config parameter(s) (check your config file):" + param);
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

    public static Connection getConn() {
        return conn;
    }

    public static String getPluginPrefix() {
        return prefix;
    }

    public static HashMap<String, CustomConfigFile> getCustomConfigs() {
        return customConfigs;
    }

    public static Logger getPluginLogger() {
        return pluginLogger;
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("DynamicChestShop");
    }
}
