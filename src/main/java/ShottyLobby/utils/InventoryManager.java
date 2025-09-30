package ShottyLobby.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ShottyLobby.Shottylobby;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class InventoryManager {

    private final Shottylobby plugin;
    private final Map<UUID, Boolean> hiddenPlayers;

    public InventoryManager(Shottylobby plugin) {
        this.plugin = plugin;
        this.hiddenPlayers = new HashMap<>();
    }

    public void giveItems(Player player) {
        player.getInventory().clear();

        if (plugin.getConfig().getBoolean("custom-items.enable", true)) {
            ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("custom-items.items");
            
            if (itemsSection != null) {
                for (String key : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                    if (itemSection != null) {
                        ItemStack item = createItem(itemSection, player);
                        int slot = itemSection.getInt("slot", 0);
                        player.getInventory().setItem(slot, item);
                    }
                }
            }
        }
    }

    private ItemStack createItem(ConfigurationSection section, Player player) {
        List<String> actions = section.getStringList("actions");
        boolean hasHidePlayersAction = actions.contains("[HIDEPLAYERS]");
        boolean isHidden = hiddenPlayers.getOrDefault(player.getUniqueId(), false);
        
        String materialName = section.getString("material", "STONE");
        if (hasHidePlayersAction && isHidden && section.contains("material-hidden")) {
            materialName = section.getString("material-hidden", materialName);
        }
        
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            material = Material.STONE;
        }

        int amount = section.getInt("amount", 1);
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String name = section.getString("name", "");
            if (hasHidePlayersAction && isHidden && section.contains("name-hidden")) {
                name = section.getString("name-hidden", name);
            }
            
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

    public void handleItemClick(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();

        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("custom-items.items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null && itemSection.getInt("slot") == slot) {
                    executeActions(player, itemSection.getStringList("actions"));
                    break;
                }
            }
        }
    }

    private void togglePlayerVisibility(Player player) {
        boolean isHidden = hiddenPlayers.getOrDefault(player.getUniqueId(), false);
        hiddenPlayers.put(player.getUniqueId(), !isHidden);

        if (!isHidden) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(player)) {
                    player.hidePlayer(plugin, online);
                }
            }
        } else {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(player)) {
                    player.showPlayer(plugin, online);
                }
            }
        }

        giveItems(player);
    }

    private void executeActions(Player player, List<String> actions) {
        for (String action : actions) {
            if (action.equals("[HIDEPLAYERS]")) {
                togglePlayerVisibility(player);
            } else if (action.equals("[PARKOUR]")) {
                executeParkourAction(player);
            } else if (action.equals("[SOCIAL]")) {
                sendSocialMessages(player);
            } else if (action.startsWith("[OPENMENU] ")) {
                String menuName = action.substring(11);
                plugin.getMenuManager().openMenu(player, menuName);
            } else if (action.startsWith("[MESSAGE] ")) {
                String message = action.substring(10);
                sendClickableMessage(player, message);
            } else if (action.startsWith("[COMMAND] ")) {
                String command = action.substring(10);
                command = command.replace("%player%", player.getName());
                player.performCommand(command);
            } else if (action.startsWith("[CONSOLE] ")) {
                String command = action.substring(10);
                command = command.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void sendSocialMessages(Player player) {
        List<String> messages = plugin.getConfig().getStringList("social-messages");
        for (String message : messages) {
            sendClickableMessage(player, message);
        }
    }

    public void sendClickableMessage(Player player, String message) {
        message = message.replace("%player%", player.getName());
        
        String plainMessage = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
        Pattern urlPattern = Pattern.compile("(https?://[\\w\\-.]+(:[0-9]+)?(/[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]*)?|[\\w\\-]+\\.[a-z]{2,}(/[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]*)?)");
        Matcher matcher = urlPattern.matcher(plainMessage);
        
        if (!matcher.find()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return;
        }
        
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        TextComponent component = new TextComponent();
        int lastEnd = 0;
        matcher.reset();
        
        while (matcher.find()) {
            String url = matcher.group();
            int urlStart = message.indexOf(url, lastEnd);
            
            if (urlStart > lastEnd) {
                String beforeUrl = message.substring(lastEnd, urlStart);
                component.addExtra(new TextComponent(TextComponent.fromLegacyText(beforeUrl)));
            }
            
            String fullUrl = url;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                fullUrl = "https://" + url;
            }
            
            TextComponent linkComponent = new TextComponent(url);
            linkComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, fullUrl));
            linkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClicca per aprire:\n§6" + url)));
            component.addExtra(linkComponent);
            
            lastEnd = urlStart + url.length();
        }
        
        if (lastEnd < message.length()) {
            String remaining = message.substring(lastEnd);
            component.addExtra(new TextComponent(TextComponent.fromLegacyText(remaining)));
        }
        
        player.spigot().sendMessage(component);
    }

    private void executeParkourAction(Player player) {
        String mode = plugin.getConfig().getString("parkour.mode", "TELEPORT");
        
        if (mode.equalsIgnoreCase("TELEPORT")) {
            String worldName = plugin.getConfig().getString("parkour.teleport.world", "world");
            org.bukkit.World world = Bukkit.getWorld(worldName);
            
            if (world != null) {
                double x = plugin.getConfig().getDouble("parkour.teleport.x", 0.0);
                double y = plugin.getConfig().getDouble("parkour.teleport.y", 100.0);
                double z = plugin.getConfig().getDouble("parkour.teleport.z", 0.0);
                float yaw = (float) plugin.getConfig().getDouble("parkour.teleport.yaw", 0.0);
                float pitch = (float) plugin.getConfig().getDouble("parkour.teleport.pitch", 0.0);
                
                org.bukkit.Location location = new org.bukkit.Location(world, x, y, z, yaw, pitch);
                player.teleport(location);
            }
        } else if (mode.equalsIgnoreCase("COMMAND")) {
            String command = plugin.getConfig().getString("parkour.command", "spawn");
            command = command.replace("%player%", player.getName());
            player.performCommand(command);
        }
    }

    public void removeHiddenPlayer(UUID uuid) {
        hiddenPlayers.remove(uuid);
    }
}
