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

public class ChannelHandler {

    public JsonArray GetLocalArray() {
        JsonArray array = new JsonArray();
        try {
            JsonObject object = JsonParser.parseReader(new FileReader("ChannelData.json")).getAsJsonObject();
            array = object.get("Channels").getAsJsonArray();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        return array;
    }
}
