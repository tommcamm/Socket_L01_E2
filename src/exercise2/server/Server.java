package exercise2.server;

import exercise2.server.exceptions.FriendNotFoundException;
import exercise2.server.exceptions.SamePersonException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    static final int DEFAULT_PORT = 53000;

    private final List<Person> persons = new ArrayList<>();
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[INFO] Friendship server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[INFO] New client connection from " + socket.getRemoteSocketAddress());

                ClientThread newClient = new ClientThread(socket, this);
                newClient.start();
            }
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port;
        if (args.length < 1) {
            System.out.println("[!] Port number not defined in arguments!");
            System.out.println("[!] Using default port " + DEFAULT_PORT);
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        Server server = new Server(port);
        server.execute();
    }

    void doOperation(String message, ClientThread client) {
        System.out.println("[IN] Message received: " + message);

        Pattern name = Pattern.compile("[a-zA-Z]+");

        Matcher nameM = name.matcher(message);
        List<String> names = new ArrayList<>();

        while (nameM.find()) {
            names.add(nameM.group());
        }

        try {
            // Register specified person (+ps1)
            if (message.equals("quit")) {
                client.sendMessage("[OK] Closing connection...");
            }
            else if (message.matches("^\\+[a-zA-Z]+$")) {
                if (!persons.contains(new Person(message.substring(1)))) {
                    persons.add(new Person(message.substring(1)));
                    client.sendMessage("[OK] " + message.substring(1) + " registered on server");
                } else {
                    client.sendMessage("[OK] " + message.substring(1) + " is already registered...");
                }
                // Print all friends of specified person (ps1)
            } else if (message.matches("^[a-zA-Z]+$")) {
                StringBuilder str = new StringBuilder(message + "'s friends:\n");
                persons.get(persons.indexOf(new Person(message))).getFriends().forEach((n) -> {
                    str.append(n.getName()).append(", ");
                });
                client.sendMessage(str.toString());
                // Add a friend to person (+ps1|ps2)
            } else if (message.matches("^\\+[a-zA-Z]+\\|[a-zA-Z]+$")) {
                persons.get(persons.indexOf(
                        new Person(names.get(0))))
                        .addFriend(persons.get(persons.indexOf(
                                new Person(names.get(1))))
                        );

                client.sendMessage("[OK] Friend relationship created");
            } else if (message.matches("^\\+[a-zA-Z]+\\|[a-zA-Z]+(;[a-zA-Z]+)*$")) {
                for (int i = 1; i < names.size(); i++) {
                    persons.get(persons.indexOf(new Person(names.get(0))))
                            .addFriend(persons.get(persons.indexOf(new Person(names.get(i)))));
                }

                client.sendMessage("[OK] Friends added to " + names.get(0));

            } else if (message.matches("^-[a-zA-Z]+\\|[a-zA-Z]+$")) {
                persons.get(persons.indexOf(new Person(names.get(0))))
                        .removeFriend(persons.get(persons.indexOf(new Person(names.get(1)))));

                client.sendMessage("[OK] Friend removed from " + names.get(0) + "'s list");

            } else if (message.matches("^-[a-zA-Z]+\\|[a-zA-Z]+(;[a-zA-Z]+)*$")) {
                for (int i = 1; i < names.size(); i++) {
                    persons.get(persons.indexOf(new Person(names.get(0))))
                            .removeFriend(persons.get(persons.indexOf(new Person(names.get(i)))));
                }

                client.sendMessage("[OK] Friends removed from" + names.get(0) + "'s list");

            } else if (message.matches("^-[a-zA-Z]+$")) {
                if (persons.contains(new Person(message.substring(1)))) {
                    for (int i = 0; i < persons.size(); i++) {
                        if (persons.get(i).getFriends().contains(new Person(names.get(0))))
                            persons.get(i).removeFriend(persons.get(persons.indexOf(new Person(names.get(0)))));
                    }
                    persons.remove(new Person(names.get(0)));

                    client.sendMessage("[OK] " + names.get(0) + " Unregistered");
                }
                else client.sendMessage("[ERR] Specified name is not registered");

            } else if (message.matches("^[a-zA-Z]+\\|[a-zA-Z]+$")) {
                client.sendMessage(persons.get(persons.indexOf(new Person(names.get(0))))
                        .getFriends().contains(new Person(names.get(1))) ? "True" : "False");
            } else {
                client.sendMessage("[ERR] Unrecognized operation");
            }
        } catch (FriendNotFoundException ex) {
            client.sendMessage("[ERR] You cannot unfriend a person that is not your friend...");
        } catch (IndexOutOfBoundsException ex) {
            client.sendMessage("[ERR] Specified name(s) not registered");
        } catch (SamePersonException ex) {
            client.sendMessage("[ERR] You cannot be friend of yourself...");
        } catch (Exception e) {
            System.out.println("[ERR] Unrecognized error occurred!");
            e.printStackTrace();
        }
    }
}