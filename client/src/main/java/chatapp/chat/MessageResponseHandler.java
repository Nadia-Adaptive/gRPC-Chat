package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MessageResponseHandler implements StreamObserver<MessageResponse> {

    List<MessageResponse> messages;
    private boolean hasClosed = false;

    public MessageResponseHandler() {
        this.messages = new ArrayList<>();
    }

    public void onNext(final MessageResponse value) {
        messages.add(value);
    }

    @Override
    public void onError(final Throwable t) {
        System.out.println(t.getMessage());
        hasClosed = true;
    }

    @Override
    public void onCompleted() {
        System.out.println("End of chat log.");
        hasClosed = true;
    }

    public Stream<MessageResponse> getMessages() {
        return messages.stream();
    }

    public boolean hasMessages() {
        try {
            return getLastMessage().hasIsLast() && getLastMessage().getIsLast();
        } catch (final IndexOutOfBoundsException e) {
            return false;
        }
    }

    public void clearMessages() {
        messages.clear();
    }

    private MessageResponse getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    public boolean hasClosed() {
        return hasClosed;
    }
}
