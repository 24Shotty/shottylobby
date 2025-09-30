package ShottyLobby;

import org.bukkit.plugin.java.JavaPlugin;

import ShottyLobby.listeners.InventoryListener;
import ShottyLobby.listeners.JoinActionsListener;
import ShottyLobby.listeners.MenuListener;
import ShottyLobby.listeners.PlayerProtectionListener;
import ShottyLobby.listeners.WorldProtectionListener;
import ShottyLobby.utils.AnnouncementManager;
import ShottyLobby.utils.InventoryManager;
import ShottyLobby.utils.MenuManager;
import ShottyLobby.utils.SpawnManager;

public final class Shottylobby extends JavaPlugin {

    private AnnouncementManager announcementManager;
    private InventoryManager inventoryManager;
    private SpawnManager spawnManager;
    private MenuManager menuManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        spawnManager = new SpawnManager(this);
        inventoryManager = new InventoryManager(this);
        menuManager = new MenuManager(this);
        
        getServer().getPluginManager().registerEvents(new JoinActionsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        
        announcementManager = new AnnouncementManager(this);
        
        getLogger().info("Plugin attivato!");
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disattivato!");
    }
}
