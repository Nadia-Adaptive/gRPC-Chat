import chatapp.chat.ChatConsole;
import chatapp.connection.ChatConnection;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SenderMain {
    static final int TIMEOUT = 5;
    static final int PORT = Integer.parseInt(System.getenv("CHAT_PORT"));

    public static void main(final String[] args) throws Exception {
        ManagedChannel channel =
                Grpc.newChannelBuilder("localhost:" + PORT, InsecureChannelCredentials.create()).build();

        ChatConnection connection = new ChatConnection(channel);

        try (Scanner scanner = new Scanner(System.in); ChatConsole console = new ChatConsole(channel);) {
            connection.login(scanner);
            console.runChat(connection, scanner);
        } finally {
            channel.shutdownNow().awaitTermination(TIMEOUT, TimeUnit.SECONDS);
            System.out.println("Closing Client");
        }
    }
}
