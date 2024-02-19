package chatapp.service;

import chatapp.ChatService.ChatServiceGrpc;

import chatapp.ChatService.ChatServiceOuterClass;
import chatapp.server.UserRepository;
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

                messages.add(ChatServiceOuterClass.MessageResponse.newBuilder()
                        .setMessage(request.getMessage())
                        .setMessageId(messages.size())
                        .setTimestamp(Instant.now().getEpochSecond())
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
