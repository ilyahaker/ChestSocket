package io.ilyahaker.chestsocket;

//import net.minecraft.server.v1_16_R3.ChatMessageType;
//import net.minecraft.server.v1_16_R3.IChatBaseComponent;
//import net.minecraft.server.v1_16_R3.Packet;
//import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
//import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ChestSocketListener implements Listener {

    private Chestsocket plugin;

    public ChestSocketListener(Chestsocket plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIntentoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        player.sendTitle("asd", "gsdfasd");
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        InventoryHolder inventoryHolder = clickedInventory.getHolder();
        if (!(inventoryHolder instanceof GameInventory)) {
            return;
        }

        event.setCancelled(true);
        if (event.getClick() != ClickType.LEFT) {
            return;
        }

        GameInventory inventory = (GameInventory) inventoryHolder;
        inventory.handleClick(event.getSlot() / 9, event.getSlot() % 9);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
//        if (event.getReason() == InventoryCloseEvent.Reason.PLUGIN) {
//            return;
//        }

        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof GameInventory)) {
            return;
        }

        GameInventory inventory = (GameInventory) inventoryHolder;
        inventory.handleClose();
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof GameInventory)) {
            return;
        }

        event.setCancelled(true);
    }

}
