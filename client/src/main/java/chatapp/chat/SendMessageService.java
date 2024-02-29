package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass.SendMessageRequest;
import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;
import chatapp.ChatService.ReactorChatServiceGrpc;
import chatapp.ChatService.ReactorChatServiceGrpc.ReactorChatServiceStub;
import chatapp.connection.ChatChannel;
import chatapp.service.ReactiveService;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static chatapp.chat.ChatMessageMapper.mapToMessage;

public class SendMessageService implements ReactiveService<ChatMessage> {
    private Consumer<ChatMessage> callback;
    private Consumer<Throwable> errorHandler;
    private Disposable responseStream;

    public SendMessageService() {
    }

    public void sendMessage(final String message) {
        final ReactorChatServiceStub stub = ReactorChatServiceGrpc.newReactorStub(ChatChannel.getChannel());

        responseStream = Mono
                .just(SendMessageRequest.newBuilder()
                        .setMessage(message)
                        .build())
                .transform(stub::sendMessage)
                .subscribe(this::processMessage, this.errorHandler);
    }

    private void processMessage(final MessageResponse m) {
        if (callback != null) {
            callback.accept(mapToMessage(m));
        }
    }
    @Override
    public void setErrorHandler(final Consumer<Throwable> t) {
        this.errorHandler = t;
    }

    @Override
    public void setSuccessHandler(final Consumer<ChatMessage> t) {
        this.callback = t;
    }

    @Override
    public void closeService() {
        responseStream.dispose();
    }
}
