package me.harry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    private Socket client;
    private PrintWriter writer;
    private BufferedReader reader;
    private ArrayList<ClientHandler> clients;
    private ArrayList<Channel> channels;
    private JSONHandler jsonHandler = new JSONHandler();
    private String identity;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients, ArrayList<Channel> channels) throws IOException {
        client = socket;
        writer = new PrintWriter(client.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.clients = clients;
        this.channels = channels;
        start();
    }

    public void run() {
        try {
            String inputString;
            while ((inputString = reader.readLine()) != null) {
                System.out.println(inputString);
                JsonObject object = new Gson().fromJson(inputString, JsonObject.class);
                switch (object.get("_class").getAsString()) {
                    case "OpenRequest": {
                        identity = object.get("identity").getAsString();
                        boolean exists = false;
                        for (Channel channel : channels) {
                            if(channel.GetName().equals(identity)) {
                                exists = true;
                                channel.Subscribe(this);
                                for (String message: channel.GetMessagesHashMap().values()) {
                                    GetWriter().println(message);
                                }
                                break;
                            }
                        }
                        if(!exists) {
                            Channel channel = new Channel(identity);
                            channel.Subscribe(this);
                            channels.add(channel);
                        }
                        GetWriter().println(jsonHandler.SuccessResponse());
                        break;
                    }
                    case "PublishRequest": {
                        Boolean located = false;
                        for (Channel channel : channels) {
                            if (channel.GetName().equals(identity)) {
                                channel.Publish(object.get("message").getAsJsonObject());
                                GetWriter().println(jsonHandler.SuccessResponse());
                                located = true;
                                break;
                            }
                        }
                        if (!located) {
                            GetWriter().println(jsonHandler.ErrorResponse("Error occurred, try again!"));
                        }
                        break;
                    }
                    case "SubscribeRequest": {
                        String channelName = object.get("channel").getAsString();
                        Channel channel = Exists(channelName);
                        if (channel != null) {
                            if (channel.Subscribe(this)) {
                                GetWriter().println(jsonHandler.SuccessResponse());
                            } else {
                                GetWriter().println(jsonHandler.ErrorResponse("You are already subscribed to this channel"));
                            }
                        } else {
                            GetWriter().println(jsonHandler.ErrorResponse("Channel does not exist"));
                        }
                        break;
                    }
                    case "UnsubscribeRequest": {
                        String channelName = object.get("channel").getAsString();
                        Channel channel = Exists(channelName);
                        if (channel != null) {
                            if (channel.Unsubscribe(this)) {
                                GetWriter().println(jsonHandler.SuccessResponse());
                            } else {
                                GetWriter().println(jsonHandler.ErrorResponse("You are not subscribed to this channel"));
                            }
                        } else {
                            GetWriter().println(jsonHandler.ErrorResponse("Channel does not exist"));
                        }
                        break;
                    }
                    case "GetRequest": {
                        for (Channel channel : channels) {
                            if (channel.GetName().equals(identity)) {
                                GetWriter().println(jsonHandler.MessageListResponse(channel.Get(object.get("after").getAsInt())));
                            }
                        }
                    }
                    default:
                        break;
                }
            }

            // Removing user data & terminating socket/threads correctly when disconnecting
            Thread.sleep(1000);
            System.out.println("Server -> Client disconnected");
            Channel channel = Exists(identity);
            if (channel != null) {
                for (ClientHandler clientHandler : channel.GetConnected()) {
                    if (clientHandler != this) {
                        channel.Unsubscribe(clientHandler);
                    }
                }
            }
            clients.remove(this);
            client.close();
            writer.close();
            reader.close();
            // Deprecated method, shows an error in certain IDE's but should not stop program running
            this.stop();
        } catch (IOException | InterruptedException io) {
            throw new RuntimeException(io);
        }
    }

    public PrintWriter GetWriter() {
        return writer;
    }

    public Channel Exists(String channelName) {
        for (Channel channel : channels) {
            GetWriter().println(channel.GetName());
            if(channel.GetName().equals(channelName)) {
                return channel;
            }
        }

        return null;
    }
}
