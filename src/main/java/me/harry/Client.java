package me.harry;

import com.google.gson.*;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private String hostName = "localhost";
    private int portNumber = 12345;
    private Scanner input = new Scanner(System.in);
    private Socket client;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    private JSONHandler jsonHandler = new JSONHandler();
    private List<String> subscribed = new ArrayList<>();
    private String currentChannel;
    private Interface definedMessage = new Interface();


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
        client = new Socket(hostName, portNumber);
        writer = new PrintWriter(client.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        System.out.println("Enter your name :");
        username = input.nextLine();
        writer.println(jsonHandler.OpenRequest(username));
        currentChannel = username;
        System.out.println("Opened channel " + username + "...");
        subscribed.add(username);
        System.out.println(definedMessage.WelcomeMessage());

        Thread readerThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                String echo = reader.readLine();
                                if (echo != null) {
                                    try {
                                        JsonObject object = new Gson().fromJson(echo, JsonObject.class);
                                        switch (object.get("_class").getAsString()) {
                                            case "SuccessResponse": {
                                                System.out.println("Server -> Success!");
                                                break;
                                            }
                                            case "ErrorResponse": {
                                                System.out.println("Error -> " + object.get("error").getAsString());
                                                break;
                                            }
                                            case "MessageListResponse": {
                                                JsonArray array = object.get("messages").getAsJsonArray();
                                                if (!array.isEmpty()) {
                                                    System.out.println("Server -> Retrieved requested messages...");
                                                    System.out.println(array);
                                                    /*for(int i =0; i < object.size() - 1; i++) {
                                                        System.out.println(array.get(i).getAsString());
                                                    }*/
                                                } else {
                                                    System.out.println("Server -> No messages found at timestamp");
                                                }
                                                break;
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
                }
        );
        readerThread.start();
        Thread inputThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        String inputString;
                        while ((inputString = input.nextLine()) != null) {
                            char[] inputArray = inputString.toCharArray();
                            if (inputArray[0] == ':') {
                                String[] splitInput = inputString.split(" ");
                                try {
                                    switch (splitInput[0].toLowerCase()) {
                                        case ":subscribe": {
                                            String channelName = splitInput[1];
                                            writer.println(jsonHandler.SubscibeRequest(username, channelName));
                                            currentChannel = channelName;
                                            break;
                                        }
                                        case ":unsubscribe": {
                                            String channelName = splitInput[1];
                                            writer.println(jsonHandler.UnsubscibeRequest(username, channelName));
                                            currentChannel = channelName;
                                            break;
                                        }
                                        case ":channels":
                                            System.out.println("Available Channels");
                                            try {
                                                JsonObject object = JsonParser.parseReader(new FileReader("ChannelData.json")).getAsJsonObject();
                                                JsonArray array = object.get("Channels").getAsJsonArray();
                                                for (JsonElement element: array) {
                                                    System.out.println(" - " + element.getAsString());
                                                }
                                            } catch (FileNotFoundException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                            break;
                                        case ":get":
                                            writer.println(jsonHandler.GetRequest(username, Integer.parseInt(splitInput[1])));
                                            break;
                                        case ":quit":
                                            System.exit(1);
                                        case ":help":
                                            System.out.println(definedMessage.HelpMenu());
                                            break;
                                    }
                                } catch (ArrayIndexOutOfBoundsException ex) {
                                    System.out.println("Invalid input, enter :help to show available commands");
                                }
                            } else {
                                writer.println(jsonHandler.PublishRequest(currentChannel, jsonHandler.Message(username, 0, inputString)));
                            }
                        }
                    }
                }
        );
        inputThread.start();
    }
}
