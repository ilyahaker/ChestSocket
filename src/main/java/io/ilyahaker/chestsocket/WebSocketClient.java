package io.ilyahaker.chestsocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class WebSocketClient implements WebSocket.Listener {

    private GameInventory inventory;
    private int count;
    StringBuilder builder = new StringBuilder();

    public WebSocketClient(GameInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("onOpen using subprotocol " + webSocket.getSubprotocol());
        webSocket.request(1L);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        builder.append(data);
        if (!last) {
            webSocket.request(1L);
            return new CompletableFuture<Void>();
        }

        JsonParser parser = new JsonParser();
        JsonElement element;
        element = parser.parse(builder.toString());
        JsonObject object = element.getAsJsonObject();
        builder = new StringBuilder();

        inventory.getInventory().clear();
        for (int row = 0; row < 6; row++) {
            if (!object.has(String.valueOf(row))) {
                continue;
            }

            JsonObject rowObject = object.getAsJsonObject(String.valueOf(row));
            for (int column = 0; column < 9; column++) {
                if (!rowObject.has(String.valueOf(column))) {
                    continue;
                }

                JsonObject columnObject = rowObject.getAsJsonObject(String.valueOf(column));
                String material = columnObject.get("material").getAsString();
                inventory.getInventory().setItem(row * 9 + column, new ItemStack(Material.getMaterial(material)));
            }
        }
//        JsonParser parser = new JsonParser();
//        JsonObject object = parser.parse(data.toString()).getAsJsonObject();
//        JsonArray cells = object.get("cells")
//                .getAsJsonArray();
//
////        inventory.getOwner().closeInventory();
//        inventory.getInventory().clear();
//        cells.forEach(cell -> {
//            JsonObject cellInfo = cell.getAsJsonObject();
//            Material material = Material.getMaterial(cellInfo.get("material").getAsString());
//            int row = cellInfo.get("i").getAsInt();
//            int column = cellInfo.get("j").getAsInt();
//            inventory.getInventory().setItem(row * 9 + column, material == null ? null : new ItemStack(material));
//        });
        webSocket.request(1L);
        return new CompletableFuture<Void>();
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("Bad day! " + webSocket.toString());
        error.printStackTrace();
        inventory.close("Connection lost!");
//        error.printStackTrace();
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("connection closed clear, code=" + statusCode + ", reason=" + reason);
        inventory.close("");
        return new CompletableFuture<Void>();
    }
}
