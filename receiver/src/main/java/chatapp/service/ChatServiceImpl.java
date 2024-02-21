package chatapp.service;

import chatapp.ChatService.ChatServiceGrpc;
import chatapp.ChatService.ChatServiceOuterClass;
import chatapp.server.UserRepository;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {
    final UserRepository state;
    List<ChatServiceOuterClass.MessageResponse> messages;
    List<StreamObserver<ChatServiceOuterClass.MessageResponse>> streams;

    public ChatServiceImpl(final UserRepository state) {
        messages = new ArrayList<>();
        streams = new ArrayList<>();
        this.state = state;
    }

    @Override
    public StreamObserver<ChatServiceOuterClass.MessageRequest> sendMessage(
            final StreamObserver<ChatServiceOuterClass.MessageResponse> responseObserver) {
        System.out.println("Adding new client.");

        streams.add(responseObserver);
        return new StreamObserver<>() {
            @Override
            public void onNext(final ChatServiceOuterClass.MessageRequest request) {
                System.out.println("Message received.");

                final var user = state.findById(request.getUserId());

                final var time = Instant.now();
                final Timestamp ts =
                        Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();

                if (!messages.isEmpty()) {
                    final var message = messages.get(messages.size() - 1);
                    messages.set(message.getMessageId(), message.toBuilder().setIsLast(false).build());
                }

                messages.add(ChatServiceOuterClass.MessageResponse.newBuilder()
                        .setMessage(request.getMessage())
                        .setUsername(user.username())
                        .setTimestamp(ts)
                        .setIsLast(true)
                        .setMessageId(messages.size())
                        .build());

                streams.forEach(s -> {
                    try {
                        messages.forEach(s::onNext);
                    } catch (final Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(final Throwable t) {
                System.out.println("Received error - " + t.getMessage());
            }

            @Override
            public void onCompleted() {
            }
        };
    }
}
