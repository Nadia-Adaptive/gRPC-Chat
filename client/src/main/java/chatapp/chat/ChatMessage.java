package chatapp.chat;

import java.time.Instant;

public record ChatMessage(int messageId, String username, String message, Instant timestamp) {
}
