package czechbol.dynamicchestshop.commands;

import czechbol.dynamicchestshop.DynamicChestShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddToBalance implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player)
            DynamicChestShop.getEcon().depositPlayer((Player) sender, Double.parseDouble(args[0]));
        return true;
    }
}
