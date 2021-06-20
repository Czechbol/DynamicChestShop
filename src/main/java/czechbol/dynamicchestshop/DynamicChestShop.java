package czechbol.dynamicchestshop;

import czechbol.dynamicchestshop.staticshop.ChestShop;
import czechbol.dynamicchestshop.staticshop.Handler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public final class DynamicChestShop extends JavaPlugin {
    public static Connection conn;
    public static HashMap<Location, ChestShop> shops = new HashMap<>();

    public static final Material[] signs = {
            Material.ACACIA_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.BIRCH_SIGN,
            Material.CRIMSON_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.DARK_OAK_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.JUNGLE_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.OAK_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.WARPED_SIGN,
            Material.WARPED_WALL_SIGN
    };

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new Handler(), this);

    }

    @Override
    public void onDisable() {
    }
}
