package chatapp.room;

import chatapp.RoomService.ChatRoomServiceGrpc.ChatRoomServiceImplBase;
import chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomRequest;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomResponse;
import chatapp.server.RequestHeader;
import chatapp.user.UserRepository;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class ChatRoomServiceImpl extends ChatRoomServiceImplBase {
    ChatRoomRepository roomRepository;
    UserRepository userRepository;

    public ChatRoomServiceImpl(final ChatRoomRepository chatRoomRepository, final UserRepository userRepository) {
        roomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void getChatRooms(final Empty request,
                             final StreamObserver<GetChatRoomResponse> responseObserver) {
        roomRepository.findAll().values().forEach(r -> responseObserver.onNext(mapToChatRoomResponse(r)));
    }

    @Override
    public void joinChatRoom(final JoinChatRoomRequest request,
                             final StreamObserver<JoinChatRoomResponse> responseObserver) {
        final var clientId = RequestHeader.CTX_CLIENT_ID.get();
        final var roomId = RequestHeader.CTX_ROOM_ID.get();

        System.out.println(clientId);
        System.out.println(roomId);
        if (clientId == null) {
            System.out.println("clientId is invalid");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("clientId is invalid").asRuntimeException());
            return;
        }

        if (roomId == null) {
            System.out.println("roomId is invalid");
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("roomId is invalid").asRuntimeException());
            return;
        }

        final var room = roomRepository.findById(roomId);
        final var user = userRepository.findById(clientId);
        System.out.println("HI");

        if (room == null) {
            System.out.println(room);
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Room does not exist").asRuntimeException());
        } else if (user == null) {
            System.out.println(user);
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("User does not exist").asRuntimeException());
        } else {
            System.out.println("Room and client are valid.");
            room.users().add(user);
            responseObserver.onNext(mapToJoinRoomResponse(room));
            responseObserver.onCompleted();
        }
    }

    private GetChatRoomResponse mapToChatRoomResponse(final ChatRoom chatRoom) {
        return GetChatRoomResponse.newBuilder()
                .setRoomId(chatRoom.id())
                .setRoomName(chatRoom.roomName())
                .build();
    }

    private JoinChatRoomResponse mapToJoinRoomResponse(final ChatRoom chatRoom) {
        return JoinChatRoomResponse.newBuilder()
                .setRoomId(chatRoom.id())
                .setRoomName(chatRoom.roomName())
                .build();
    }
}
