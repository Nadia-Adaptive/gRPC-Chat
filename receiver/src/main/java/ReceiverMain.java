import chatapp.server.ChatServer;

public class ReceiverMain {
    public static void main(final String[] args) throws InterruptedException {
        ChatServer main = new ChatServer();

        try {
            main.start();
            main.blockUntilShutdown();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            main.stop();
        }
    }
}

