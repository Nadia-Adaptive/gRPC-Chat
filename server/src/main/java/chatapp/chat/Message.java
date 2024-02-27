package chatapp.chat;

import java.time.Instant;

public record Message(int roomId, int messageId, String username, Instant timestamp, String message) {
}
