package chatapp.chat;

import java.time.Instant;

public record Message(String username, Instant timestamp, String message, int messageId) {
}
