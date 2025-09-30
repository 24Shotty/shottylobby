package ShottyLobby.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import ShottyLobby.Shottylobby;

public class SpawnManager {

    private final Shottylobby plugin;

    public SpawnManager(Shottylobby plugin) {
        this.plugin = plugin;
    }

    public Location getSpawn() {
        if (!plugin.getConfig().getBoolean("spawnpoint.enable", true)) {
            return null;
        }

        String worldName = plugin.getConfig().getString("spawnpoint.world", "world");
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            plugin.getLogger().warning("Il mondo '" + worldName + "' non esiste!");
            return null;
        }

        double x = plugin.getConfig().getDouble("spawnpoint.x", 0.0);
        double y = plugin.getConfig().getDouble("spawnpoint.y", 64.0);
        double z = plugin.getConfig().getDouble("spawnpoint.z", 0.0);
        float yaw = (float) plugin.getConfig().getDouble("spawnpoint.yaw", 0.0);
        float pitch = (float) plugin.getConfig().getDouble("spawnpoint.pitch", 0.0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean hasSpawn() {
        return plugin.getConfig().getBoolean("spawnpoint.enable", true) && 
               plugin.getConfig().contains("spawnpoint.world");
    }
}