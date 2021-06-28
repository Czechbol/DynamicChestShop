package czechbol.dynamicchestshop.commands;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminShopDestroy implements CommandExecutor {

    private final String prefix = DynamicChestShop.getPluginPrefix();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender; //TODO: This system doesn't work with /op-ed players

            if (player.hasPermission("dynamicshop.adminshop")) {
                if (player.hasPermission("dynamicshop.adminshop.toggle")) {
                    DynamicChestShop.getPerms().playerRemove(player,
                            "dynamicshop.adminshop.toggle");
                    player.sendMessage(prefix + "Admin destroy is mode turned off");
                } else {
                    DynamicChestShop.getPerms().playerAdd(player,
                            "dynamicshop.adminshop.toggle");
                    player.sendMessage(prefix + "Admin destroy mode is turned on");
                }
            } else {
                player.sendMessage(prefix + "You do not have permission to destroy admin shops");
            }
        }

        return false;
    }
}
