package chatapp.connection;

import chatapp.ConnectionService.ConnectionServiceGrpc;
import chatapp.ConnectionService.ConnectionServiceOuterClass;
import chatapp.console.ApplicationDisplay;
import io.grpc.Channel;

public class ChatConnection {
    public static boolean isConnected;
    private final ConnectionServiceGrpc.ConnectionServiceBlockingStub stub;
    public static int clientId = Integer.MIN_VALUE;
    public static int roomId = Integer.MIN_VALUE;
            // TODO - use singleton? Or provide headerinterceptor a class instance

    public ChatConnection(final Channel channel) {
        stub = ConnectionServiceGrpc.newBlockingStub(channel);
        isConnected = false;
        clientId = Integer.MIN_VALUE;
    }

    public void login(final ApplicationDisplay display) {
        while (true) {
            System.out.println();
            System.out.println("Enter your username");
            final var username = display.readString();

            if (username.equalsIgnoreCase("q")) {
                break;
            }
            System.out.println();
            isConnected = connectToServer(username);
            if (isConnected) {
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
