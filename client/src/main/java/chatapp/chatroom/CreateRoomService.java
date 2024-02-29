package chatapp.chatroom;

import chatapp.RoomService.ChatRoomServiceOuterClass.ChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.CreateChatRoomRequest;
import chatapp.RoomService.ReactorChatRoomServiceGrpc;
import chatapp.connection.ChatChannel;
import chatapp.service.ReactiveService;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static chatapp.chatroom.ChatRoomMapper.mapToChatRoom;

public class CreateRoomService implements ReactiveService<ChatRoom> {
    private final Channel channel;
    private Consumer<ChatRoom> callback;
    private Consumer<Throwable> errorHandler;
    private Disposable response;

    public CreateRoomService() {
        this.channel = ChatChannel.getChannel();
    }

    public void processResponse(final ChatRoomResponse r) {
        if (callback != null) {
            callback.accept(mapToChatRoom(r));
        }
    }

    public void createRoom(final String roomName) {
        final var stub = ReactorChatRoomServiceGrpc.newReactorStub(channel);

        try {
            processResponse(Mono
                    .just(CreateChatRoomRequest.newBuilder()
                            .setRoomName(roomName)
                            .build())
                    .transform(stub::createChatRoom)
                    .block());
        } catch (final StatusRuntimeException e) {
            errorHandler.accept(e);
        }
    }

    @Override
    public void setErrorHandler(final Consumer<Throwable> t) {
        this.errorHandler = t;
    }

    @Override
    public void setSuccessHandler(final Consumer<ChatRoom> c) {
        callback = c;
    }

    @Override
    public void closeService() {
        response.dispose();
    }
}
