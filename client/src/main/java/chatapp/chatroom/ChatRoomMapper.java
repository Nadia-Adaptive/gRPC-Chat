package chatapp.chatroom;

import chatapp.RoomService.ChatRoomServiceOuterClass.ChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;

import java.util.List;

public class ChatRoomMapper {

    public static List<ChatRoom> mapToChatRoomList(final GetChatRoomResponse r) {
        return r.getRoomsList().stream().map(ChatRoomMapper::mapToChatRoom).toList();
    }

    public static ChatRoom mapToChatRoom(final ChatRoomResponse r) {
        return new ChatRoom(r.getRoomId(), r.getRoomName());
    }

}
