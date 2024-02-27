package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;
import chatapp.ChatService.ChatServiceOuterClass.MessagesResponse;
import chatapp.ChatService.ReactorChatServiceGrpc;
import chatapp.ChatService.ReactorChatServiceGrpc.ReactorChatServiceStub;
import chatapp.connection.ChatChannel;
import com.google.protobuf.Empty;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

public class GetMessagesService {
    private boolean hasClosed = false;
    private Consumer<List<Message>> callback;
    private Disposable responseStream;

    public GetMessagesService() {

    }

    public void getMessagesService() {
        final ReactorChatServiceStub stub = ReactorChatServiceGrpc.newReactorStub(ChatChannel.getChannel());

        responseStream = Flux
                .just(Empty.newBuilder().build())
                .transform(stub::getMessages)
                .doOnError(this::reportError)
                .subscribe(this::processMessage);
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
        getMessagesService();
    }
}
