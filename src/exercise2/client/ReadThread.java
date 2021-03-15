package exercise2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * This thread is responsible for reading server's input and printing it
 * to the console.
 * It runs in an infinite loop until the client disconnects from the server.
 */

public class ReadThread extends Thread{
    private BufferedReader reader;
    final Socket socket;
    private Client client;

    public ReadThread (Socket socket, Client client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println("\n" + response);
            } catch (SocketException ex) {
                if(ex.getMessage().equals("Connection reset")) {
                    System.out.println("[ERR] Connection with server lost! (connection reset)");
                }
                else if (ex.getMessage().equals("Socket closed")) {
                    System.out.println("[INFO] Connection with server closed");
                }
                else {
                    System.out.println("[ERR] Error reading from server: " + ex.getMessage());
                    ex.printStackTrace();
                }
                break;
            }
            catch (IOException ex) {
                System.out.println("[ERR] Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }

}
