package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass;
import chatapp.connection.ChatConnection;
import io.grpc.Channel;

import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatConsole implements AutoCloseable {
    ChatClient client;

    public ChatConsole(final Channel channel) {
        client = new ChatClient(channel);
    }

    public void runChat(final ChatConnection connection, final Scanner scanner) {
        clearChat();
        System.out.println("Enter your message: ");

        ExecutorService watchStreamExecutor = Executors.newSingleThreadExecutor();

        watchStreamExecutor.submit(() -> {
            while (true) {
                if (client.hasMessages()) {
                    clearChat();
                    client.getMessages().forEach(this::printMessage);
                    client.getMessages().removeAll(client.getMessages());
                    System.out.println("Enter your message: ");
                }
            }
        });

        while (true) {
            final var message = scanner.nextLine();
            System.out.println();

            if (message.equalsIgnoreCase("q")) {
                break;
            }

            try {
                System.out.println();
                client.sendMessage(message, connection.clientId);
                clearChat();
            } catch (final Exception e) {
                System.out.println(e.getMessage());
            }
        }
        watchStreamExecutor.shutdown();
    }

    public void close() {
        if (client != null) {
            client.closeClient();
        }
    }

    private void printMessage(final ChatServiceOuterClass.MessageResponse m) {
        System.out.println();
        System.out.printf("%s [%s]%n", m.getUsername(), Instant.ofEpochSecond(m.getTimestamp().getSeconds()));
        System.out.println("\t" + m.getMessage());
    }

    private void clearChat() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}