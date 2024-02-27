import chatapp.chat.ChatConsole;
import chatapp.connection.ChatChannel;
import chatapp.connection.ChatConnection;
import chatapp.grpc.interceptor.ClientHeaderInterceptor;
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
                Grpc.newChannelBuilder("localhost:" + PORT, InsecureChannelCredentials.create())
                        .intercept(new ClientHeaderInterceptor()).build();

        ChatChannel.setChannel(channel);

        ChatConnection connection = new ChatConnection(channel);
        ChatConsole console = null;

        try (Scanner scanner = new Scanner(System.in)) {
            connection.login(scanner);
            if (ChatConnection.isConnected) {
                console = new ChatConsole();
                console.joinRoom(scanner);
                console.runChat(scanner);
            }
        } finally {
            channel.shutdownNow().awaitTermination(TIMEOUT, TimeUnit.SECONDS);

            System.out.println("Closing Client");
        }
    }
}
