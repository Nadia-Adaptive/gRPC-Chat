package chatapp.chatroom;

import java.util.List;
import java.util.function.Consumer;

public class ChatRoomService {
    private Consumer<Throwable> errorHandler;
    private final GetChatRoomService getService;
    private final JoinRoomService joinRoomService;

    private final CreateRoomService createRoomService;

    public ChatRoomService() {
        this.joinRoomService = new JoinRoomService();
        this.getService = new GetChatRoomService();
        createRoomService = new CreateRoomService();
    }

    public void setGetHandler(final Consumer<List<ChatRoom>> c) {
        getService.setSuccessHandler(c);
    }

    public void setJoinHandler(final Consumer<ChatRoom> c) {
        joinRoomService.setSuccessHandler(c);
    }

    public void joinRoom(final int roomId) {
        joinRoomService.joinRoom(roomId);
    }

    public void getChatRooms() {
        getService.getRooms();
    }

    public void closeServices() {
        joinRoomService.closeService();
        getService.closeService();
        createRoomService.closeService();
    }

    public void setErrorHandler(final Consumer<Throwable> errorHandler) {
        joinRoomService.setErrorHandler(errorHandler);
        getService.setErrorHandler(errorHandler);
        createRoomService.setErrorHandler(errorHandler);
    }

    public void setCreateHandler(final Consumer<ChatRoom> c) {
        createRoomService.setSuccessHandler(c);
    }

    public void createRoom(final String roomName) {
        createRoomService.createRoom(roomName);
    }
}
