package exercise2.server;

import exercise2.server.exceptions.AlreadyFriendException;
import exercise2.server.exceptions.FriendNotFoundException;
import exercise2.server.exceptions.SamePersonException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person {
    private final String name;
    private final List<Person> friends;

    public Person(String name) {
        this.name = name;
        this.friends = new ArrayList<>();
    }

    // TODO: controllare se esistè già la persona, nel caso eccezzione!
    public void addFriend(Person person) throws AlreadyFriendException, SamePersonException {
        if(friends.contains(person))
            throw new AlreadyFriendException(name + "is already friend with " + person.getName());
        if(person.equals(this))
            throw new SamePersonException(name + "is trying to be friend of himself...");
        friends.add(person);
    }

    // TODO: Controllare se la persona da togliere non esiste
    public void removeFriend(Person person) throws FriendNotFoundException {
        if(!friends.contains(person))
            throw new FriendNotFoundException("Specified person not in friend list");
        friends.remove(person);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", friends=" + friends +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public List<Person> getFriends() {
        return friends;
    }
}
