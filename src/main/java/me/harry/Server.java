package me.harry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private ServerSocket serverSocket;
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

        while(true) {
            new ClientHandler((Socket) serverSocket.accept()).start();
            System.out.println("connected");
        }
    }

}
