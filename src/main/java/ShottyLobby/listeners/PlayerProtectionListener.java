package ShottyLobby.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import ShottyLobby.Shottylobby;

public class PlayerProtectionListener implements Listener {

    private final Shottylobby plugin;

    public PlayerProtectionListener(Shottylobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        double voidHeight = plugin.getConfig().getDouble("void-teleport-height", 0);

        if (player.getLocation().getY() <= voidHeight) {
            Location spawn = plugin.getSpawnManager().getSpawn();
            if (spawn != null) {
                player.teleport(spawn);
            }
        }
    }
}
