package czechbol.dynamicchestshop.staticshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class Handler implements Listener {
    static final int NAME_LINE = 0;
    static final int QUANTITY_LINE = 1;
    static final int PRICES_LINE = 2;
    static final int MATERIAL_LINE = 3;

    @EventHandler
    public void OnSignPlace(SignChangeEvent e) {
        var block = e.getBlock();

        if (!e.getLine(NAME_LINE).equalsIgnoreCase("[ChestShop]")) return;

        var owner = e.getPlayer();

        owner.sendMessage(e.getLines());

        e.setLine(NAME_LINE, owner.getName());

        var quantity = Integer.parseInt(e.getLine(QUANTITY_LINE).strip());
        var result = ChestShop.price_pattern.matcher(e.getLine(PRICES_LINE));
        var buy_price = Float.parseFloat(result.group("buy"));
        var sell_price = Float.parseFloat(result.group("sell"));
        var material = Material.getMaterial(e.getLine(MATERIAL_LINE));

        var sign_location = block.getLocation();

        var chestshop = new ChestShop(e.getPlayer(), sign_location, sell_price, buy_price, material, quantity);
        DynamicChestShop.shops.put(sign_location, chestshop);
        owner.sendMessage("Chestshop was created");
    }

    /**
     * Can be simplified to read first line of chestshop, but you still must verify if it is even a shop
     */
    @EventHandler
    public void OnSignDestroy(BlockBreakEvent e) {
        var block = e.getBlock();
        ChestShop shop = DynamicChestShop.shops.get(block.getLocation());

        if (shop != null) {
            if (shop.owner.getPlayer() != e.getPlayer()) {
                e.setCancelled(true);
            }
        }
    }

    private boolean isChestShop(Sign sign) {
        return sign.getLine(NAME_LINE).equalsIgnoreCase("[ChestShop]");
    }
}
