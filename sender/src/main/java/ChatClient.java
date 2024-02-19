import chatapp.ChatService.ChatServiceGrpc;
import chatapp.ChatService.ChatServiceOuterClass;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

public class ChatClient {
    ChatServiceGrpc.ChatServiceStub serviceStub;
    StreamObserver<ChatServiceOuterClass.MessageRequest> requestObserver;

    public ChatClient(Channel channel) {
        serviceStub = ChatServiceGrpc.newStub(channel);
        requestObserver = serviceStub.sendMessage(new MessageObserver());
    }

    void sendMessage(final String message, final int clientId) {
        try {
            requestObserver.onNext(ChatServiceOuterClass.MessageRequest.newBuilder().setMessage(message).setRoomId(0).setUserId(clientId).build());
        } catch (final RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
    }

    void closeClient() {
        requestObserver.onCompleted();
    }
}