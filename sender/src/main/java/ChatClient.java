import io.grpc.Channel;

import java.time.Instant;

public class ChatClient {
    ChatServiceGrpc.ChatServiceBlockingStub serviceStub;

    public ChatClient(Channel channel) {
        serviceStub = ChatServiceGrpc.newBlockingStub(channel);
    }

    void sendMessage(final String message) {
        final var request = ChatApp.SendMessageRequest.newBuilder().setMessage(message).setRoomId(0).setUserId(0).build();
        final var result = serviceStub.sendMessage(request);
        while (result.hasNext()) {
            final var response = result.next();
            System.out.println();
            System.out.println("%s [%s]".formatted(Instant.ofEpochSecond(response.getTimestamp()), response.getMessageId()));
            System.out.println("\t" + response.getMessage());
        }
    }
}
