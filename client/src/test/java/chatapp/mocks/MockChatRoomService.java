package chatapp.mocks;

import chatapp.RoomService.ChatRoomServiceOuterClass.ChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.CreateChatRoomRequest;
import chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomRequest;
import chatapp.RoomService.ReactorChatRoomServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.INVALID_ARGUMENT;

public class MockChatRoomService extends ReactorChatRoomServiceGrpc.ChatRoomServiceImplBase {
    @Override
    public Mono<ChatRoomResponse> joinChatRoom(final Mono<JoinChatRoomRequest> request) {
        return request.handle((r, sink) -> {
            if (r.getRoomId() == -1) {
                sink.error(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Chatroom doesn't exist")));
            } else if (r.getRoomId() == -2) {
                sink.error(new StatusRuntimeException(Status.INTERNAL.withDescription("Something went wrong")));
            } else {
                sink.next(ChatRoomResponse.newBuilder()
                        .setRoomId(0)
                        .setRoomName("Test Room")
                        .build());
            }
        });
    }

    @Override
    public Flux<GetChatRoomResponse> getChatRooms(final Flux<Empty> request) {
        return Flux
                .fromStream(Stream.of(GetChatRoomResponse.newBuilder()
                        .addRooms(ChatRoomResponse.newBuilder().setRoomId(1).setRoomName("Test01").build())
                        .addRooms(ChatRoomResponse.newBuilder().setRoomId(2).setRoomName("Test02").build())
                        .addRooms(ChatRoomResponse.newBuilder().setRoomId(3).setRoomName("Test03").build())
                        .build())
                );
    }

    @Override
    public Mono<ChatRoomResponse> createChatRoom(final Mono<CreateChatRoomRequest> request) {
        return request.handle((m, sink) -> {
            if (m.getRoomName().isEmpty()) {
                sink.error(INVALID_ARGUMENT.withDescription("No room name specified").asRuntimeException());
            } else if (m.getRoomName().equals("error")) {
                sink.error(INTERNAL.withDescription("Something went wrong").asRuntimeException());
            } else {
                sink.next(ChatRoomResponse.newBuilder().setRoomId(1).setRoomName(m.getRoomName()).build());
            }
        });
    }
}
