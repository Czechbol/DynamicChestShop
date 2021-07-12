package czechbol.dynamicchestshop.dynamicshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import czechbol.dynamicchestshop.customconfig.CustomConfigFile;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.SQLException;

public class Price {
    private static final Connection conn = DynamicChestShop.getConn();

    private final int id;
    private final Material material;
    private int buyPrice;
    private int sellPrice;
    private double steepness;
    private boolean active;

    public Price(Material material) throws SQLException {
        var stmt = conn.prepareStatement("SELECT * FROM material_prices WHERE material = ?");
        stmt.setMaxRows(1);
        stmt.setString(1, material.toString());
        var set = stmt.executeQuery();
        set.next();

        this.id = set.getInt("id");
        this.material = Material.getMaterial(set.getString("material"));
        this.buyPrice = set.getInt("buy_price");
        this.sellPrice = set.getInt("sell_price");
        this.steepness = set.getDouble("steepness");
        this.active = set.getBoolean("active");
    }

    public void update() throws SQLException {
        var stmt = conn.prepareStatement("""
                UPDATE material_prices SET buy_price = ?, sell_price = ?, steepness = ?, active = ? WHERE id = ?
                """);
        stmt.setInt(1, this.buyPrice);
        stmt.setInt(2, this.sellPrice);
        stmt.setDouble(3, this.steepness);
        stmt.setBoolean(4, this.active);
        stmt.setInt(5, this.id);

        stmt.executeUpdate();
    }

    public static void add(Material material, int buy_price, int sell_price, double steepness) throws SQLException {
        Price price = null;
        try {
            price = new Price(material);
        } catch (SQLException e) {
            var stmt = conn.prepareStatement("""
                INSERT INTO material_prices (
                    material, buy_price, sell_price, steepness, active
                ) VALUES (
                    ?, ?, ?, ?, ?
                )
                """);
            stmt.setString(1, material.toString());
            stmt.setInt(2, buy_price);
            stmt.setInt(3, sell_price);
            stmt.setDouble(4, steepness);
            stmt.setBoolean(5, true);

            stmt.executeUpdate();
            return;
        }

        price.setActive(true);
        price.update();
    }

    public static void createTable() throws SQLException {
        var stmt = conn.createStatement();
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS material_prices (
                    id SERIAL PRIMARY KEY,
                    material varchar(36),
                    buy_price int NOT NULL,
                    sell_price int NOT NULL,
                    steepness float NOT NULL,
                    active bool NOT NULL DEFAULT(false),
                    CONSTRAINT material_unique UNIQUE (material)
                );
                """);
    }

    public int getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public double getSteepness() {
        return steepness;
    }

    public boolean isActive() {
        return active;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setSteepness(double steepness) {
        this.steepness = steepness;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
