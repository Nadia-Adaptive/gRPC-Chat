package chatapp.chatroom;

import chatapp.RoomService.ChatRoomServiceOuterClass.ChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomRequest;
import chatapp.RoomService.ReactorChatRoomServiceGrpc;
import chatapp.connection.ChatChannel;
import chatapp.connection.ChatConnection;
import io.grpc.Channel;

import java.util.function.Consumer;

import static chatapp.chatroom.ChatRoomMapper.mapToChatRoom;

public class JoinRoomService {
    private final Channel channel;
    private Consumer<ChatRoom> callback;
    private boolean hasClosed = false;

    public JoinRoomService() {
        this.channel = ChatChannel.getChannel();
    }

    public void processResponse(final ChatRoomResponse r) {
        callback.accept(mapToChatRoom(r));
        hasClosed = true;
    }

    public void joinRoom(final int roomId) {
        ChatConnection.roomId = roomId;
        final var stub = ReactorChatRoomServiceGrpc.newReactorStub(channel);

        final var request = stub.joinChatRoom(JoinChatRoomRequest.newBuilder().build());

        processResponse(request.block());
    }

    public void submit(final Consumer<ChatRoom> r) {
        callback = r;
    }

    public boolean hasClosed() {
        return hasClosed;
    }
}
