package exercise2.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    static final int DEFAULT_PORT = 53000;
    static final String DEFAULT_HOST = "localhost";

    private String hostname;
    private int port;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to friendship server");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        String hostname;
        int port;
        if (args.length < 2) {
            System.out.println("[!] arguments not found");
            System.out.println("[!] using default host '" + DEFAULT_HOST +"' at port " + DEFAULT_PORT);
            hostname = DEFAULT_HOST;
            port = DEFAULT_PORT;
        } else {
            hostname = args[0];
            port = Integer.parseInt(args[1]);
        }

        Client client = new Client(hostname, port);
        client.execute();
    }
}
