package czechbol.dynamicchestshop.commands;

import czechbol.dynamicchestshop.DynamicChestShop;
import czechbol.dynamicchestshop.customconfig.CustomConfigFile;
import czechbol.dynamicchestshop.dynamicshop.CalculateInitialPrices;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.logging.Level;

public class GenerateItemPrices implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = DynamicChestShop.getPluginPrefix();
        FileConfiguration config = DynamicChestShop.getConf();
        String inputFileName = config.getString("FileSettings.PricesFile");
        CustomConfigFile prices = DynamicChestShop.getCustomConfigs().get(inputFileName);
        String pluginDataFolderPath = DynamicChestShop.getPlugin().getDataFolder().getPath();

        if (sender.hasPermission("dynamicshop.genprices")) {
            try {
                CalculateInitialPrices.calc(pluginDataFolderPath + "/" + inputFileName, prices.getConfig());
                prices.saveConfig();
            } catch (IOException e) {
                DynamicChestShop.getPluginLogger().log(Level.WARNING, "File " + inputFileName + " was not found.");
            }
        } else {
            sender.sendMessage(prefix + "You can not use this command.");
        }
        return true;
    }
}
