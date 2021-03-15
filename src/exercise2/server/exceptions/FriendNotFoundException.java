package exercise2.server.exceptions;

public class FriendNotFoundException extends Exception{
    public FriendNotFoundException(String message) {
        super(message);
    }
}
