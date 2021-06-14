package io.ilyahaker.chestsocket;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Chestsocket extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(new ChestSocketListener(this), this);
        getCommand("socket").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command is for players only!");
                return false;
            }

            Player player = (Player) sender;
            GameInventory inventory = new GameInventory("Socket", player, this);
            inventory.getOwner();
            inventory.open();
            return true;
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}