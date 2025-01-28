package org.GoodPomoc.Alchemik;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GoodPomoc extends JavaPlugin implements CommandExecutor {

    private static final String AUTHOR = "_Alchemik";

    @Override
    public void onEnable() {
        if (!getDescription().getAuthors().equals(AUTHOR)) {
            getLogger().severe("Plugin został wyłączony: zły autor.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.getCommand("pomoc").setExecutor(this);
        getLogger().info("Plugin Pomoc został załadowany.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pomoc")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length < 1) {
                    player.sendMessage("Musisz podać treść zgłoszenia.");
                    return true;
                }

                String message = String.join(" ", args);
                String adminMessage = player.getName() + " zgłosił: " + message;

                // Wysyłanie wiadomości do wszystkich administratorów (graczy z uprawnieniami)
                Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.hasPermission("pomoc.view"))
                        .forEach(admin -> admin.sendMessage(adminMessage));

                player.sendMessage("Twoje zgłoszenie zostało wysłane do administratora.");
                return true;
            } else {
                sender.sendMessage("Ta komenda może być używana tylko przez graczy.");
                return true;
            }
        }
        return false;
    }
}
