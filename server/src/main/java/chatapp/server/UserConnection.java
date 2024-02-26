package chatapp.server;

import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;
import io.grpc.stub.StreamObserver;

public record UserConnection(int userId, StreamObserver<MessageResponse> connection) {
}
