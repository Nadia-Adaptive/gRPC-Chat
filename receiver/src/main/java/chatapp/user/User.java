package chatapp.user;

public class User {
    private int id;
    private final String username;

    public User(final int id, final String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
