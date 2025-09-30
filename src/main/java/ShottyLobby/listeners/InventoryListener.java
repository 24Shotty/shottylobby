package ShottyLobby.listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ShottyLobby.Shottylobby;

public class InventoryListener implements Listener {

    private final Shottylobby plugin;

    public InventoryListener(Shottylobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        List<String> allowedGamemodes = plugin.getConfig().getStringList("item-click-gamemodes");
        boolean canClick = false;
        
        for (String mode : allowedGamemodes) {
            try {
                GameMode gameMode = GameMode.valueOf(mode.toUpperCase());
                if (player.getGameMode() == gameMode) {
                    canClick = true;
                    break;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        
        if (canClick) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getItem() != null) {
                    plugin.getInventoryManager().handleItemClick(player, event.getItem());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE && !player.hasPermission("world.interaction")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        
        if (!player.hasPermission("world.interaction")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getInventoryManager().removeHiddenPlayer(event.getPlayer().getUniqueId());
    }
}
