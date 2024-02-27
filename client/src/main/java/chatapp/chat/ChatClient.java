package chatapp.chat;

import chatapp.ChatService.ChatServiceGrpc;
import chatapp.ChatService.ChatServiceOuterClass.MessageRequest;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

public class ChatClient {
    private final StreamObserver<MessageRequest> requestObserver;

    private final MessageResponseHandler handler;

    public ChatClient(final Channel channel) {
        handler = new MessageResponseHandler();
        requestObserver = ChatServiceGrpc.newStub(channel).sendMessage(handler);
        // TODO - split request sending and observer creation into separate classes
    }

    public void sendMessage(final String message, final int roomId) {
        try {
            requestObserver.onNext(MessageRequest.newBuilder()
                    .setMessage(message)
                    .setRoomId(roomId)
                    .build());
        } catch (final RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
    }

    public void closeClient() {
        requestObserver.onCompleted();
    }

    public MessageResponseHandler getHandler() {
        return handler;
    }


}
