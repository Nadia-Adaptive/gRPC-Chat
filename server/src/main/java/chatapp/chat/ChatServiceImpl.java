package chatapp.chat;

import chatapp.ChatService.ChatServiceGrpc.ChatServiceImplBase;
import chatapp.ChatService.ChatServiceOuterClass.MessageRequest;
import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;
import chatapp.room.ChatRoom;
import chatapp.room.ChatRoomRepository;
import chatapp.server.RequestHeader;
import chatapp.user.UserRepository;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ChatServiceImpl extends ChatServiceImplBase {
    final UserRepository userRepository;
    final ChatRoomRepository chatRoomRepository;
    Map<Integer, StreamObserver<MessageResponse>> streams;

    public ChatServiceImpl(final UserRepository state, final ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
        streams = new HashMap<>();
        this.userRepository = state;
    }

    @Override
    public StreamObserver<MessageRequest> sendMessage(
            final StreamObserver<MessageResponse> responseObserver) {
        System.out.println("Adding new client.");

        final var clientId = RequestHeader.CTX_CLIENT_ID.get();
        final var roomId = RequestHeader.CTX_ROOM_ID.get();

        streams.put(clientId, responseObserver);

        final var room = chatRoomRepository.findById(roomId);

        if (room == null) {
            responseObserver.onError(new StatusRuntimeException(Status.CANCELLED));
            return null;
        }

        broadcastToAll(responseObserver, room);

        return new StreamObserver<>() {
            @Override
            public void onNext(final MessageRequest request) {
                final var clientId = RequestHeader.CTX_CLIENT_ID.get();
                System.out.println("Message from client #%s received.".formatted(clientId));

                final var room = chatRoomRepository.findById(request.getRoomId());

                final var messages = room.messages();

                messages.add(mapToMessage(request.toBuilder().setUserId(clientId).build()));

                System.out.println(room.users());

                room.users().forEach(u -> {
                    broadcastToAll(streams.get(u.id()), room);
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

    private void broadcastToAll(final StreamObserver<MessageResponse> stream, final ChatRoom room) {
        try {
            room.messages().forEach(m -> {
                System.out.println("Sending message #" + m.messageId());
                stream.onNext(mapToMessageResponse(m, m.messageId() == room.messages().size()));
            });
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private MessageResponse mapToMessageResponse(final Message message, final boolean isLast) {
        final var time = message.timestamp();
        final Timestamp ts =
                Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();

        return MessageResponse.newBuilder()
                .setMessage(message.message())
                .setMessageId(message.messageId())
                .setUsername(message.username())
                .setTimestamp(ts)
                .setIsLast(isLast)
                .build();
    }

    private Message mapToMessage(final MessageRequest request) {
        final var user = userRepository.findById(request.getUserId());
        final var room = chatRoomRepository.findById(request.getRoomId());

        if (user == null) {
            throw new NullPointerException("User is null!");
        }

        // TODO: Rearrange record parameters to id, user, timestamp, message
        return new Message(user.username(), Instant.now(), request.getMessage(), room.messages().size() + 1);
    }
}
