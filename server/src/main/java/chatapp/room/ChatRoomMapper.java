package chatapp.room;

import chatapp.RoomService.ChatRoomServiceOuterClass.ChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.GetChatRoomResponse;

import java.util.Collection;

public class ChatRoomMapper {

    public static GetChatRoomResponse mapToGetChatRoomResponse(final Collection<ChatRoom> rooms) {
        return GetChatRoomResponse.newBuilder()
                .addAllRooms(rooms.stream().map(ChatRoomMapper::mapToChatRoomResponse).toList())
                .build();
    }
    public static ChatRoomResponse mapToChatRoomResponse(final ChatRoom chatRoom) {
        return ChatRoomResponse.newBuilder()
                .setRoomId(chatRoom.id())
                .setRoomName(chatRoom.roomName())
                .build();
    }
}
