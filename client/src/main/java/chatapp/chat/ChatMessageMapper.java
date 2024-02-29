package chatapp.chat;

import chatapp.ChatService.ChatServiceOuterClass.MessageResponse;

import java.time.Instant;

public class ChatMessageMapper {
    public static ChatMessage mapToMessage(final MessageResponse response) {
        if (response.getMessageId() == -1) {
            return new ChatMessage(-1, null, null, Instant.now());
        }
        return new ChatMessage(response.getMessageId(), response.getUsername(), response.getMessage(),
                Instant.ofEpochSecond(response.getTimestamp().getSeconds()));
    }
}
