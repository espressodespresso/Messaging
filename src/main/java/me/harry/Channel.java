package me.harry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Channel {
    private String name;
    private ArrayList<ClientHandler> connected = new ArrayList<>();
    private HashMap<Long, String> messageHistory = new HashMap<>();

    public Channel(String name) {
        this.name = name;
    }

    public String GetName() {
        return name;
    }

    public boolean Subscribe(ClientHandler clientHandler) {
        if(connected.contains(clientHandler)) {
            return false;
        }

        connected.add(clientHandler);
        return true;
    }

    public boolean Unsubscribe(ClientHandler clientHandler) {
        if(!connected.contains(clientHandler)) {
            return false;
        }

        connected.remove(clientHandler);
        return true;
    }

    public JsonArray Get(int after) {
        JsonArray messages = new JsonArray();
        for (Long wasd: messageHistory.keySet()) {
            if(wasd > after) {
                messages.add("Time : " + wasd.intValue() + " | " + messageHistory.get(wasd));
            }
        }

        return messages;
    }

    public void Publish(JsonObject object) {
        String body = object.get("body").getAsString();
        String message = "*" + object.get("from").getAsString() + " | " + name + " : " + body;
        for (ClientHandler client: connected) {
            client.GetWriter().println(message);
        }

        messageHistory.put(new Date().getTime(), body);
    }
}
