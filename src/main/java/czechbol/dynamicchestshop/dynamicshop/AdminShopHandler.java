package czechbol.dynamicchestshop.dynamicshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static czechbol.dynamicchestshop.dynamicshop.AdminShop.*;

public class AdminShopHandler implements Listener {

    private FileConfiguration config = DynamicChestShop.getConf();
    private final int shopMode = config.getInt("General.Mode");
    private final double capacityThresholdValue = config.getDouble("General.CapacityThreshold");
    private final double differenceValue = config.getDouble("General.Difference");
    private final boolean fillAdminShopFlag = config.getBoolean("General.FillAdminShop");
    private final String prefix = DynamicChestShop.getPluginPrefix();
    private double materialSteepness;
    private FileConfiguration materials = DynamicChestShop.getCustomConfigs().get("material_prices.yml").getConfig();

    @EventHandler
    public void onSignEdit(SignChangeEvent e) {
        var block = e.getBlock();
        var player = e.getPlayer();
        ItemStack materialIS = null;
        var quantity = 0;
        var buyPrice = 0;
        var sellPrice = 0;

        if (!e.getLine(NAME_LINE).equalsIgnoreCase("as")) return;

        if (!player.hasPermission("dynamicshop.adminshop")) {
            e.getPlayer().sendMessage(prefix + "You don't have permission to create AdminShop");
            e.setCancelled(true);
            return;
        }

        if (block.getRelative(BlockFace.DOWN, 1).getState() instanceof Chest chest) {
            ItemStack[] contents = chest.getBlockInventory().getContents();

            for (ItemStack is : contents) {
                if (is == null) continue;

                if (materialIS == null || materialIS.getType().equals(is.getType())) {
                    materialIS = is;
                    quantity += is.getAmount();
                } else {
                    player.sendMessage(prefix + "You can't sell 2 or more different items.");
                    e.setCancelled(true);
                    return;
                }
            }

            if (materialIS == null) {
                player.sendMessage(prefix + "You didn't put anything in the chest.");
                e.setCancelled(true);
                return;
            }

        } else {
            player.sendMessage(prefix + "You need to place the chest first.");
            e.setCancelled(true);
            return;
        }

        materialSteepness = materials.getInt(materialIS.getType() + ".Price")
                * chest.getBlockInventory().getSize() * materialIS.getMaxStackSize() * capacityThresholdValue;
        System.out.println(materialSteepness);
        materials.set(materialIS.getType() + ".Steepness", materialSteepness);

        if (fillAdminShopFlag) {
            chest.getBlockInventory().clear();

            while (getUsedSpace(chest.getBlockInventory()) < chest.getBlockInventory().getSize()
                    * materialIS.getMaxStackSize() * capacityThresholdValue)
                chest.getBlockInventory().addItem(materialIS);

            buyPrice = (int) (materialSteepness / chest.getBlockInventory().getSize()
                    * materialIS.getMaxStackSize() * capacityThresholdValue);
            sellPrice = (int) (materialSteepness / chest.getBlockInventory().getSize()
                    * materialIS.getMaxStackSize() * capacityThresholdValue * differenceValue);
        } else {
            buyPrice = (int) (materialSteepness / quantity);
            sellPrice = (int) (materialSteepness / quantity * differenceValue);
        }

        System.out.println(buyPrice + " " + sellPrice);

        e.setLine(NAME_LINE, "[AdminShop]");
        e.setLine(QUANTITY_LINE, String.valueOf(quantity));
        e.setLine(PRICES_LINE, String.format("B %d:%d S", buyPrice, sellPrice)); //TODO: Get unique starting prices of item from file or DB
        e.setLine(MATERIAL_LINE, materialIS.getType().toString());

        player.sendMessage(prefix + "Shop was created.");
    }

