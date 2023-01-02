package me.harry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Channel {
    private String name;
    private ArrayList<ClientHandler> connected = new ArrayList<>();
    private HashMap<Long, String> messageHistory = new HashMap<>();

    public Channel(String name) {
        this.name = name;
        SaveLocal(name);
    }

    public Channel(String name, HashMap<Long, String> messageHistory) {
        this.name = name;
        this.messageHistory = messageHistory;
        SaveLocal(name);
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
        for (Long time: messageHistory.keySet()) {
            if(time > after) {
                messages.add("Time : " + time.intValue() + " -> " + messageHistory.get(time));
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

        messageHistory.put(new Date().getTime(), message);
    }

    private void SaveLocal(String channelName) {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        try {
            object = JsonParser.parseReader(new FileReader("ChannelData.json")).getAsJsonObject();
            array = object.get("Channels").getAsJsonArray();
            array.add(channelName);
        } catch (FileNotFoundException ex) {
            array = new JsonArray();
            array.add(channelName);
            object.add("Channels", array);
        }

        try {
            FileWriter writer = new FileWriter("ChannelData.json");
            new Gson().toJson(object, writer);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ArrayList<ClientHandler> GetConnected() {
        return connected;
    }

    public HashMap<Long, String> GetMessagesHashMap() { return messageHistory; }
}