package me.harry;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    /*public static void main(String[] args) {
        int port = 12345;
        String hostName = "localhost";
        Scanner input = new Scanner(System.in);

        try
        {
            Socket client = new Socket(hostName, port);
            PrintWriter writer = new PrintWriter(
                    client.getOutputStream(), true
            );
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    client.getInputStream()
            ));

            String inputLine;
            System.out.println("Send a message to the server.");
            while((inputLine = input.nextLine())!=null)
            {
                writer.println(inputLine);
                String echo = reader.readLine();
                if(echo==null)
                    break;
                System.out.println("Echo : " + echo);
            }
            client.close();
        }catch(IOException ie)
        {
            System.out.println(ie.getMessage());
        }

        input.close();

    }*/

    private String hostName = "localhost";
    private int portNumber = 12345;
    private Scanner input = new Scanner(System.in);
    private Socket client;
    private PrintWriter writer;
    private BufferedReader reader;


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

        System.out.println("Enter your message :");
        String inputString;
        while ((inputString = input.nextLine()) != null) {
            writer.println(inputString);
            String echo = reader.readLine();
            if(echo != null) {
                System.out.println("Echo : " + echo);
            }
        }
    }
}
