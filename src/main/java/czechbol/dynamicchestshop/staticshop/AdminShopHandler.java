package czechbol.dynamicchestshop.staticshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static czechbol.dynamicchestshop.staticshop.ChestShop.*;

public class AdminShopHandler implements Listener {
    @EventHandler
    public void onSignEdit(SignChangeEvent e) {
        var block = e.getBlock();
        var player = e.getPlayer();

        if (!e.getLine(NAME_LINE).equalsIgnoreCase("[AdminShop]")) return;

        if (!player.hasPermission("dynamicshop.adminshop")) {
            e.getPlayer().sendMessage("You don't have permission to create AdminShop");
            e.setCancelled(true);
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
            player.sendMessage("AdminShop: Shop could not be created");
            block.setType(Material.AIR);
            return;
        }

        var material = Material.matchMaterial(e.getLine(MATERIAL_LINE));

        if (material != null) {
            e.setLine(MATERIAL_LINE, material.toString());
        } else {
            player.sendMessage("AdminShop: Invalid item");
        }

        player.sendMessage("AdminShop: Shop was created");
    }

    @EventHandler
    public void OnSignInteract(PlayerInteractEvent e) {
        var action = e.getAction();

        if (!action.equals(Action.LEFT_CLICK_BLOCK)
                && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;

        var block = e.getClickedBlock();

        if (block.getState() instanceof Sign sign) {
            if (!sign.getLine(NAME_LINE).equals("[AdminShop]")) return;

            var quantity = Integer.parseInt(sign.getLine(QUANTITY_LINE));
            var material = Material.getMaterial(sign.getLine(MATERIAL_LINE));
            if (material == null) {
                e.getPlayer().sendMessage("AdminShop: Invalid item");
                return;
            }
            var player = e.getPlayer();

            switch (action) {
                case LEFT_CLICK_BLOCK -> {
                    var price = ChestShop.getBuyPrice(sign.getLine(PRICES_LINE));
                    System.out.println(price);
                    if (price == -1) return;

                    if (DynamicChestShop.getEcon().getBalance(player) >= price) {
                        if (player.getInventory().firstEmpty() == -1) {
                            ItemStack[] content = player.getInventory().getContents();
                            var freeSpace = 0;
                            for (ItemStack is : content) {
                                if (is == null) continue;
                                if (is.getType().equals(material)
                                        && is.getMaxStackSize() - is.getAmount() >= 0) {
                                    freeSpace += is.getMaxStackSize() - is.getAmount();
                                }
                            }
                            if (freeSpace < quantity) {
                                player.sendMessage("AdminShop: Your inventory is full");
                                return;
                            }
                        }
                        DynamicChestShop.getEcon().withdrawPlayer(player, price);
                        player.getInventory().addItem(new ItemStack(material, quantity));
                        //TODO: Change global price on buy accordingly
                    } else {
                        player.sendMessage("AdminShop: You do not have enough money");
                    }
                }

                case RIGHT_CLICK_BLOCK -> {
                    var price = ChestShop.getSellPrice(sign.getLine(PRICES_LINE));
                    System.out.println(price);
                    if (price == -1) return;

                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack itemStack = new ItemStack(material, quantity);
                    if (playerInventory.containsAtLeast(itemStack, quantity)) {
                        DynamicChestShop.getEcon().depositPlayer(player, price);
                        ItemStack[] content = playerInventory.getContents();
                        for (ItemStack is : content) {
                            if (is != null && is.getType().equals(material)) {
                                var amountOfItems = is.getAmount();
                                if (amountOfItems >= quantity) {
                                    is.setAmount(amountOfItems - quantity);
                                    break;
                                } else {
                                    is.setAmount(0);
                                    quantity = quantity - amountOfItems;
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

    @EventHandler
    public void onSignBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign sign) {
            if (sign.getLine(0).equals("[AdminShop]")
                    && !event.getPlayer().hasPermission("dynamicshop.adminshop.toggle")) {
                event.setCancelled(true);
                if (!event.getPlayer().hasPermission("dynamicshop.adminshop"))
                    event.getPlayer().sendMessage("AdminShop: You can not destroy admin shops.");
                else
                    event.getPlayer().sendMessage("AdminShop: Use /toggleadmindestroy or\n" +
                            " /tad to toggle admin shop destroy mode.");
            }
        }
    }
}
