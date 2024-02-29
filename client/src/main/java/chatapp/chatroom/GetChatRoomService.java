package chatapp.chatroom;

import chatapp.RoomService.ReactorChatRoomServiceGrpc;
import chatapp.connection.ChatChannel;
import chatapp.service.ReactiveService;
import com.google.protobuf.Empty;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;

import static chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;

public class GetChatRoomService implements ReactiveService<List<ChatRoom>> {
    private Consumer<List<ChatRoom>> callback;
    private Disposable response;
    private Consumer<Throwable> errorHandler;

    public GetChatRoomService() {
    }

    public void processResponse(final GetChatRoomResponse r) {
        callback.accept(ChatRoomMapper.mapToChatRoomList(r));
    }

    public void getRooms() {
        final var stub = ReactorChatRoomServiceGrpc.newReactorStub(ChatChannel.getChannel());

        response = Flux
                .just(Empty.newBuilder().build())
                .transform(stub::getChatRooms)
                .subscribe(this::processResponse, this.errorHandler);
    }

    @Override
    public void setErrorHandler(final Consumer t) {
        this.errorHandler = t;
    }

    @Override
    public void setSuccessHandler(final Consumer<List<ChatRoom>> r) {
        callback = r;
    }

    @Override
    public void closeService() {
            response.dispose();
    }
}
