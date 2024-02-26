package chatapp.room;

import chatapp.chat.Message;
import chatapp.user.User;

import java.util.List;

public record ChatRoom(int id, String roomName, List<User> users, List<Message> messages) {
}
