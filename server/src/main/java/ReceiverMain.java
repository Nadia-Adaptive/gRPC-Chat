import chatapp.server.ApplicationServer;

import java.util.logging.Logger;

public class ReceiverMain {
    static Logger log = Logger.getLogger("ReceiverMain");
    public static void main(final String[] args) throws InterruptedException {
        ApplicationServer main = new ApplicationServer();

        try {
            main.start();
            main.blockUntilShutdown();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            main.stop();
        }
    }
}

