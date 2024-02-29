import chatapp.chat.ChatView;
import chatapp.console.ConsoleDisplay;
import chatapp.chatroom.ChatRoomView;
import chatapp.connection.ChatChannel;
import chatapp.connection.ChatConnection;
import chatapp.grpc.interceptor.ClientHeaderInterceptor;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

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

        try (ConsoleDisplay display = new ConsoleDisplay()) {
            connection.login(display);
            if (ChatConnection.isConnected) {

                final var chatRoomView = new ChatRoomView(display);

                chatRoomView.run();

                final var room = chatRoomView.getChatRoom();
                if (room != null) {
                    new ChatView(display).runChat(room);
                }
            }
        } finally {
            channel.shutdownNow().awaitTermination(TIMEOUT, TimeUnit.SECONDS);

            System.out.println("Closing Client");
        }
    }
}
