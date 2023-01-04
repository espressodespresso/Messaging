package me.harry;

import com.google.common.base.Stopwatch;
import com.google.gson.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private ArrayList<Channel> channels = new ArrayList<>() {};

    public static void main( String[] args )
    {
        try {
            new Server();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Server() throws IOException {
        int port = 12345;
        ServerSocket serverSocket = new ServerSocket(port);
        GetDataFile();
        Runtime.getRuntime().addShutdownHook(new ShutdownThead(channels));
        //channelHandler.start();
        System.out.println("Server started! Now listening on port " + port + "...");
        Stopwatch stopwatch = Stopwatch.createStarted();

        while(true) {
            ArrayList<ClientHandler> clients = new ArrayList<>();
            ClientHandler clientHandler = new ClientHandler(serverSocket.accept(), clients, channels, stopwatch);
            //clientHandler.start();
            clients.add(clientHandler);
            System.out.println("Server -> New client connected");
        }
    }

    public void GetDataFile() {
        try {
            JsonObject object = JsonParser.parseReader(new FileReader("temp.json")).getAsJsonObject();
            for (String channelName : object.keySet()) {
                HashMap<Long, String> messageHistory = new HashMap<>();
                JsonArray messages = object.get(channelName).getAsJsonArray();
                // Loops over each message in a saved channel
                for (int i=0; i < messages.size(); i++) {
                    String[] messageSplit = messages.get(i).getAsString().split(" ");
                    Long time = Long.parseLong(messageSplit[0]);
                    boolean first = true;
                    StringBuilder builder = new StringBuilder();
                    // Remakes string without first array pos
                    for (String string : messageSplit) {
                        if(first) {
                            first = false;
                            continue;
                        }
                        if(builder.isEmpty()) {
                            builder.append(string);
                        } else {
                            builder.append(" ").append(string);
                        }
                    }

                    messageHistory.put(time, builder.toString());
                }

                Channel channel = new Channel(channelName, messageHistory);
                channels.add(channel);
            }
            new File("temp.json").delete();
        } catch (FileNotFoundException ex) {
            System.out.println("WARNING : Unable to get message data (No Data?), ignoring...");
        }
    }
}

class ShutdownThead extends Thread {
    private ArrayList<Channel> channels = new ArrayList<>();

    @Override
    public void run() {
        System.out.println("Finishing up...");
        JsonObject object = new JsonObject();
        for (Channel channel: channels) {
            JsonArray messagesArray = new JsonArray();
            HashMap<Long, String> messages = channel.GetMessagesHashMap();
            for (Long time : messages.keySet()) {
                messagesArray.add(time + " " + messages.get(time));
            }
            object.add(channel.GetName(), messagesArray);
        }
        try {
            FileWriter writer = new FileWriter("temp.json");
            new Gson().toJson(object, writer);
            writer.flush();
            writer.close();
            new File("ChannelData.json").delete();
        } catch (IOException e) {
            System.out.println("Fatal Error occured, contact developer...");
            throw new RuntimeException(e);
        }
    }

    public ShutdownThead(ArrayList<Channel> channels) {
        this.channels = channels;
    }
}
