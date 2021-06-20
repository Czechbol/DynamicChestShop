package czechbol.dynamicchestshop.staticshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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

        e.setLine(NAME_LINE, owner.getName());

        var quantity = Integer.parseInt(e.getLine(QUANTITY_LINE).strip());
        var prices = e.getLine(PRICES_LINE).split(":", 2);

        float buy_price = 0;
        float sell_price = 0;

        for (String price : prices) {
            var m = ChestShop.price_pattern.matcher(price);
            if (m.find()) {
                var price_str = m.group(1);

                if (price.contains("S")) {
                    sell_price = Float.parseFloat(price_str);
                } else if (price.contains("B")) {
                    buy_price = Float.parseFloat(price_str);
                }
            }
        }

        var material = Material.matchMaterial(e.getLine(MATERIAL_LINE));

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

    /**
     * todo Economy
     * */
    @EventHandler
    public void OnBuy(PlayerInteractEvent e) {
        var block = e.getClickedBlock();
        var player = e.getPlayer();

        if (block.getState() instanceof Sign) {
            ChestShop shop = DynamicChestShop.shops.get(block.getLocation());

            if (shop != null) {
                var final_price = shop.buy_price * shop.quantity;

                player.sendMessage(String.format("You bought %s for %.2f", shop.material.toString(), final_price));

                var stack = new ItemStack(shop.material, shop.quantity);

                e.getPlayer().getInventory().addItem(stack);
            }
        }
    }

    private boolean isChestShop(Sign sign) {
        return sign.getLine(NAME_LINE).equalsIgnoreCase("[ChestShop]");
    }
}
