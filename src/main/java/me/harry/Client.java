package me.harry;

import com.google.gson.*;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
    private ArrayList<String> subscribed = new ArrayList<>();
    private Interface textblocks = new Interface();
    private String currentChannel;
    private AwaitType await;
    private String awaitString;
    private boolean menu;
    public static void main(String[] args) {
        try {
            new Client();
        } catch (ConnectException ex) {
            System.out.println("Unable to connect to server... (" + ex.getMessage() + ")");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Client() throws IOException {
        Socket client = new Socket("localhost", 12345);
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

        // Initial Client Setup
        System.out.println("Enter your name :");
        Scanner input = new Scanner(System.in);
        String username = input.nextLine();
        JSONHandler jsonHandler = new JSONHandler();
        writer.println(jsonHandler.OpenRequest(username));
        System.out.println("Opened channel " + username + "...");
        subscribed.add(username);
        currentChannel = username;
        System.out.println(textblocks.WelcomeMessage());
        menu = true;

        // InputThread
        new Thread(
                () -> {
                    String inputString;
                    while ((inputString = input.nextLine()) != null) {
                        char[] inputArray = inputString.toCharArray();
                        if (inputArray[0] == ':') {
                            try {
                                String[] splitInput = inputString.split(" ");
                                String channelName;
                                switch (splitInput[0].toLowerCase()) {
                                    case ":subscribe" -> {
                                        channelName = splitInput[1];
                                        await = AwaitType.SUBSCRIBE;
                                        awaitString = channelName;
                                        writer.println(jsonHandler.SubscibeRequest(username, channelName));
                                    }
                                    case ":unsubscribe" -> {
                                        channelName = splitInput[1];
                                        await = AwaitType.UNSUBSCRIBE;
                                        awaitString = channelName;
                                        writer.println(jsonHandler.UnsubscibeRequest(username, channelName));
                                    }
                                    case ":channels" -> {
                                        System.out.println("Available Channels");
                                        try {
                                            JsonObject object = JsonParser.parseReader(new FileReader("ChannelData.json")).getAsJsonObject();
                                            JsonArray array = object.get("Channels").getAsJsonArray();
                                            for (JsonElement element : array) {
                                                System.out.println(" - " + element.getAsString());
                                            }
                                        } catch (FileNotFoundException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }

                                    case ":get" -> writer.println(jsonHandler.GetRequest(username, Integer.parseInt(splitInput[1])));
                                    case ":quit" -> System.exit(1);
                                    case ":menu" -> System.out.println(textblocks.MenuCommand());
                                    case ":current" -> {
                                        System.out.println("Current Channel");
                                        if(currentChannel == null) {
                                            System.out.println("None");
                                        } else {
                                            System.out.println(currentChannel);
                                        }
                                    }
                                    case ":subscribed" -> {
                                        System.out.println("Subscribed Channels");
                                        if(subscribed.isEmpty()) {
                                            System.out.println("None");
                                        } else {
                                            for (String name : subscribed) {
                                                System.out.println(" - " + name);
                                            }
                                        }
                                    }
                                    case ":search" -> {
                                        await = AwaitType.SEARCH;
                                        StringBuilder builder = new StringBuilder();
                                        boolean first = true;
                                        for (String string : splitInput) {
                                            if(string != ":search") {
                                                if(first) {
                                                    builder.append(string);
                                                    first = false;
                                                } else {
                                                    builder.append(" ").append(string);
                                                }
                                            }
                                        }
                                        awaitString = builder.toString();
                                        writer.println(jsonHandler.GetRequest(username, 0));
                                    }
                                    case ":select" -> {
                                        channelName = splitInput[1];
                                        if(subscribed.contains(channelName)) {
                                            System.out.println("Select ->You have now selected the channel " + channelName);
                                            currentChannel = channelName;
                                        } else if(currentChannel.equals(channelName)) {
                                            System.out.println("Select -> You have already selected this channel!");
                                        } else {
                                            System.out.println("Select -> You are not subscribed to the channel " + channelName);
                                        }
                                    }
                                    default -> System.out.println("Invalid command, use :help to show available commands");
                                }
                            } catch (ArrayIndexOutOfBoundsException ex) {
                                System.out.println("Error -> Invalid command, enter :help to show available commands");
                            }
                        } else {
                            if(currentChannel != null) {
                                writer.println(jsonHandler.PublishRequest(currentChannel, jsonHandler.Message(username, 0, inputString)));
                            } else {
                                System.out.println("Error -> You haven't selected a channel to message, use :select followed by a subscribed channel");
                            }
                        }
                    }
                }
        ).start();


        // Reader Thread
        new Thread(
                () -> {
                    while (true) {
                        try {
                            String echo = reader.readLine();
                            if (echo != null) {
                                try {
                                    JsonObject object = new Gson().fromJson(echo, JsonObject.class);
                                    switch (object.get("_class").getAsString()) {
                                        case "SuccessResponse" ->{
                                            System.out.println("Server -> Success!");
                                            if(await != null) {
                                                switch (await) {
                                                    case SUBSCRIBE -> {
                                                        currentChannel = awaitString;
                                                        subscribed.add(currentChannel);
                                                    }
                                                    case UNSUBSCRIBE -> {
                                                        subscribed.remove(awaitString);
                                                        if(awaitString.equals(currentChannel)) {
                                                            currentChannel = null;
                                                        }
                                                    }
                                                }
                                                awaitString = "";
                                                await = null;
                                            }
                                        }
                                        case "ErrorResponse" -> System.out.println("Error -> " + object.get("error").getAsString());
                                        case "MessageListResponse" -> {
                                            JsonArray array = object.get("messages").getAsJsonArray();
                                            if(await == AwaitType.SEARCH) {
                                                String[] splitAwait = awaitString.split(" ");
                                                ArrayList<String> containsList = new ArrayList<>();
                                                if(!array.isEmpty()) {
                                                    for (JsonElement element : array) {
                                                        String message = element.getAsString();
                                                        boolean contains = false;
                                                        for (String string : splitAwait) {
                                                            if(message.contains(string)) {
                                                                contains = true;
                                                            } else {
                                                                contains = false;
                                                            }
                                                        }
                                                        if(contains) {
                                                            containsList.add(message);
                                                        }
                                                    }
                                                    if(containsList.isEmpty()) {
                                                        System.out.println("Search -> No messages contain those keywords");
                                                    } else {
                                                        System.out.println("Search -> Messages found containing (" + awaitString + ")");
                                                        for (String message : containsList) {
                                                            System.out.println(message);
                                                        }
                                                    }
                                                } else {
                                                    System.out.println("Search -> No messages to be searched");
                                                }
                                                await = null;
                                                awaitString = "";
                                            } else {
                                                if (!array.isEmpty()) {
                                                    System.out.println("Server -> Retrieved requested messages...");
                                                    for (JsonElement element : array) {
                                                        System.out.println(element.getAsString());
                                                    }
                                                } else {
                                                    System.out.println("Server -> No messages found at timestamp");
                                                }
                                            }
                                        }
                                    }
                                } catch (JsonSyntaxException exception) {
                                    System.out.println("Server -> " + echo);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        ).start();
    }
}

enum AwaitType {
    SUBSCRIBE,
    UNSUBSCRIBE,
    SEARCH
}
