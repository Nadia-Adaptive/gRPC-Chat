package chatapp.server;

import chatapp.user.User;
import io.grpc.ServerTransportFilter;

import java.util.HashMap;
import java.util.Map;

public class ServerState {
    Map<Integer, User> connectedUsers;

    public ServerState() {
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
}

