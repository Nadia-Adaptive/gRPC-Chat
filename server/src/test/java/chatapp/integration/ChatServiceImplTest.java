package chatapp.integration;

import chatapp.ChatService.ChatServiceOuterClass.MessagesResponse;
import chatapp.ChatService.ChatServiceOuterClass.SendMessageRequest;
import chatapp.ChatService.ReactorChatServiceGrpc.ReactorChatServiceStub;
import chatapp.chat.ChatServiceImpl;
import chatapp.mocks.MockClientHeaderInterceptor;
import chatapp.room.ChatRoomRepository;
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

import static chatapp.ChatService.ReactorChatServiceGrpc.newReactorStub;
import static org.junit.Assert.assertEquals;

public class ChatServiceImplTest {
    private UserRepository userRepo;
    private ChatRoomRepository roomRepo;
    private ReactorChatServiceStub stub;

    @BeforeEach
    public void setup() throws Exception {
        final var grpcCleanupRule = new GrpcCleanupRule();
        final var serverName = InProcessServerBuilder.generateName();

        userRepo = new UserRepository();
        userRepo.addUser("User01");
        roomRepo = new ChatRoomRepository();

        grpcCleanupRule.register(
                InProcessServerBuilder.forName(serverName).directExecutor()
                        .addService(new ChatServiceImpl(userRepo, roomRepo))
                        .intercept(new HeaderInterceptor())
                        .build()
                        .start());

        ManagedChannel channel =
                grpcCleanupRule.register(InProcessChannelBuilder.forName(serverName).directExecutor()
                        .usePlaintext()
                        .intercept(new MockClientHeaderInterceptor())
                        .build());

        stub = newReactorStub(channel);
    }

    @Test
    public void sendingAValidMessageReturnsMessage() {
        final var response = Mono.just(SendMessageRequest.newBuilder()
                        .setMessage("Test message").build())
                .transform(stub::sendMessage)
                .block();

        assertEquals(1, response.getMessageId());
        assertEquals("User01", response.getUsername());
        assertEquals("Test message", response.getMessage());
    }

    @Test
    public void getMessagesReturnsAlMessages() {
        Mono.just(SendMessageRequest.newBuilder()
                        .setMessage("Test message").build())
                .transform(stub::sendMessage).block();

        final var response = MessagesResponse.newBuilder();
        Flux
                .just(Empty.newBuilder().build())
                .transform(stub::getMessages)
                .subscribe((r) -> response.addAllMessages(r.getMessagesList()));

        assertEquals(1, response.getMessagesCount());

    }
}
