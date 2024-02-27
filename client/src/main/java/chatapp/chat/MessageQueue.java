package chatapp.chat;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageQueue<T extends Message> {
    private List<T> messages;

    public MessageQueue() {
        messages = new ArrayList<>();
    }

    public void clearQueue() {
        messages.removeAll(messages);
    }

    public List<T> getMessages() {
        return messages;
    }


    private T getLastMessage() {
        return messages.get(messages.size() - 1);
    }
}
