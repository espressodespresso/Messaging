package me.harry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.*;
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


    public static void main( String[] args )
    {
        try {
            new Client();
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
                                if(echo != null) {
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
                                                if(!array.isEmpty()) {
                                                    System.out.println("Server -> Retrieved requested messages...");
                                                    for(int i =0; i < object.size(); i++) {
                                                        System.out.println(array.get(i).getAsString());
                                                    }
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
                            if(inputArray[0] == ':') {
                                String[] splitInput = inputString.split(" ");
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
                                    case ":get":
                                        writer.println(jsonHandler.GetRequest(username, Integer.parseInt(splitInput[1])));
                                        break;
                                    case ":quit":
                                        System.exit(1);
                                    case ":help":
                                        System.out.println(definedMessage.HelpMenu());
                                        break;
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
