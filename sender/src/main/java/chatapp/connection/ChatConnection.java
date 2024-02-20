package chatapp.connection;

import chatapp.ConnectionService.ConnectionServiceGrpc;
import chatapp.ConnectionService.ConnectionServiceOuterClass;
import io.grpc.Channel;

import java.util.Scanner;

public class ChatConnection {
    private final ConnectionServiceGrpc.ConnectionServiceBlockingStub stub;
    public int clientId;

    public ChatConnection(final Channel channel) {
        stub = ConnectionServiceGrpc.newBlockingStub(channel);
    }

    public void login(final Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.println("Enter your username");
            final var username = scanner.nextLine();

            if (username.equalsIgnoreCase("q")) {
                break;
            }
            System.out.println();
            if (connectToServer(username)) {
                break;
            }
            System.out.println("Error logging in.");
        }
    }

    boolean connectToServer(final String username) {
        clientId = stub.connect(
                ConnectionServiceOuterClass.ConnectionRequest.newBuilder()
                        .setUsername(username)
                        .build())
                .getClientId();
        System.out.println("Connected with client id of " + clientId);
        return clientId != Integer.MIN_VALUE;
    }
}
