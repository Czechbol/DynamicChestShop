package czechbol.dynamicchestshop.staticshop.static;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChestShop implements Listener {
    @EventHandler
    public void OnSignPlace(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();

        if (!isChestShop(block)) {
            return;
        }

        var sign = (Sign) block;



    }

    private boolean isChestShop(Block block) {
        if (!(block instanceof Sign sign)) return false;

        return sign.getLine(0).equalsIgnoreCase("[ChestShop]");
    }
}
