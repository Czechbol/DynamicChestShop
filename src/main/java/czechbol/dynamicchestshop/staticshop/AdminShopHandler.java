package czechbol.dynamicchestshop.staticshop;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static czechbol.dynamicchestshop.staticshop.ChestShop.*;

class AdminShopHandler implements Listener {
    @EventHandler
    public void onSignEdit(SignChangeEvent e) {
        var block = e.getBlock();
        var player = e.getPlayer();

        if (!e.getLine(NAME_LINE).equalsIgnoreCase("[AdminShop]")) return;
        if (!player.hasPermission("chestshop.adminshop")) {
            e.getPlayer().sendMessage("You don't have permission to create AdminShop");
            return;
        }

        e.setLine(NAME_LINE, "[AdminShop]");

        try {
            var quantity = Integer.parseInt(e.getLine(QUANTITY_LINE).strip());
            e.setLine(QUANTITY_LINE, String.format("%d", quantity));
        } catch (Exception exp) {
            player.sendMessage("AdminShop: required quantity must be Integer");
            block.setType(Material.AIR);
            return;
        }

        try {
            e.setLine(PRICES_LINE, ChestShop.formatPrices(e.getLine(PRICES_LINE)));
        } catch (Exception exp) {
            player.sendMessage("AdminShop: could not be created");
            block.setType(Material.AIR);
            return;
        }

        var material = Material.matchMaterial(e.getLine(MATERIAL_LINE));

        if (material != null) {
            e.setLine(MATERIAL_LINE, material.toString());
        } else {
            player.sendMessage("AdminShop: Invalid item");
        }

        //todo: set invulnerability

        player.sendMessage("AdminShop was created");
    }

    /**
     * todo: No buy price ošetrenie
     * todo: economy
     * todo: handle events pls
     * */
    @EventHandler
    public void OnSignInteract(PlayerInteractEvent e) {
        var block = e.getClickedBlock();

        if (block.getState() instanceof Sign sign) {
            if (!sign.getLine(NAME_LINE).equals("[AdminShop]")) return;

            var quantity = Integer.parseInt(sign.getLine(QUANTITY_LINE));
            var material = Material.getMaterial(sign.getLine(MATERIAL_LINE));
            var player = e.getPlayer();

            var action = e.getAction();

            switch (action) {
                case LEFT_CLICK_BLOCK -> {
                    var price = ChestShop.getBuyPrice(sign.getLine(PRICES_LINE));

                    player.getInventory().addItem(new ItemStack(material, quantity));
                }
                case RIGHT_CLICK_BLOCK -> {
                    var price = ChestShop.getSellPrice(sign.getLine(PRICES_LINE));

                    player.getInventory().remove(new ItemStack(material, quantity));
                }
                default -> throw new IllegalStateException("Unexpected value: " + action);
            }
        }
    }
}
