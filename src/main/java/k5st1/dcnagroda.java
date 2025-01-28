package k5st1;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class DiscordLinkPlugin extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private HashMap<String, String> playerDiscordLinks = new HashMap<>(); // <MinecraftPlayer, DiscordUserId>

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Tworzy plik config.yml, jeśli nie istnieje
        config = getConfig();

        // Rejestracja eventów
        Bukkit.getPluginManager().registerEvents(this, this);

        // Logowanie do bota Discorda
        try {
            JDABuilder.createDefault(config.getString("discord.token"))
                    .build();
        } catch (LoginException e) {
            getLogger().severe("Błąd logowania do Discorda: " + e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dc") && sender instanceof Player) {
            Player player = (Player) sender;
            openDiscordGUI(player);
            return true;
        }
        return false;
    }

    // Funkcja otwierająca GUI, które informuje, czy konto jest połączone
    private void openDiscordGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "Połączenie z Discord");

        // Tworzymy element GUI z informacją, czy konto jest połączone
        ItemStack connectedItem = new ItemStack(Material.GREEN_WOOL);
        if (playerDiscordLinks.containsKey(player.getName())) {
            connectedItem.getItemMeta().setDisplayName("Konto połączone z Discordem!");
        } else {
            connectedItem.getItemMeta().setDisplayName("Nie połączono z Discordem.");
        }

        connectedItem.setItemMeta(connectedItem.getItemMeta());  // Zapisujemy zmiany
        inv.setItem(4, connectedItem);
        player.openInventory(inv);
    }

    // Event, który obsługuje kliknięcie w GUI
    @EventHandler
    public void onInventoryClick(PlayerInteractEvent event) {
        // Obsługuje kliknięcia w GUI (jeśli zajdzie taka potrzeba)
    }

    // Funkcja do połączenia konta Minecraft z Discordem
    public void linkAccountToDiscord(Player player, String discordUserId) {
        try {
            JDABuilder jdaBuilder = JDABuilder.createDefault(config.getString("discord.token"));

            jdaBuilder.addEventListener(new net.dv8tion.jda.api.hooks.ListenerAdapter() {
                @Override
                public void onMessageReceived(net.dv8tion.jda.api.events.message.MessageReceivedEvent event) {
                    if (event.getMessage().getContentRaw().startsWith(config.getString("discord.command_prefix"))) {
                        if (event.getMessage().getAuthor().getId().equals(discordUserId)) {
                            // Zmieniamy nick na Discordzie na taki, jak w Minecraft
                            event.getJDA().getUserById(discordUserId).modifyNickname(player.getName()).queue();
                        }
                    }
                }
            });

            jdaBuilder.build();
        } catch (LoginException e) {
            getLogger().severe("Błąd logowania do Discorda: " + e.getMessage());
        }
    }

    // Funkcja do zapisywania gracza w HashMap
    public void savePlayerToConfig(String playerName, String discordId) {
        playerDiscordLinks.put(playerName, discordId);
    }
}
