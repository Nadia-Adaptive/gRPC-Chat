package chatapp.integration;

import chatapp.chatroom.ChatRoomView;
import chatapp.connection.ChatChannel;
import chatapp.mocks.MockChatRoomService;
import chatapp.mocks.MockChatService;
import chatapp.mocks.MockDisplay;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatRoomViewTest {
    private MockDisplay display;

    protected ChatRoomView setupDisplay(final String scanner) {
        display = new MockDisplay(scanner + "\nq");
        return new ChatRoomView(display);
    }

    protected void setupJoinDisplay(final String scanner) {
        setupDisplay(scanner).joinRoom();

    }

    protected void setupCreateDisplay(final String scanner) {
        setupDisplay(scanner).createRoom();
    }

    protected void setupMenuDisplay(final String scanner) {
        setupDisplay(scanner).run();
    }

    @BeforeEach
    public void setup() throws Exception {
        final var grpcCleanupRule = new GrpcCleanupRule();
        final var serverName = InProcessServerBuilder.generateName();

        grpcCleanupRule.register(
                InProcessServerBuilder.forName(serverName).directExecutor()
                        .addService(new MockChatRoomService())
                        .addService(new MockChatService())
                        .build()
                        .start());

        ManagedChannel channel =
                grpcCleanupRule.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

        ChatChannel.setChannel(channel);
    }

    @Test
    public void chatRoomViewDisplaysMenuCorrectly() {
        setupMenuDisplay("2");

        assertEquals("Choose an option: 0: Join a room1: Create a room2: Quit program", display.getOutput());
    }

    @Test
    public void sendingAValidRoomIdDisplaysAMessage() {
        setupJoinDisplay("1");

        assertEquals("Joining chatroom Test Room", display.getOutput());
    }

    @Test
    public void chatRoomMenuDisplaysAvailableChatRoom() {
        setupJoinDisplay("q");
        assertEquals("==== Available Rooms ===="
                        + "1: Test01"
                        + "2: Test02"
                        + "3: Test03"
                        + "Choose a room to join: ",
                display.getOutput());
    }

    @Test
    public void sendingAInvalidRoomIdDisplaysARetryPrompt() {
        setupJoinDisplay("-1");
        assertEquals("Error received - NOT_FOUND: Chatroom doesn't existPrevious action cancelled. Please try again.",
                display.getOutput());
    }

    @Test
    public void joinRoomErrorDisplaysAMessage() {
        setupJoinDisplay("-2");
        assertEquals("Error received - INTERNAL: Something went wrongPrevious action cancelled. Please try again.",
                display.getOutput());
    }

    @Test
    public void validCreateRequestReturnsChatRoom() {
        setupCreateDisplay("New Room");
        assertEquals("Created new room: New Room",
                display.getOutput());
    }

    @Test
    public void invalidCreateRequestDisplaysError() {
        setupCreateDisplay("");
        assertEquals(
                "Error received - INVALID_ARGUMENT: No room name specifiedPrevious action cancelled. Please try again.",
                display.getOutput());
    }

    @Test
    public void createRequestInternalErrorDisplaysError() {
        setupCreateDisplay("error\nq");
        assertEquals("Error received - INTERNAL: Something went wrongPrevious action cancelled. Please try again.",
                display.getOutput());
    }
}
