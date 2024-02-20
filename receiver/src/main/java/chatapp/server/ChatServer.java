package chatapp.server;

import chatapp.service.ChatServiceImpl;
import chatapp.service.ConnectionServiceImpl;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ChatServer {
    Server server;
    UserRepository state;
    static final int PORT = Integer.parseInt(System.getenv("CHAT_PORT"));
    static final int TIMEOUT = 30;

    public void start() throws IOException {
        state = new UserRepository();
        server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
                .addService(new ConnectionServiceImpl(state))
                .addService(new ChatServiceImpl(state))
                .build();
        server.start();
        System.out.println("Server started");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                   ChatServer.this.stop();
                } catch (final InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void stop() throws InterruptedException {
        server.shutdown().awaitTermination(TIMEOUT, TimeUnit.SECONDS);
    }
}
