package czechbol.dynamicchestshop.staticshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
     * */
    @EventHandler
    public void OnSignInteract(PlayerInteractEvent e) {
        var action = e.getAction();

        if(!action.equals(Action.LEFT_CLICK_BLOCK)
                && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;

        var block = e.getClickedBlock();

        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            //todo: check if chestshop

            var quantity = Integer.parseInt(sign.getLine(QUANTITY_LINE));
            var material = Material.getMaterial(sign.getLine(MATERIAL_LINE));
            var player = e.getPlayer();

            switch (action) {
                case LEFT_CLICK_BLOCK -> {
                    var price = ChestShop.getBuyPrice(sign.getLine(PRICES_LINE));
                    //TODO: Check if player has full inventory
                    if(DynamicChestShop.getEcon().getBalance(player) >= price) {
                        //TODO: Check presence of items and remove items from chest
                        DynamicChestShop.getEcon().withdrawPlayer(player, price);
                        player.getInventory().addItem(new ItemStack(material, quantity));
                    } else {
                        player.sendMessage("AdminShop: You do not have enough money");
                    }
                }

                case RIGHT_CLICK_BLOCK -> {
                    var price = ChestShop.getSellPrice(sign.getLine(PRICES_LINE));
                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack itemStack = new ItemStack(material, quantity);
                    if(playerInventory.containsAtLeast(itemStack, quantity)){
                        DynamicChestShop.getEcon().depositPlayer(player, price);
                        ItemStack[] content = playerInventory.getContents();
                        for(ItemStack is : content){
                            if(is != null && is.getType().equals(material)) {
                                var amountOfItems = is.getAmount();
                                if(amountOfItems >= quantity){
                                    is.setAmount(amountOfItems-quantity);
                                    break;
                                } else {
                                    is.setAmount(0);
                                    quantity = quantity-amountOfItems;
                                }
                            }
                        }
                        //TODO: Change global price on sell accordingly
                    } else {
                        player.sendMessage("AdminShop: You do not have enough items to sell");
                    }
                }

                default -> throw new IllegalStateException("Unexpected value: " + action);
            }
        }
    }
}
