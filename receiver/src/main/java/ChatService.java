import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatService extends ChatServiceGrpc.ChatServiceImplBase {
    List<ChatApp.SendMessageResponse> messages;

    ChatService() {
        messages = new ArrayList<>();
    }

    @Override
    public void sendMessage(ChatApp.SendMessageRequest request, StreamObserver<ChatApp.SendMessageResponse> responseObserver) {
        try {
            System.out.println("Message received.");
            messages.add(ChatApp.SendMessageResponse.newBuilder().setMessage(request.getMessage()).setMessageId(messages.size()).setTimestamp(Instant.now().getEpochSecond()).build());
            messages.forEach(m ->
                    responseObserver.onNext(m));
            responseObserver.onCompleted();
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());

        }
    }
}
