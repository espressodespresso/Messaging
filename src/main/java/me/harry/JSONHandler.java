package me.harry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

public class JSONHandler {

    private JsonObject GetObject(String _class) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("_class", _class);
        return jsonObject;
    }

    // Messages

    public JsonObject Message(String from, int when, String body) {
        JsonObject object = GetObject("Message");
        object.addProperty("from", from);
        object.addProperty("when", when);
        object.addProperty("body", body);
        return object;
    }

    // Requests

    public JsonObject OpenRequest(String identity) {
        JsonObject object = GetObject("OpenRequest");
        object.addProperty("identity", identity);
        return object;
    }

    public JsonObject PublishRequest(String identity, JsonObject message) {
        JsonObject object = GetObject("PublishRequest");
        object.addProperty("identity", identity);
        object.add("message", message);
        return object;
    }

    public JsonObject SubscibeRequest(String identity, String channel) {
        JsonObject object = GetObject("SubscribeRequest");
        object.addProperty("identity", identity);
        object.addProperty("channel", channel);
        return object;
    }

    public JsonObject UnsubscibeRequest(String identity, String channel) {
        JsonObject object = GetObject("UnsubscribeRequest");
        object.addProperty("identity", identity);
        object.addProperty("channel", channel);
        return object;
    }

    public JsonObject GetRequest(String identity, int after) {
        JsonObject object = GetObject("GetRequest");
        object.addProperty("identity", identity);
        object.addProperty("after", after);
        return object;
    }

    // Responses

    public JsonObject SuccessResponse() {
        return GetObject("SuccessResponse");
    }

    public JsonObject ErrorResponse(String error) {
        JsonObject object = GetObject("ErrorResponse");
        object.addProperty("error", error);
        return object;
    }

    public JsonObject MessageListResponse(List messages) {
        JsonObject object = GetObject("MessageListResponse");
        object.add("messages", new Gson().toJsonTree(messages));
        return object;
    }
}
