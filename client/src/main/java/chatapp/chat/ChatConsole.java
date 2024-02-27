package chatapp.chat;

import chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;
import chatapp.chatroom.Room;
import chatapp.chatroom.RoomQueue;
import chatapp.connection.ChatChannel;
import chatapp.connection.ChatConnection;
import io.grpc.Channel;

import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChatConsole {
    ChatClient chatService;
    RoomQueue roomQueue;

    Room room = null;

    public ChatConsole(final Channel channel) {
        roomQueue = new RoomQueue(channel);
    }

    public void runChat(final Scanner scanner) {
        chatService = new ChatClient(ChatChannel.getChannel());
        final var handler = chatService.getHandler();
        clearChat();
        System.out.println("Enter your message: ");

        ExecutorService watchStreamExecutor = Executors.newSingleThreadExecutor();

        watchStreamExecutor.submit(() -> {
            while (!handler.hasClosed()) {
                if (handler.hasMessages()) {
                    clearChat();
                    System.out.println("Room %s====".formatted(room.roomName()));

                    handler.getMessages().forEach(m -> printMessage(new Message(m.getUsername(), m.getMessage(),
                            Instant.ofEpochSecond(m.getTimestamp().getSeconds()))));

                    handler.clearMessages();
                    System.out.println("Enter your message: ");
                }
            }
        });

        while (!handler.hasClosed()) {
            final var message = scanner.nextLine();
            System.out.println();

            if (message.equalsIgnoreCase("q")) {
                chatService.closeClient();
                break;
            }

            try {
                System.out.println();
                handler.clearMessages();
                chatService.sendMessage(message, room.roomId());
            } catch (final Exception e) {
                System.out.println(e.getMessage());
            }
        }

        watchStreamExecutor.shutdownNow();
    }

    public void joinRoom(final Scanner scanner) {
        final var roomPrompt = "Choose a room to join";
        clearChat();
        System.out.println("==== Available rooms");
        Future<Room> roomRequest = null;

        try (ExecutorService watchStreamExecutor = Executors.newSingleThreadExecutor()) {
            while (true) {
                roomQueue.processQueue((final GetChatRoomResponse response) -> System.out.println(
                        response.getRoomId() + ": " + response.getRoomName()));
                System.out.println(roomPrompt);
                final var roomId = scanner.nextLine();
                System.out.println();

                if (roomId.equalsIgnoreCase("q")) {
                    break;
                }

                try {
                    roomQueue.joinRoom(Integer.parseInt(roomId));
                    roomRequest = watchStreamExecutor.submit(() -> {
                        while (!roomQueue.isReady()) {
                            clearChat();
                            System.out.println("Entering chat room.");
                        }
                        return roomQueue.getJoinResponse();
                    });
                    watchStreamExecutor.shutdown();
                    break;
                } catch (final NumberFormatException e) {
                    System.out.println("Error parsing.");
                }

            }

            this.room = roomRequest.get();
            ChatConnection.roomId = room.roomId();
        } catch (final ExecutionException | InterruptedException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (chatService != null) {
            chatService.closeClient();
        }
    }

    private void printMessage(final Message m) {
        System.out.println();
        System.out.println("%s [%s]".formatted(m.username(), m.timestamp()));
        System.out.println("\t" + m.message());
    }

    private void clearChat() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
// TODO: Convert to Reactive API
// TODO: Upgrade to Java 21
// TODO: Refactor codebase
