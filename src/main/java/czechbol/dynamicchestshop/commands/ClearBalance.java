package czechbol.dynamicchestshop.commands;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearBalance implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player)
            DynamicChestShop.getEcon().withdrawPlayer(player,
                    DynamicChestShop.getEcon().getBalance(player));
        return true;
    }
}
