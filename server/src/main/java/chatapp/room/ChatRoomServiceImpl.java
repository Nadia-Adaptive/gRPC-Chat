package chatapp.room;

import chatapp.RoomService.ChatRoomServiceOuterClass.ChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.CreateChatRoomRequest;
import chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomRequest;
import chatapp.RoomService.ReactorChatRoomServiceGrpc.ChatRoomServiceImplBase;
import chatapp.server.RequestHeader;
import chatapp.user.UserRepository;
import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static chatapp.room.ChatRoomMapper.mapToChatRoomResponse;
import static chatapp.room.ChatRoomMapper.mapToGetChatRoomResponse;
import static chatapp.server.RequestHeader.CTX_CLIENT_ID;
import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;

public class ChatRoomServiceImpl extends ChatRoomServiceImplBase {
    final UserRepository userRepository;
    final ChatRoomRepository chatRoomRepository;

    public ChatRoomServiceImpl(final ChatRoomRepository chatRoomRepository, final UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Flux<GetChatRoomResponse> getChatRooms(final Flux<Empty> request) {
        final var rooms = chatRoomRepository.findAll();
        System.out.println("Sending all " + chatRoomRepository.chatRooms.size() + " chat rooms");

        return request.map((r) -> mapToGetChatRoomResponse(rooms.values()));
    }

    @Override
    public Mono<ChatRoomResponse> joinChatRoom(final Mono<JoinChatRoomRequest> request) {
        final var clientId = CTX_CLIENT_ID.get();
        final var roomId = RequestHeader.CTX_ROOM_ID.get();
        try {
            final var room = chatRoomRepository.findById(roomId);
            final var user = userRepository.findById(clientId);

            if (room == null) {
                System.out.println("Requested room does not exist");
                return Mono.error(
                        INVALID_ARGUMENT.withDescription("Room does not exist").asRuntimeException());
            }

            if (user == null) {
                System.out.println("Requested user does not exist");
                return Mono.error(
                        INVALID_ARGUMENT.withDescription("User does not exist").asRuntimeException());
            }
            System.out.println("Room and client are valid.");
            room.users().add(user);
            return request.map((r) -> mapToChatRoomResponse(room));
        } catch (final NullPointerException e) {
            return Mono.error(
                    INVALID_ARGUMENT.withDescription("Invalid clientId or roomId").asRuntimeException());
        }
    }

    @Override
    public Mono<ChatRoomResponse> createChatRoom(final Mono<CreateChatRoomRequest> request) {
        final var clientId = CTX_CLIENT_ID.get();

        final var user = userRepository.findById(clientId);

        if (user == null) {
            return Mono.error(new StatusRuntimeException(NOT_FOUND));
        }

        System.out.println("Sending updated messages");

        return request.handle((m, sink) -> {
            if (!m.hasRoomName()) {
                sink.error(INVALID_ARGUMENT.withDescription("No room name specified.").asRuntimeException());
            } else {
                final var chatRoom = chatRoomRepository.add(m.getRoomName(), user);
                sink.next(mapToChatRoomResponse(chatRoom));
            }
        });
    }
}
