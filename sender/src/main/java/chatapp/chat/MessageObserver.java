package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass;

import java.time.Instant;

public class MessageObserver implements io.grpc.stub.StreamObserver<ChatServiceOuterClass.MessageResponse> {
    public void onNext(final ChatServiceOuterClass.MessageResponse value) {
        printMessage(value);
    }

    @Override
    public void onError(final Throwable t) {
        System.out.println(t.getMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("End of chat log.");
    }

    void printMessage(final ChatServiceOuterClass.MessageResponse m) {
        System.out.println();
        System.out.printf("%s [%s]%n", Instant.ofEpochSecond(m.getTimestamp()), m.getMessageId());
        System.out.println("\t" + m.getMessage());
    }
}
