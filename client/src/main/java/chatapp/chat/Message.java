package chatapp.chat;

import java.time.Instant;

public record Message(String username, String message, Instant timestamp) {
}
