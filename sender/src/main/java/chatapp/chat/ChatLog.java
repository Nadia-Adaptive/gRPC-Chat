package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass;

import java.util.List;

public class ChatLog implements io.grpc.stub.StreamObserver<ChatServiceOuterClass.MessageResponse> {

    List<ChatServiceOuterClass.MessageResponse> messages;

    public ChatLog(final List<ChatServiceOuterClass.MessageResponse> messages) {
        this.messages = messages;
    }

    public void onNext(final ChatServiceOuterClass.MessageResponse value) {
        messages.add(value);
    }

    @Override
    public void onError(final Throwable t) {
        System.out.println(t.getMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("End of chat log.");
    }
}
