package czechbol.dynamicchestshop.staticshop;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminShopDestroyCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(player.hasPermission("dynamicshop.admindestroy")){
                if(player.hasPermission("dynamicshop.admindestroy.toggle")){
                    DynamicChestShop.getPerms().playerRemove(player,
                            "dynamicshop.admindestroy.toggle");
                    player.sendMessage("AdminShop: Admin destroy is toggled off");
                } else {
                    DynamicChestShop.getPerms().playerAdd(player,
                            "dynamicshop.admindestroy.toggle");
                    player.sendMessage("AdminShop: Admin destroy is toggled on");
                }
            } else {
                player.sendMessage("AdminShop: You do not have permission to destroy admin shops");
            }
        }
        return false;
    }
}
