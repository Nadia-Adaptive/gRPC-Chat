package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass;
import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;
import chatapp.ChatService.ChatServiceOuterClass.MessagesResponse;
import chatapp.ChatService.ReactorChatServiceGrpc;
import chatapp.ChatService.ReactorChatServiceGrpc.ReactorChatServiceStub;
import chatapp.connection.ChatChannel;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

public class SendMessageService {
    private boolean hasClosed = false;
    private Consumer<List<Message>> callback;
    private Disposable responseStream;

    public SendMessageService() {

    }

    public void sendMessage(final String message) {
        final ReactorChatServiceStub stub = ReactorChatServiceGrpc.newReactorStub(ChatChannel.getChannel());

        responseStream = Mono.just(ChatServiceOuterClass.MessageRequest.newBuilder()
                .setMessage(message)
                .build()).transform(stub::sendMessage).doOnError(this::reportError).subscribe();
    }

    private void processMessage(final MessagesResponse m) {
        callback.accept(m.getMessagesList().stream().map(this::mapToMessage).toList());
    }

    private Message mapToMessage(final MessageResponse response) {
        return new Message(response.getUsername(), response.getMessage(),
                Instant.ofEpochSecond(response.getTimestamp().getSeconds()));
    }

    private void reportError(final Throwable t) {
        System.out.println("Error - " + t.getMessage());
        System.out.println("Closing chat.");
        hasClosed = true;
    }

    public boolean hasClosed() {
        return hasClosed;
    }

    public void closeService() {
       hasClosed = true;
       responseStream.dispose();
    }

    public void submit(final Consumer<List<Message>> processMessages) {
        callback = processMessages;
    }
}
