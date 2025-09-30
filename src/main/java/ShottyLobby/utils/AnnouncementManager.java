package ShottyLobby.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import ShottyLobby.Shottylobby;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class AnnouncementManager {

    private final Shottylobby plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private List<String> messages;
    private int currentIndex = 0;
    private int interval;

    public AnnouncementManager(Shottylobby plugin) {
        this.plugin = plugin;
        loadMessagesConfig();
        startAnnouncements();
    }

    private void loadMessagesConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            try (InputStream in = plugin.getResource("messages.yml")) {
                if (in != null) {
                    Files.copy(in, messagesFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messages = messagesConfig.getStringList("messages");
        interval = messagesConfig.getInt("interval", 300);
    }

    private void startAnnouncements() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (messages.isEmpty()) {
                    return;
                }
                
                String message = messages.get(currentIndex);
                
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendClickableMessage(player, message);
                }
                
                currentIndex++;
                if (currentIndex >= messages.size()) {
                    currentIndex = 0;
                }
            }
        }.runTaskTimer(plugin, interval * 20L, interval * 20L);
    }

    private void sendClickableMessage(Player player, String message) {
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

    public void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messages = messagesConfig.getStringList("messages");
        interval = messagesConfig.getInt("interval", 300);
        currentIndex = 0;
    }
}
