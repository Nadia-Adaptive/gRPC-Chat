package chatapp.server;

import chatapp.user.User;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    Map<Integer, User> connectedUsers;

    public UserRepository() {
        connectedUsers = new HashMap<>();
    }


    public User addUser(final String username) {
        try {
            final var clientId = connectedUsers.size() + 1;
            final var user = new User(clientId, username);
            connectedUsers.put(clientId, user);
            return user;
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public User findById(final int userId) {
        return connectedUsers.get(userId);
    }
}

