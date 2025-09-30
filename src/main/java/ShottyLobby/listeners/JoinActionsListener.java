package ShottyLobby.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ShottyLobby.Shottylobby;

public class JoinActionsListener implements Listener {

    private final Shottylobby plugin;

    public JoinActionsListener(Shottylobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        String defaultGamemode = plugin.getConfig().getString("join-gamemode", "ADVENTURE");
        try {
            GameMode gameMode = GameMode.valueOf(defaultGamemode.toUpperCase());
            player.setGameMode(gameMode);
        } catch (IllegalArgumentException e) {
            player.setGameMode(GameMode.ADVENTURE);
        }
        
        Location spawn = plugin.getSpawnManager().getSpawn();
        if (spawn != null) {
            player.teleport(spawn);
        }
        
        plugin.getInventoryManager().giveItems(player);
        
        List<String> joinActions = plugin.getConfig().getStringList("join-actions");
        
        for (String action : joinActions) {
            if (action.startsWith("[MESSAGE] ")) {
                String message = action.substring(10);
                message = replacePlaceholders(message, player);
                plugin.getInventoryManager().sendClickableMessage(player, message);
            } else if (action.startsWith("[GAMEMODE] ")) {
                String gamemode = action.substring(11).toUpperCase();
                try {
                    GameMode gm = GameMode.valueOf(gamemode);
                    player.setGameMode(gm);
                } catch (IllegalArgumentException e) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }
        }
    }

    private String replacePlaceholders(String message, Player player) {
        message = message.replace("%player_name%", player.getName());
        message = message.replace("%player_displayname%", player.getDisplayName());
        message = message.replace("%player_uuid%", player.getUniqueId().toString());
        message = message.replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        message = message.replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()));
        return message;
    }
}
