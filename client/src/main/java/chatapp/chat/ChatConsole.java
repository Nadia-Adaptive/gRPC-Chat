package chatapp.chat;

import chatapp.chatroom.ChatRoom;
import chatapp.chatroom.GetChatRoomService;
import chatapp.chatroom.JoinRoomService;
import chatapp.connection.ChatConnection;

import java.util.Scanner;

public class ChatConsole {
    ChatRoom room = null;

    public ChatConsole() {
    }

    public void runChat(final Scanner scanner) {
        final var handler = new SendMessageService();
        final var getMessages = new GetMessagesService();
        clearChat();

        handler.submit((messages) -> {
            clearChat();
            System.out.println("Room %s====".formatted(room.roomName()));

            messages.forEach(this::printMessage);
            System.out.println("Enter your message: ");
        });

        getMessages.submit((messages) -> {
            clearChat();
            System.out.println("Room %s====".formatted(room.roomName()));

            messages.forEach(this::printMessage);
            System.out.println("Enter your message: ");
        });

        while (!handler.hasClosed()) {
            System.out.println("Enter your message: ");
            final var message = scanner.nextLine();
            System.out.println();

            if (message.equalsIgnoreCase("q")) {
                handler.closeService();
                break;
            }

            try {
                handler.sendMessage(message);
            } catch (final Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void joinRoom(final Scanner scanner) {
        final var joinRoomService = new JoinRoomService();
        final var getChatRoomService = new GetChatRoomService();
        final var roomPrompt = "Choose a room to join";

        getChatRoomService.submit((r) -> {
            System.out.println("==== Available rooms");
            r.forEach(this::printRoom);
        });

        joinRoomService.submit((r) -> {
            this.room = r;
            ChatConnection.roomId = r.roomId();
        });

        getChatRoomService.getRooms();
        while (!joinRoomService.hasClosed()) {
            clearChat();
            System.out.println(roomPrompt);
            final var roomId = scanner.nextLine();
            System.out.println();

            if (roomId.equalsIgnoreCase("q")) {
                break;
            }
            try {
                joinRoomService.joinRoom(Integer.parseInt(roomId));
            } catch (final NumberFormatException e) {
                System.out.println("Error parsing.");
            }
        }
    }

    private void printMessage(final Message m) {
        System.out.println();
        System.out.println("%s [%s]".formatted(m.username(), m.timestamp()));
        System.out.println("\t" + m.message());
    }

    private void printRoom(final ChatRoom r) {
        System.out.println(r.roomId() + ": " + r.roomName());
    }

    private void clearChat() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
// TODO: Convert to Reactive API
// TODO: Upgrade to Java 21
// TODO: Refactor codebase
