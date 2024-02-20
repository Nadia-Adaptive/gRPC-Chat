package chatapp.chat;

import chatapp.ChatService.ChatServiceGrpc;
import chatapp.ChatService.ChatServiceOuterClass;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class ChatClient {
    private StreamObserver<ChatServiceOuterClass.MessageRequest> requestObserver;
    private List<ChatServiceOuterClass.MessageResponse> messages;

    public ChatClient(final Channel channel) {
        messages = new ArrayList<>();
        requestObserver = ChatServiceGrpc.newStub(channel).sendMessage(new ChatLog(messages));
    }

    public void sendMessage(final String message, final int clientId) {
        messages.removeAll(messages);
        try {
            requestObserver.onNext(ChatServiceOuterClass.MessageRequest.newBuilder()
                    .setMessage(message)
                    .setRoomId(0)
                    .setUserId(clientId)
                    .build());
        } catch (final RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
    }

    public void closeClient() {
        requestObserver.onCompleted();
    }

    public List<ChatServiceOuterClass.MessageResponse> getMessages() {
        return messages;
    }

    public boolean hasMessages() {
        try {
            return getLastMessage().hasIsLast() && getLastMessage().getIsLast();
        } catch (final IndexOutOfBoundsException e) {
            return false;
        }
    }

    private ChatServiceOuterClass.MessageResponse getLastMessage() {
        return messages.get(messages.size() - 1);
    }
}
