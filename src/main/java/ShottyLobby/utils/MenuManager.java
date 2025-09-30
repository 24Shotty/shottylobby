package ShottyLobby.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ShottyLobby.Shottylobby;

public class MenuManager {

    private final Shottylobby plugin;
    private final Map<UUID, String> openMenus;

    public MenuManager(Shottylobby plugin) {
        this.plugin = plugin;
        this.openMenus = new HashMap<>();
    }

    public void openMenu(Player player, String menuName) {
        ConfigurationSection menuSection = plugin.getConfig().getConfigurationSection("menus." + menuName);
        
        if (menuSection == null) {
            player.sendMessage(ChatColor.RED + "Menu non trovato!");
            return;
        }

        String title = ChatColor.translateAlternateColorCodes('&', menuSection.getString("title", "Menu"));
        int size = menuSection.getInt("size", 27);
        
        if (size % 9 != 0 || size > 54) {
            size = 27;
        }

        Inventory inventory = Bukkit.createInventory(null, size, title);
        
        ConfigurationSection itemsSection = menuSection.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    ItemStack item = createMenuItem(itemSection);
                    int slot = itemSection.getInt("slot", 0);
                    if (slot >= 0 && slot < size) {
                        inventory.setItem(slot, item);
                    }
                }
            }
        }

        player.openInventory(inventory);
        openMenus.put(player.getUniqueId(), menuName);
    }

    private ItemStack createMenuItem(ConfigurationSection section) {
        String materialName = section.getString("material", "STONE");
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            material = Material.STONE;
        }

        int amount = section.getInt("amount", 1);
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String name = section.getString("name", "");
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

            List<String> lore = section.getStringList("lore");
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);

            item.setItemMeta(meta);
        }

        return item;
    }

    public void handleMenuClick(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        String menuName = openMenus.get(uuid);
        
        if (menuName == null) {
            return;
        }

        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("menus." + menuName + ".items");
        if (itemsSection == null) {
            return;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection != null && itemSection.getInt("slot") == slot) {
                String command = itemSection.getString("command", "");
                if (!command.isEmpty()) {
                    player.closeInventory();
                    command = command.replace("%player%", player.getName());
                    player.performCommand(command);
                }
                break;
            }
        }
    }

    public boolean isInMenu(UUID uuid) {
        return openMenus.containsKey(uuid);
    }

    public void removePlayer(UUID uuid) {
        openMenus.remove(uuid);
    }
}
