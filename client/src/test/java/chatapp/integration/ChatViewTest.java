package chatapp.integration;

import chatapp.chat.ChatView;
import chatapp.chatroom.ChatRoom;
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

public class ChatViewTest {
    private MockDisplay display;

    protected void setupDisplay(final String scanner) {
        display = new MockDisplay(scanner + "\nq");
        ChatView chatView = new ChatView(display);
        chatView.runChat(new ChatRoom(0, "test"));
    }

    @BeforeEach
    public void setup() throws Exception {
        final var grpcCleanupRule = new GrpcCleanupRule();
        final var serverName = InProcessServerBuilder.generateName();
        grpcCleanupRule.register(
                InProcessServerBuilder.forName(serverName).directExecutor()
                        .addService(new MockChatRoomService())
                        .addService(new MockChatService())
                        .build().start());

        ManagedChannel channel =
                grpcCleanupRule.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

        ChatChannel.setChannel(channel);
    }

    @Test
    public void sendingAValidMessageReturnsAllMessages() {
        setupDisplay("test");

        assertEquals(
                "Room test==== [1970-01-01T00:00:00Z]\tTest01 [1970-01-01T00:00:00Z]\tTest02 [1970-01-01T00:00:00Z]"
                        + "\tTest03Enter your message: ",
                display.getOutput());
    }

    @Test
    public void sendingAInvalidMessageDisplaysARetryPrompt() {
        setupDisplay("fail");
        assertEquals("Trouble sending message. Please try again.Enter your message: ", display.getOutput());
    }

    @Test
    public void onErrorDisplaysAMessage() {
        setupDisplay("error");
        assertEquals("Error received - INTERNAL: Something went wrongPrevious action cancelled. Please try again.",
                display.getOutput());
    }
}
