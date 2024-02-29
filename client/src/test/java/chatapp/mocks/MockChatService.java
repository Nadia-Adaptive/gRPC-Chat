package chatapp.mocks;

import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;
import chatapp.ChatService.ChatServiceOuterClass.MessagesResponse;
import chatapp.ChatService.ChatServiceOuterClass.SendMessageRequest;
import chatapp.ChatService.ReactorChatServiceGrpc.ChatServiceImplBase;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.stream.Stream;

public class MockChatService extends ChatServiceImplBase {
    public MockChatService() {
    }

    @Override
    public Mono<MessageResponse> sendMessage(final Mono<SendMessageRequest> request) {
        final var timestamp = Instant.now();
        return request.handle((r, sink) -> {
            if (r.getMessage().equalsIgnoreCase("error")) {
                sink.error(new StatusRuntimeException(Status.INTERNAL.withDescription("Something went wrong")));
            } else {
                sink.next(!r.getMessage().equalsIgnoreCase("fail")
                        ? MessageResponse.newBuilder()
                        .setMessage(r.getMessage())
                        .setMessageId(0)
                        .setUsername("User")
                        .setTimestamp(
                                Timestamp.newBuilder()
                                        .setSeconds(timestamp.getEpochSecond())
                                        .setNanos(timestamp.getNano())
                                        .build())
                        .build()
                        : MessageResponse.newBuilder().setMessageId(-1).buildPartial());
            }
        });
    }

    @Override
    public Flux<MessagesResponse> getMessages(final Flux<Empty> request) {
        return Flux
                .fromStream(Stream.of(MessagesResponse.newBuilder()
                        .addMessages(MessageResponse.newBuilder().setMessage("Test01").build())
                        .addMessages(MessageResponse.newBuilder().setMessage("Test02").build())
                        .addMessages(MessageResponse.newBuilder().setMessage("Test03").build())
                        .build())
                );
    }
}
