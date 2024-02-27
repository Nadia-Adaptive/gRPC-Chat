package chatapp.room;

import chatapp.RoomService.ChatRoomServiceOuterClass.ChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomRequest;
import chatapp.RoomService.ReactorChatRoomServiceGrpc.ChatRoomServiceImplBase;
import chatapp.server.RequestHeader;
import chatapp.user.UserRepository;
import com.google.protobuf.Empty;
import io.grpc.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public class ChatRoomServiceImpl extends ChatRoomServiceImplBase {
    final UserRepository userRepository;
    final ChatRoomRepository roomRepository;

    public ChatRoomServiceImpl(final ChatRoomRepository chatRoomRepository, final UserRepository userRepository) {
        roomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Flux<GetChatRoomResponse> getChatRooms(final Flux<Empty> request) {
        final var rooms = roomRepository.findAll();
        System.out.println("Sending all " + roomRepository.chatRooms.size() + " chat rooms");

        return request.map((r) -> mapToGetChatRoomResponse(rooms.values()));
    }

    private GetChatRoomResponse mapToGetChatRoomResponse(final Collection<ChatRoom> rooms) {
        return GetChatRoomResponse.newBuilder()
                .addAllRooms(rooms.stream().map(this::mapToChatRoomResponse).toList())
                .build();
    }

    @Override
    public Mono<ChatRoomResponse> joinChatRoom(final Mono<JoinChatRoomRequest> request) {
        final var clientId = RequestHeader.CTX_CLIENT_ID.get();
        final var roomId = RequestHeader.CTX_ROOM_ID.get();
        try {
            final var room = roomRepository.findById(roomId);
            final var user = userRepository.findById(clientId);
            System.out.println("HI");

            if (room == null) {
                System.out.println("Requested room does not exist");
                return Mono.error(
                        Status.INVALID_ARGUMENT.withDescription("Room does not exist").asRuntimeException());
            }

            if (user == null) {
                System.out.println("Requested user does not exist");
                return Mono.error(
                        Status.INVALID_ARGUMENT.withDescription("User does not exist").asRuntimeException());
            }
            System.out.println("Room and client are valid.");
            room.users().add(user);
            return request.map((r) -> mapToChatRoomResponse(room));
        } catch (final NullPointerException e) {
            return Mono.error(
                    Status.INVALID_ARGUMENT.withDescription("Invalid clientId or roomId").asRuntimeException());
        }
    }

    private ChatRoomResponse mapToChatRoomResponse(final ChatRoom chatRoom) {
        return ChatRoomResponse.newBuilder()
                .setRoomId(chatRoom.id())
                .setRoomName(chatRoom.roomName())
                .build();
    }
}
