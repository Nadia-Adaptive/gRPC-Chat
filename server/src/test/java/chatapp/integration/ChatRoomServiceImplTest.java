package chatapp.integration;

import chatapp.RoomService.ChatRoomServiceOuterClass.CreateChatRoomRequest;
import chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomRequest;
import chatapp.RoomService.ReactorChatRoomServiceGrpc.ReactorChatRoomServiceStub;
import chatapp.mocks.MockClientHeaderInterceptor;
import chatapp.room.ChatRoomRepository;
import chatapp.room.ChatRoomServiceImpl;
import chatapp.server.HeaderInterceptor;
import chatapp.user.UserRepository;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static chatapp.RoomService.ReactorChatRoomServiceGrpc.newReactorStub;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChatRoomServiceImplTest {
    private ReactorChatRoomServiceStub stub;
    private UserRepository userRepo;
    private ChatRoomRepository roomRepo;

    @BeforeEach
    public void setup() throws Exception {
        final var grpcCleanupRule = new GrpcCleanupRule();
        final var serverName = InProcessServerBuilder.generateName();

        userRepo = new UserRepository();
        userRepo.addUser("User01");
        roomRepo = new ChatRoomRepository();

        grpcCleanupRule.register(
                InProcessServerBuilder.forName(serverName).directExecutor()
                        .addService(new ChatRoomServiceImpl(roomRepo, userRepo))
                        .intercept(new HeaderInterceptor())
                        .build()
                        .start());

        ManagedChannel channel =
                grpcCleanupRule.register(InProcessChannelBuilder.forName(serverName).directExecutor()
                        .intercept(new MockClientHeaderInterceptor())
                        .build());
        stub = newReactorStub(channel);
    }

    @Test
    public void requestingAValidChatroomReturnsChatRoom() {
        final var response = Mono
                .just(JoinChatRoomRequest.newBuilder().setRoomId(0).build())
                .transform(stub::joinChatRoom)
                .block();

        assertTrue(response.getRoomId() == 0);
        assertEquals("Global", response.getRoomName());
    }

    @Test
    public void getChatRoomsReturnsAlRooms() {
        roomRepo.add("New room 2", userRepo.findById(1));
        final var response = GetChatRoomResponse.newBuilder();

        Flux
                .just(Empty.newBuilder().build())
                .transform(stub::getChatRooms)
                .take(1).subscribe((r) -> response.addAllRooms(r.getRoomsList()));
        assertEquals(2, response.getRoomsCount());
    }

    @Test
    public void creatingANewChatroomReturnsTheNewRoom() {
        final var response = Mono
                .just(CreateChatRoomRequest.newBuilder().setRoomName("New room").build())
                .transform(stub::createChatRoom)
                .block();

        final var room = roomRepo.findById(response.getRoomId());

        assertEquals(2, room.id());
        assertEquals("New room", room.roomName());
        assertEquals(userRepo.findById(1), room.users().get(0));
        assertTrue(room.messages().isEmpty());
    }
}
