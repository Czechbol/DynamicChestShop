package czechbol.dynamicchestshop.commands;

import czechbol.dynamicchestshop.DynamicChestShop;
import czechbol.dynamicchestshop.customconfig.CustomConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ReloadCustomConfigs implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        HashMap<String, CustomConfigFile> configs = DynamicChestShop.getCustomConfigs();
        String prefix = DynamicChestShop.getPluginPrefix();

        for (String confName : configs.keySet()) {
            if (configs.get(confName).reloadConfig()) {
                DynamicChestShop.getPluginLogger().info(
                        ChatColor.stripColor(prefix) + "Config file \"" + confName + "\" was reloaded.");
                if (sender instanceof Player)
                    sender.sendMessage(prefix + "Config file \"" + ChatColor.GREEN
                            + confName + ChatColor.WHITE + "\" was reloaded.");
            } else {
                DynamicChestShop.getPluginLogger().severe("Config file \"" + confName + "\" was not reloaded!");
                if (sender instanceof Player)
                    sender.sendMessage(prefix + "Config file \"" + ChatColor.RED
                            + confName + ChatColor.WHITE + "\" was not reloaded!");
            }
        }

        return true;
    }
}
