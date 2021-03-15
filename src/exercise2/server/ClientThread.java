package exercise2.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread extends Thread{
    private Socket socket;
    private Server server;
    private PrintWriter writer;

    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                server.doOperation(clientMessage, this);

            } while (!clientMessage.equals("quit"));

            socket.close();

        } catch (SocketException ex) {
            if (ex.getMessage().equals("Connection reset"))
                System.out.println("[INFO] Client connection lost (reset)");
            else {
                System.out.println("Error in ClientThread: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        catch (IOException ex) {
            System.out.println("Error in ClientThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    void sendMessage(String message) {
        System.out.println("[OUT] Message sent: " + message);
        writer.println(message);
    }
}
