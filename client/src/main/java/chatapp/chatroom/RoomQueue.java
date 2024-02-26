package chatapp.chatroom;

import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomRequest;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomResponse;
import chatapp.connection.ChatConnection;
import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

import java.util.function.Consumer;

import static chatapp.RoomService.ChatRoomServiceGrpc.newStub;
import static chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;

public class RoomQueue {
    private final Channel channel;

    private JoinChatRoomResponse response;

    private boolean isFinishedProcessing = false;

    public RoomQueue(final Channel channel) {
        this.channel = channel;
    }

    public void processQueue(final Consumer<GetChatRoomResponse> r) {
        newStub(channel).getChatRooms(Empty.newBuilder().build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(final GetChatRoomResponse value) {
                        r.accept(value);
                    }

                    @Override
                    public void onError(final Throwable t) {
                        System.out.println(t.getMessage());
                        isFinishedProcessing = true;
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Rooms received.");
                        isFinishedProcessing = true;
                    }
                });
    }

    public void joinRoom(final int roomId) {
        ChatConnection.roomId = roomId;
        newStub(channel).joinChatRoom(JoinChatRoomRequest.newBuilder()
                        .setRoomId(roomId)
                        .setUserId(ChatConnection.clientId).build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(final JoinChatRoomResponse value) {
                        response = value;
                    }

                    @Override
                    public void onError(final Throwable t) {
                        System.out.println(t.getMessage());
                        isFinishedProcessing = true;
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Completed");
                        isFinishedProcessing = true;
                    }
                });
    }

    public boolean isReady() {
        return isFinishedProcessing;
    }

    public Room getJoinResponse() {
        if (isReady()) {
            return new Room(response.getRoomId(), response.getRoomName());
        } else {
            return null;
        }
    }
}
