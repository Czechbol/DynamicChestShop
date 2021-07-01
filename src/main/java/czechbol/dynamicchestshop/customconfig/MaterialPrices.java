package czechbol.dynamicchestshop.customconfig;

import czechbol.dynamicchestshop.dynamicshop.CalculateInitialPrices;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class MaterialPrices extends CustomConfigFile {

    public MaterialPrices(String fileName, Plugin plugin) {
        super(fileName, plugin);
    }

    public void initDefaults() {
        FileConfiguration config = getConfig();
        String pluginDataFolderPath = getPlugin().getDataFolder().getPath();
        String inputFileName = getPlugin().getConfig().getString("FileSettings.InputFile");

        if (getPlugin().getConfig().getBoolean(pluginDataFolderPath + "/" + inputFileName)) { //CalculatePrices option
            try {
                CalculateInitialPrices.calc(pluginDataFolderPath + "/" + inputFileName, config);
            } catch (IOException e) {
                getPlugin().getLogger().warning("File " + inputFileName + " was not found.");
            }
        } else { //TODO: Should this as "default" values in code?
            config.set(Material.MELON_SEEDS + ".Price", 30);
            config.set(Material.GRASS_BLOCK + ".Price", 50);
            config.set(Material.CHEST + ".Price", 96);
            config.set(Material.GOLDEN_HORSE_ARMOR + ".Price", 714);
            config.set(Material.ENCHANTED_GOLDEN_APPLE + ".Price", 5000);
            config.set(Material.RED_SAND + ".Price", 10);
            config.set(Material.FROSTED_ICE + ".Price", 50);
            config.set(Material.ACACIA_LOG + ".Price", 50);
            config.set(Material.SADDLE + ".Price", 1000);
            config.set(Material.PINK_TULIP + ".Price", 20);
            config.set(Material.JUNGLE_SAPLING + ".Price", 10);
            config.set(Material.POTATO + ".Price", 20);
            config.set(Material.WHITE_TULIP + ".Price", 20);
            config.set(Material.CACTUS + ".Price", 12);
            config.set(Material.BLUE_ORCHID + ".Price", 20);
            config.set(Material.BAMBOO_SAPLING + ".Price", 20);
            config.set(Material.GHAST_TEAR + ".Price", 500);
            config.set(Material.PUMPKIN + ".Price", 20);
            config.set(Material.FLOWERING_AZALEA_LEAVES + ".Price", 15);
            config.set(Material.WHEAT + ".Price", 20);
            config.set(Material.GUNPOWDER + ".Price", 50);
            config.set(Material.SPRUCE_LOG + ".Price", 50);
            config.set(Material.SCULK_SENSOR + ".Price", 2000);
            config.set(Material.SALMON + ".Price", 30);
            config.set(Material.BEEF + ".Price", 30);
            config.set(Material.SPRUCE_SAPLING + ".Price", 10);
            config.set(Material.MUTTON + ".Price", 30);
            config.set(Material.DIAMOND_HORSE_ARMOR + ".Price", 14000);
            config.set(Material.DIAMOND + ".Price", 2000);
            config.set(Material.COBBLESTONE + ".Price", 10);
            config.set(Material.SPIDER_EYE + ".Price", 20);
            config.set(Material.GLOWSTONE_DUST + ".Price", 100);
            config.set(Material.BASALT + ".Price", 40);
            config.set(Material.RAW_GOLD + ".Price", 100);
            config.set(Material.TOTEM_OF_UNDYING + ".Price", 50000);
            config.set(Material.DARK_OAK_SAPLING + ".Price", 10);
            config.set(Material.NETHERITE_SCRAP + ".Price", 6000);
            config.set(Material.COD + ".Price", 30);
            config.set(Material.SOUL_SAND + ".Price", 40);
            config.set(Material.EMERALD + ".Price", 500);
            config.set(Material.SUNFLOWER + ".Price", 20);
            config.set(Material.CORNFLOWER + ".Price", 20);
            config.set(Material.SHULKER_SHELL + ".Price", 4000);
            config.set(Material.POTATOES + ".Price", 30);
            config.set(Material.ICE + ".Price", 10);
            config.set(Material.CLAY_BALL + ".Price", 15);
            config.set(Material.COCOA_BEANS + ".Price", 25);
            config.set(Material.POPPY + ".Price", 20);
            config.set(Material.STRING + ".Price", 20);
            config.set(Material.SOUL_SOIL + ".Price", 40);
            config.set(Material.DARK_OAK_LOG + ".Price", 50);
            config.set(Material.LILAC + ".Price", 20);
            config.set(Material.RAW_COPPER + ".Price", 30);
            config.set(Material.RABBIT_FOOT + ".Price", 50);
            config.set(Material.APPLE + ".Price", 25);
            config.set(Material.REDSTONE + ".Price", 100);
            config.set(Material.COAL + ".Price", 56);
            config.set(Material.ENDER_PEARL + ".Price", 500);
            config.set(Material.CARROT + ".Price", 20);
            config.set(Material.ACACIA_LEAVES + ".Price", 15);
            config.set(Material.GRASS + ".Price", 50);
            config.set(Material.SLIME_BALL + ".Price", 50);
            config.set(Material.QUARTZ + ".Price", 50);
            config.set(Material.SAND + ".Price", 10);
            config.set(Material.LAPIS_LAZULI + ".Price", 50);
            config.set(Material.CRAFTING_TABLE + ".Price", 48);
            config.set(Material.BAMBOO + ".Price", 12);
            config.set(Material.SNOW + ".Price", 10);
            config.set(Material.SUGAR_CANE + ".Price", 15);
            config.set(Material.DIRT + ".Price", 10);
            config.set(Material.JUNGLE_LEAVES + ".Price", 15);
            config.set(Material.PUMPKIN_SEEDS + ".Price", 30);
            config.set(Material.RED_TULIP + ".Price", 20);
            config.set(Material.JUNGLE_LOG + ".Price", 50);
            config.set(Material.INK_SAC + ".Price", 15);
            config.set(Material.NETHER_WART + ".Price", 150);
            config.set(Material.RABBIT_HIDE + ".Price", 50);
            config.set(Material.BEETROOT + ".Price", 30);
            config.set(Material.SWEET_BERRIES + ".Price", 15);
            config.set(Material.LILY_OF_THE_VALLEY + ".Price", 20);
            config.set(Material.DANDELION + ".Price", 20);
            config.set(Material.PRISMARINE + ".Price", 50);
            config.set(Material.FLINT + ".Price", 15);
            config.set(Material.AZALEA_LEAVES + ".Price", 15);
            config.set(Material.RABBIT + ".Price", 30);
            config.set(Material.COCOA + ".Price", 15);
            config.set(Material.ALLIUM + ".Price", 20);
            config.set(Material.OAK_SAPLING + ".Price", 10);
            config.set(Material.PEONY + ".Price", 20);
            config.set(Material.ROTTEN_FLESH + ".Price", 5);
            config.set(Material.OBSIDIAN + ".Price", 100);
            config.set(Material.AZURE_BLUET + ".Price", 20);
            config.set(Material.OXEYE_DAISY + ".Price", 20);
            config.set(Material.END_STONE + ".Price", 30);
            config.set(Material.DARK_OAK_LEAVES + ".Price", 15);
            config.set(Material.GRAVEL + ".Price", 10);
            config.set(Material.MELON + ".Price", 45);
            config.set(Material.IRON_HORSE_ARMOR + ".Price", 364);
            config.set(Material.WHEAT_SEEDS + ".Price", 10);
            config.set(Material.COBWEB + ".Price", 20);
            config.set(Material.CHICKEN + ".Price", 30);
            config.set(Material.ACACIA_SAPLING + ".Price", 10);
            config.set(Material.POWDER_SNOW + ".Price", 20);
            config.set(Material.BIRCH_SAPLING + ".Price", 10);
            config.set(Material.OAK_LOG + ".Price", 50);
            config.set(Material.LEATHER + ".Price", 15);
            config.set(Material.BONE + ".Price", 20);
            config.set(Material.POISONOUS_POTATO + ".Price", 5);
            config.set(Material.BEETROOTS + ".Price", 30);
            config.set(Material.SNOWBALL + ".Price", 10);
            config.set(Material.BIRCH_LOG + ".Price", 50);
            config.set(Material.EGG + ".Price", 10);
            config.set(Material.PORKCHOP + ".Price", 30);
            config.set(Material.CARROTS + ".Price", 30);
            config.set(Material.FEATHER + ".Price", 20);
            config.set(Material.OAK_LEAVES + ".Price", 15);
            config.set(Material.SPRUCE_LEAVES + ".Price", 15);
            config.set(Material.BEETROOT_SEEDS + ".Price", 15);
            config.set(Material.NETHERRACK + ".Price", 20);
            config.set(Material.LEATHER_HORSE_ARMOR + ".Price", 105);
            config.set(Material.SPONGE + ".Price", 200);
            config.set(Material.BLAZE_ROD + ".Price", 300);
            config.set(Material.RAW_IRON + ".Price", 65);
            config.set(Material.BIRCH_LEAVES + ".Price", 15);
            config.set(Material.ORANGE_TULIP + ".Price", 20);
            config.set(Material.ROSE_BUSH + ".Price", 20);
        }

        saveConfig();
    }
}
