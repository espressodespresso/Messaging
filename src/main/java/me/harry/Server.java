package me.harry;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private ArrayList<Channel> channels = new ArrayList<>() {};
    private int port = 12345;

    public static void main( String[] args )
    {
        try {
            new Server();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Server() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started! Now listening on port " + port + "...");

        while(true) {
            ClientHandler clientHandler = new ClientHandler(serverSocket.accept(), clients, channels);
            clientHandler.start();
            System.out.println("Server -> New client connected");
        }
    }

}
