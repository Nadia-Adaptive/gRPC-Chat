package chatapp.chat;

import chatapp.connection.ChatConnection;
import io.grpc.Channel;

import java.util.Scanner;

public class ChatConsole implements AutoCloseable {
    ChatClient client;

    public ChatConsole(final Channel channel) {
        client = new ChatClient(channel);
    }

    public void runChat(final ChatConnection connection, final Scanner scanner) {
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
    }

    public void close() {
        if (client != null) {
            client.closeClient();
        }
    }
}