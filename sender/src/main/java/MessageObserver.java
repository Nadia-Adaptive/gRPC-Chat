import chatapp.ChatService.ChatServiceOuterClass;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MessageObserver implements io.grpc.stub.StreamObserver<ChatServiceOuterClass.MessageResponse> {
    public void onNext(ChatServiceOuterClass.MessageResponse value) {
        printMessage(value);
    }

    @Override
    public void onError(Throwable t) {
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
