import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

import java.time.Instant;

public class ChatClient {
    ChatServiceGrpc.ChatServiceStub serviceStub;
    StreamObserver responseObserver;

    public ChatClient(Channel channel) {
        serviceStub = ChatServiceGrpc.newStub(channel);
        responseObserver = new MessageObserver();
    }

    void sendMessage(final String message) {
        try{
        final var request = ChatApp.SendMessageRequest.newBuilder().setMessage(message).setRoomId(0).setUserId(0).build();
        serviceStub.sendMessage(request, responseObserver);
        } catch (final Exception e){
            System.out.println(e.getMessage());
        }
    }
}
