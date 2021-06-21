package czechbol.dynamicchestshop.staticshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static czechbol.dynamicchestshop.staticshop.ChestShop.*;

public class ChestShopHandler implements Listener {

    /**
     * todo: Locale
     * todo: check if chest is there and invulnerable it
     * */
    @EventHandler
    public void OnSignPlace(SignChangeEvent e) {
        var block = e.getBlock();
        var player = e.getPlayer();

        if (!e.getLine(NAME_LINE).equalsIgnoreCase("[ChestShop]")) return;

        e.setLine(NAME_LINE, player.getName());

        try {
            var quantity = Integer.parseInt(e.getLine(QUANTITY_LINE).strip());
            e.setLine(QUANTITY_LINE, String.format("%d", quantity));
        } catch (Exception exp) {
            player.sendMessage("ChestShop: required quantity must be Integer");
            block.setType(Material.AIR);
            return;
        }

        try {
            e.setLine(PRICES_LINE, ChestShop.formatPrices(e.getLine(PRICES_LINE)));
        } catch (Exception exp) {
            player.sendMessage("ChestShop: could not be created");
            block.setType(Material.AIR);
            return;
        }

        var material = Material.matchMaterial(e.getLine(MATERIAL_LINE));

        if (material != null) {
            e.setLine(MATERIAL_LINE, material.toString());
        } else {
            player.sendMessage("ChestShop: Invalid item");
        }

        //todo: set invulnerability

        player.sendMessage("Chestshop was created");
    }

    /**
     * todo: No buy price osetrenie
     * todo: economy
     * todo: handle events pls
     * */
    @EventHandler
    public void OnSignInteract(PlayerInteractEvent e) {
        var block = e.getClickedBlock();

        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            //todo: check if chestshop

            var quantity = Integer.parseInt(sign.getLine(QUANTITY_LINE));
            var material = Material.getMaterial(sign.getLine(MATERIAL_LINE));
            var player = e.getPlayer();

            var action = e.getAction();

            switch (action) {
                case LEFT_CLICK_BLOCK -> {
                    var price = ChestShop.getBuyPrice(sign.getLine(PRICES_LINE));
                    DynamicChestShop.getEcon().withdrawPlayer(player, price);
                    player.getInventory().addItem(new ItemStack(material, quantity));
                }

                case RIGHT_CLICK_BLOCK -> {
                    var price = ChestShop.getSellPrice(sign.getLine(PRICES_LINE));
                    DynamicChestShop.getEcon().depositPlayer(player, price);
                    player.getInventory().remove(new ItemStack(material, quantity));
                }

                default -> throw new IllegalStateException("Unexpected value: " + action);
            }
        }
    }
}
