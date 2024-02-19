import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MessageObserver implements io.grpc.stub.StreamObserver {
    public void onNext(Object value) {
        final var response = (ChatApp.SendMessageResponse) value;
        System.out.println();
        System.out.println("%s [%s]".formatted(Instant.ofEpochSecond(response.getTimestamp()), response.getMessageId()));
        System.out.println("\t" + response.getMessage());
    }

    @Override
    public void onError(Throwable t) {
        System.out.println(t.getMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("End of chat log.");
    }
}
