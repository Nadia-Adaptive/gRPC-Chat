package chatapp.chat;

import chatapp.chatroom.ChatRoom;
import chatapp.console.ApplicationDisplay;

public class ChatView {
    final ApplicationDisplay display;

    public ChatView(final ApplicationDisplay display) {
        this.display = display;
    }

    public void runChat(final ChatRoom room) {
        final var service = new ChatMessageService();
        display.clearDisplay();

        service.setGetHandler((messages) -> {
            display.clearDisplay();
            display.print("Room %s====".formatted(room.roomName()));

            messages.forEach(this::printMessage);
            display.print("Enter your message: ");
        });

        service.setSendHandler((b) -> {
            if (b.messageId() == -1) {
                display.clearDisplay();
                display.print("Trouble sending message. Please try again.");
                display.print("Enter your message: ");
            }
        });

        service.setErrorHandler((t) -> {
            display.clearDisplay();
            display.print("Error received - " + t.getMessage());
            display.print("Previous action cancelled. Please try again.");
        });

        service.getMessages();
        while (true) {
            final var message = display.readString();

            if (message.equalsIgnoreCase("q")) {
                service.closeServices();
                break;
            }

            try {
                service.sendMessage(message);
            } catch (final Exception e) {
                display.print(e.getMessage());
            }
        }
    }

    private void printMessage(final ChatMessage m) {
        display.print("%s [%s]".formatted(m.username(), m.timestamp()));
        display.print("\t" + m.message());
    }
}
