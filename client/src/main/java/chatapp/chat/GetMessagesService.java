package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass.MessagesResponse;
import chatapp.ChatService.ReactorChatServiceGrpc;
import chatapp.ChatService.ReactorChatServiceGrpc.ReactorChatServiceStub;
import chatapp.connection.ChatChannel;
import chatapp.service.ReactiveService;
import com.google.protobuf.Empty;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;

public class GetMessagesService implements ReactiveService<List<ChatMessage>> {
    private Consumer<List<ChatMessage>> callback;
    private Disposable responseStream;
    private Consumer<Throwable> errorHandler;

    public List<ChatMessage> messages;

    public GetMessagesService() {
    }

    public void get() {
        final ReactorChatServiceStub stub = ReactorChatServiceGrpc.newReactorStub(ChatChannel.getChannel());

        responseStream = Flux
                .just(Empty.newBuilder().build())
                .transform(stub::getMessages)
                .doOnError(errorHandler)
                .subscribe(this::processMessage);
    }

    private void processMessage(final MessagesResponse m) {
        callback.accept(m.getMessagesList().stream().map(ChatMessageMapper::mapToMessage).toList());
    }

    @Override
    public void setErrorHandler(final Consumer<Throwable> t) {
        this.errorHandler = t;
    }

    @Override
    public void setSuccessHandler(final Consumer<List<ChatMessage>> action) {
        this.callback = action;
    }

    public void closeService() {
        responseStream.dispose();
    }

    public void submit(final Consumer<List<ChatMessage>> processMessages) {
        callback = processMessages;
    }
}