    @EventHandler
    public void OnSignInteract(PlayerInteractEvent e) {
        var action = e.getAction();
        var player = e.getPlayer();

        if (!action.equals(Action.LEFT_CLICK_BLOCK)
                && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;

        var block = e.getClickedBlock();

        if (block.getState() instanceof Sign sign) {
            if (!sign.getLine(NAME_LINE).equals("[AdminShop]")) return;

            int quantity = Integer.parseInt(sign.getLine(QUANTITY_LINE));
            var material = Material.getMaterial(sign.getLine(MATERIAL_LINE));

            if (material == null) {
                player.sendMessage(prefix + "Invalid item");
                return;
            }

            Inventory chestInv = null;
            if (sign.getBlock().getRelative(BlockFace.DOWN, 1).getState() instanceof Chest chest) {
                chestInv = chest.getBlockInventory();
                chest.setCustomName("Test");
            }

            materialSteepness = materials.getInt(material + ".Steepness");

            if (materialSteepness == 0) return;

            if (e.getPlayer().isSneaking()) {
                quantity = material.getMaxStackSize();
            }

            int buyPrice = AdminShop.getBuyPrice(sign.getLine(PRICES_LINE));
            int sellPrice = AdminShop.getSellPrice(sign.getLine(PRICES_LINE));

            switch (action) {
                case LEFT_CLICK_BLOCK -> {
                    if (buyPrice == NOT_FOR_BUY) {
                        player.sendMessage(prefix + "You can not buy in this shop.");
                        return;
                    }

                    if (player.hasPermission("dynamicshop.adminshop.toggle")) return;

                    if (DynamicChestShop.getEcon().getBalance(player) >= buyPrice) {
                        if (player.getInventory().firstEmpty() == -1) { // Free inventory space check
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
                                player.sendMessage(prefix + "Your inventory is full.");
                                return;
                            }
                        }

                        if (chestInv.containsAtLeast(new ItemStack(material, quantity), quantity)) {
                            var usedSpace = getUsedSpace(chestInv);

                            if (usedSpace == quantity) {
                                player.sendMessage(prefix + "Shop is empty...");
                                return;
                            }

                            int newBuyPrice = (int) (materialSteepness / (usedSpace - quantity) * quantity);

                            DynamicChestShop.getEcon().withdrawPlayer(player, buyPrice);

                            for (ItemStack is : chestInv.getContents()) { // removes items from shop chest
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

                            player.getInventory().addItem(new ItemStack(material, quantity));

                            if (sellPrice == NOT_FOR_SALE)
                                sign.setLine(PRICES_LINE, String.format("B %d", newBuyPrice));
                            else
                                sign.setLine(PRICES_LINE, String.format("B %d:%d S", newBuyPrice, buyPrice));

                            sign.update();
                            //TODO: Change global price on buy in DB
                        } else {
                            player.sendMessage(prefix + "Shop is empty.");
                            e.setCancelled(true);
                        }
                    } else {
                        player.sendMessage(prefix + "You do not have enough money.");
                    }
                }

                case RIGHT_CLICK_BLOCK -> {
                    if (sellPrice == NOT_FOR_SALE) {
                        player.sendMessage(prefix + "You can not sell in this shop.");
                        return;
                    }

                    if (player.hasPermission("dynamicshop.adminshop.toggle")) {
                        player.sendMessage(prefix + "You have adminshop destroy mode toggled on.");
                        return;
                    }

                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack itemStack = new ItemStack(material, quantity);

                    if (playerInventory.containsAtLeast(itemStack, quantity)) {
                        DynamicChestShop.getEcon().depositPlayer(player, sellPrice);
                        ItemStack[] content = playerInventory.getContents();
                        var usedSpace = getUsedSpace(chestInv);
                        var freeSpace = 0;

                        for (ItemStack is : content) { // removes items from player inventory
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

                        content = chestInv.getContents();

                        for (ItemStack is : content) {
                            if (is == null) continue;

                            if (is.getMaxStackSize() - is.getAmount() > 0) {
                                freeSpace += is.getMaxStackSize() - is.getAmount();
                            }
                        }

                        chestInv.addItem(new ItemStack(material, quantity));

                        int newSellPrice = (int) (materialSteepness / (usedSpace + quantity) * quantity);

                        if (buyPrice == NOT_FOR_SALE)
                            sign.setLine(PRICES_LINE, String.format("S %d", newSellPrice));
                        else
                            sign.setLine(PRICES_LINE, String.format("B %d:%d S", sellPrice, newSellPrice));

                        sign.update();
                        //TODO: Change global price on sell in DB
                    } else {
                        player.sendMessage(prefix + "You do not have enough items to sell");
                    }
                }
            }
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        //TODO: Check if broken block has a shop sign attached to it
        Player player = event.getPlayer();
        BlockState blockState = event.getBlock().getState();

        if (blockState instanceof Sign sign
                && sign.getBlock().getRelative(BlockFace.DOWN, 1).getState() instanceof Chest chest
                && checkIfAdminShop(sign, player, event)) {

            chest.getBlockInventory().clear();
            chest.getBlock().setType(Material.AIR);

        } else if (blockState instanceof Chest chest
                && chest.getBlock().getRelative(BlockFace.UP, 1).getState() instanceof Sign sign
                && checkIfAdminShop(sign, player, event)) {

            event.setCancelled(true);
            chest.getBlockInventory().clear();
            sign.getBlock().setType(Material.AIR);
            chest.getBlock().setType(Material.AIR);
        }
    }

    private int getUsedSpace(Inventory inventory) {
        var totalItems = 0;
        ItemStack[] content = inventory.getContents();

        for (ItemStack is : content) {
            if (is == null) continue;

            totalItems += is.getAmount();
        }

        return totalItems;
    }

    private boolean checkIfAdminShop(Sign sign, Player player, BlockBreakEvent event) {
        if (sign.getLine(NAME_LINE).equals("[AdminShop]")) {
            if (!player.hasPermission("dynamicshop.adminshop.toggle")) {

                event.setCancelled(true);

                if (!player.hasPermission("dynamicshop.adminshop")) {
                    player.sendMessage(prefix + "You don't have permission to destroy shops.");
                }
            } else {
                return true;
            }
        }
        return false;
    }
}
