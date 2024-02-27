package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass.MessageRequest;
import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;
import chatapp.ChatService.ChatServiceOuterClass.MessagesResponse;
import chatapp.ChatService.ReactorChatServiceGrpc.ChatServiceImplBase;
import chatapp.room.ChatRoomRepository;
import chatapp.server.RequestHeader;
import chatapp.user.UserRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.List;

public class ChatServiceImpl extends ChatServiceImplBase {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final Sinks.Many<MessagesResponse> messageSink = Sinks.many().multicast().onBackpressureBuffer();

    public ChatServiceImpl(final UserRepository state, final ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = state;
    }


    @Override
    public Mono<Empty> sendMessage(final Mono<MessageRequest> request) {
        System.out.println("Adding new client.");

        final var clientId = RequestHeader.CTX_CLIENT_ID.get();
        final var roomId = RequestHeader.CTX_ROOM_ID.get();

        final var room = chatRoomRepository.findById(roomId);

        if (room == null) {
            return Mono.error(new StatusRuntimeException(Status.CANCELLED));
        }

        System.out.println("Sending updated messages");

        return request.map((m) -> {
            room.messages().add(mapToMessage(m.toBuilder().setUserId(clientId).build()));
            final var messages = mapToMessagesResponse(room.messages());

            messageSink.tryEmitNext(messages);

            return Empty.newBuilder().build();
        });
    }

    @Override
    public Flux<MessagesResponse> getMessages(final Flux<Empty> request) {
        System.out.println("Sending all messages");

        final var clientId = RequestHeader.CTX_CLIENT_ID.get();
        final var roomId = RequestHeader.CTX_ROOM_ID.get();

        final var room = chatRoomRepository.findById(roomId);

        if (room == null) {
            return Flux.error(new StatusRuntimeException(Status.CANCELLED));
        }

        return messageSink.asFlux(); //TODO: Map to correct users + rooms
    }

    private MessageResponse mapToMessageResponse(final Message message) {
        System.out.println(message.message());
        final boolean isLast =
                message.messageId() != chatRoomRepository.findById(message.roomId()).messages().size() - 1;
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

    private MessagesResponse mapToMessagesResponse(final List<Message> messages) {
        return MessagesResponse.newBuilder().addAllMessages(messages.stream().map(this::mapToMessageResponse).toList())
                .build();
    }

    private Message mapToMessage(final MessageRequest request) {
        final var user = userRepository.findById(request.getUserId());
        final var room = chatRoomRepository.findById(request.getRoomId());

        if (user == null) {
            throw new NullPointerException("User is null!");
        }

        return new Message(room.id(), room.messages().size() + 1, user.username(), Instant.now(), request.getMessage());
    }
}
