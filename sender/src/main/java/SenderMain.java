import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import service.ChatConnection;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SenderMain {

    static void login(final Scanner scanner, final ChatConnection connection) {
        while (true) {
            System.out.println();
            System.out.println("Enter your username");
            final var username = scanner.nextLine();

            if (username.equalsIgnoreCase("q")) {
                break;
            }
            System.out.println();
            if (connection.connectToServer(username)) {
                break;
            }
            System.out.println("Error logging in.");
        }
    }

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        ChatClient client = new ChatClient(channel);
        ChatConnection connection = new ChatConnection(channel);

        try (Scanner scanner = new Scanner(System.in)) {
            login(scanner, connection);
            while (true) {
                System.out.println();
                System.out.println("Enter your message: ");
                final var message = scanner.nextLine();
                System.out.println();

                if (message.equalsIgnoreCase("q")) {
                    break;
                }

                try {
                    System.out.println();
                    client.sendMessage(message, connection.clientId);
                } catch (final Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } finally {
            client.closeClient();
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Closing Client");
        }
    }
}
