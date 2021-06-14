package io.ilyahaker.chestsocket;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

public class GameInventory implements InventoryHolder {

    @Getter
    private final Inventory inventory;

    @Getter
    private Player owner;
    private Chestsocket plugin;

//    private final GameObject[][] matrix;

    private int currentColumn, currentRow;

    private final WebSocket webSocket;

    public GameInventory(String title, Player owner, Chestsocket plugin) {
        inventory = Bukkit.createInventory(this, 6 * 9, title);
        this.owner = owner;
        this.plugin = plugin;


        webSocket = HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:8003/socoban/"), new WebSocketClient(this))
                .join();
    }


//    private void setItem(int row, int column) {
//        if (row < matrix.length && column < matrix[0].length) {
//            GameObject object = matrix[row][column];
//            ItemStack itemStack = object == null ? null : object.getItem();
//            inventory.setItem((row - currentRow) * 9 + column - currentColumn, itemStack);
//        } else {
//            inventory.setItem((row - currentRow) * 9 + column - currentColumn, null);
//        }
//    }

    public void handleClick(int row, int column) {
        JsonObject object = new JsonObject();
        object.addProperty("type", "click");
        object.addProperty("i", row);
        object.addProperty("j", column);

        webSocket.sendText(object.toString(), true);


//        int differenceRow = playerPosition.getKey() - row - currentRow,
//                differenceColumn = playerPosition.getValue() - column - currentColumn;
//
//        //inverse XOR operation
//        if ((Math.abs(differenceRow) == 1 && Math.abs(differenceColumn) == 0) == (Math.abs(differenceColumn) == 1 && Math.abs(differenceRow) == 0)) {
//            return;
//        }
//
//        Pair<Integer, Integer> currentPlayerPosition = new Pair<>(row + currentRow, column + currentColumn);
//        boolean needFilling = true;
//
//        //changing the starting point depending on player's position
//        if (differenceRow < 0 && matrix.length - currentPlayerPosition.getKey() >= 3 && currentPlayerPosition.getKey() >= 3) {
//            currentRow = Math.max(currentPlayerPosition.getKey() - 3, 0);
//        } else if (differenceRow > 0 && matrix.length - currentPlayerPosition.getKey() >= 4 && currentPlayerPosition.getKey() >= 2) {
//            currentRow = Math.max(currentPlayerPosition.getKey() - 2, 0);
//        } else if (differenceColumn < 0 && matrix[0].length - currentPlayerPosition.getValue() >= 5 && currentPlayerPosition.getValue() >= 4) {
//            currentColumn = Math.max(currentPlayerPosition.getValue() - 4, 0);
//        } else if (differenceColumn > 0 && matrix[0].length - currentPlayerPosition.getValue() >= 5 && currentPlayerPosition.getValue() >= 4) {
//            currentColumn = Math.max(currentPlayerPosition.getValue() - 4, 0);
//        } else {
//            needFilling = false;
//        }
//
//        if (needFilling) {
//            fillInventory();
//        } else {
//            setItem(playerPosition.getKey(), playerPosition.getValue());
//        }
//
//        playerPosition = currentPlayerPosition;
//        fillPlayers();
    }

    public void handleClose() {
        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Close inventory")
                .whenComplete((webSocket1, throwable) -> {
                    throwable.printStackTrace();
                    webSocket.abort();
                });
    }

    public void close(String message) {
        if (!message.isEmpty()) {
            owner.sendMessage(message);
        }

        Bukkit.getScheduler().runTask(plugin, () -> owner.closeInventory());
    }

    public void open() {
        owner.openInventory(inventory);
    }
}
