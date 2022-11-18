package me.harry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {

    private Socket client;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket socket) throws IOException {
        client = socket;
        writer = new PrintWriter(client.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    public void run() {
        try {
            String inputString;
            while((inputString = reader.readLine()) != null) {
                System.out.println("Message : " + inputString);
                writer.println(inputString);
                System.out.println("Echo");
            }


        } catch (IOException io) {

        }
    }
}
