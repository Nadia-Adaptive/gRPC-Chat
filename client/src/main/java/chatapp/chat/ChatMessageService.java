package chatapp.chat;

import java.util.List;
import java.util.function.Consumer;

public class ChatMessageService {
    private Consumer<Throwable> errorHandler;
    private final SendMessageService sendService;
    private final GetMessagesService getService;

    public ChatMessageService() {
        this.sendService = new SendMessageService();
        this.getService = new GetMessagesService();
    }

    public void setGetHandler(final Consumer<List<ChatMessage>> action) {
        this.getService.setSuccessHandler(action);
    }

    public void setSendHandler(final Consumer<ChatMessage> action) {
        this.sendService.setSuccessHandler(action);
    }

    public void sendMessage(final String message) {
        sendService.sendMessage(message);
    }

    public void getMessages() {
        getService.get();
    }

    public void closeServices() {
        sendService.closeService();
        getService.closeService();
    }

    public void setErrorHandler(final Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        sendService.setErrorHandler(this.errorHandler);
        getService.setErrorHandler(this.errorHandler);
    }
}
