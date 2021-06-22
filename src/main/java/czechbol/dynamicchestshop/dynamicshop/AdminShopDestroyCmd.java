package czechbol.dynamicchestshop.dynamicshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminShopDestroyCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender; //TODO: This system doesn't work with /op-ed players

            if (player.hasPermission("dynamicshop.adminshop")) {
                if (player.hasPermission("dynamicshop.adminshop.toggle")) {
                    DynamicChestShop.getPerms().playerRemove(player,
                            "dynamicshop.adminshop.toggle");
                    player.sendMessage("AdminShop: Admin destroy is toggled off");
                } else {
                    DynamicChestShop.getPerms().playerAdd(player,
                            "dynamicshop.adminshop.toggle");
                    player.sendMessage("AdminShop: Admin destroy is toggled on");
                }
            } else {
                player.sendMessage("AdminShop: You do not have permission to destroy admin shops");
            }
        }

        return false;
    }
}
