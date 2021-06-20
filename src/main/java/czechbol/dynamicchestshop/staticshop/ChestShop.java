package czechbol.dynamicchestshop.staticshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;

import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Pattern;


public class ChestShop {
    public static Pattern price_pattern = Pattern.compile("B\\s*(?<buy>\\d+)\\s*:?\\s*S\\s*(?<sell>\\d+)");
    int id;
    OfflinePlayer owner;
    Location location;
    int quantity;
    float sell_price; //fixme: only buy shop?
    float buy_price; //fixme: only sell shop?
    Material material;

    public ChestShop(Location loc) {

    }

    public ChestShop(Sign sign) {

    }

    public ChestShop(OfflinePlayer owner, Location location, float sell_price, float buy_price, Material material, int quantity) {
        this.owner = owner;
        this.location = location;
        this.sell_price = sell_price;
        this.buy_price = buy_price;
        this.material = material;
        this.quantity = quantity;
    }

    /**
     * Adds chestshop entry to database
     * */
//    public void create() throws SQLException {
//        var stmt = DynamicChestShop.conn.createStatement();
//        stmt.executeQuery("INSERT INTO ")
//    }

    public void remove() {

    }

//    public static void create_table() throws SQLException {
//        var stmt = DynamicChestShop.conn.createStatement();
//        var sql = "CREATE TABLE staticshop" +
//                "(id INTEGER NOT NULL," +
//                "owner VARCHAR(16)," +
//                "location "
//    }
    /*
    public static Set<ChestShop> getPlayersShops(OfflinePlayer) {

    }*/
}
