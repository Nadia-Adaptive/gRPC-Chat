package chatapp.chatroom;

import chatapp.RoomService.ReactorChatRoomServiceGrpc;
import chatapp.connection.ChatChannel;
import com.google.protobuf.Empty;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;

import static chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;

public class GetChatRoomService {
    private Consumer<List<ChatRoom>> callback;

    public GetChatRoomService() {
    }

    public void submit(final Consumer<List<ChatRoom>> r) {
        callback = r;
    }

    public void processResponse(final GetChatRoomResponse r) {
        callback.accept(ChatRoomMapper.mapToChatRoomList(r));
    }

    public void getRooms() {
        final var stub = ReactorChatRoomServiceGrpc.newReactorStub(ChatChannel.getChannel());

        final var response = Flux.just(Empty.newBuilder().build()).transform(stub::getChatRooms);

        response.subscribe(this::processResponse);
    }
}
