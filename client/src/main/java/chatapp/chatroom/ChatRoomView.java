package chatapp.chatroom;

import chatapp.connection.ChatConnection;
import chatapp.console.ApplicationDisplay;

public class ChatRoomView {
    final ApplicationDisplay display;
    private ChatRoom room;
    ChatRoomService service;

    public ChatRoomView(final ApplicationDisplay display) {
        this.display = display;
        service = new ChatRoomService();


        service.setErrorHandler((t) -> {
            display.clearDisplay();
            display.print("Error received - " + t.getMessage());
            display.print("Previous action cancelled. Please try again.");
        });
    }

    public void run() {
        boolean canQuit = false;
        while (!canQuit) {
            display.print("Choose an option: ");
            display.print("0: Join a room");
            display.print("1: Create a room");
            display.print("2: Quit program");

            final var choice = display.readString();

            switch (choice) {
                case "0":
                    joinRoom();
                    break;
                case "1":
                    createRoom();
                    break;
                case "2":
                    canQuit = true;
                    break;
                default:
                    display.print("Invalid option. Please try again.");
                    break;
            }
        }
    }

    public void joinRoom() {
        final var roomPrompt = "Choose a room to join: ";
        display.clearDisplay();

        service.setGetHandler((r) -> {
            display.clearDisplay();
            display.print("==== Available Rooms ====");
            r.forEach(this::printRoom);
            display.print(roomPrompt);
        });

        service.setJoinHandler((r) -> {
            room = r;
            ChatConnection.roomId = r.roomId();

            display.clearDisplay();
            display.print("Joining chatroom " + r.roomName());
        });

        service.getChatRooms();
        while (room == null) {
            try {
                final var roomId = display.readString();

                if (roomId.equalsIgnoreCase("q")) {
                    break;
                }
                service.joinRoom(Integer.parseInt(roomId));
            } catch (final NumberFormatException e) {
                display.print("Error parsing.");
            }
        }
        this.room = room;
    }

    public void createRoom() {
        display.clearDisplay();


        service.setCreateHandler((r) -> {
            display.clearDisplay();
            display.print("Created new room: " + r.roomName());
            room = r;
        });

        while (room == null) {
            final var roomName = display.readString();

            if (roomName.equalsIgnoreCase("q")) {
                break;
            }
            service.createRoom(roomName);
        }
        room = null;
    }

    private void printRoom(final ChatRoom r) {
        display.print(r.roomId() + ": " + r.roomName());
    }

    public ChatRoom getChatRoom() {
        return room;
    }
}
