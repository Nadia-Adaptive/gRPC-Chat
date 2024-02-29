package chatapp.room;

import chatapp.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomRepository {
    Map<Integer, ChatRoom> chatRooms;

    public ChatRoomRepository() {
        chatRooms = new HashMap<>();

        chatRooms.put(0, new ChatRoom(0, "Global", new ArrayList<>(), new ArrayList<>()));
    }

    public ChatRoom add(final String roomName, final User owner) {
        final var roomId = chatRooms.size() + 1;
        final var room = new ChatRoom(roomId, roomName, new ArrayList<>(), new ArrayList<>());
        chatRooms.put(roomId, room);
        room.users().add(owner);
        return room;
    }

    public ChatRoom findById(final int roomId) {
        return chatRooms.get(roomId);
    }

    public Map<Integer, ChatRoom> findAll() {
        return chatRooms;
    }
}

