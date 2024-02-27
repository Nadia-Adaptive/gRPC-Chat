package chatapp.server;

import chatapp.room.ChatRoomRepository;
import chatapp.room.ChatRoomServiceImpl;
import chatapp.chat.ChatServiceImpl;
import chatapp.service.ConnectionServiceImpl;
import chatapp.user.UserRepository;
import io.grpc.Grpc;
import io.grpc.Server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.grpc.InsecureServerCredentials.create;
import static io.grpc.ServerInterceptors.intercept;
import static java.lang.Integer.parseInt;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.getenv;

public class ApplicationServer {
    Server server;
    UserRepository userRepository;
    static final int PORT = parseInt(getenv("CHAT_PORT"));
    static final int TIMEOUT = 30;
    private ChatRoomRepository chatRoomRepository;

    public void start() throws IOException {
        userRepository = new UserRepository();
        chatRoomRepository = new ChatRoomRepository();

        server = Grpc.newServerBuilderForPort(PORT, create())
                .addService(new ConnectionServiceImpl(userRepository))
                .addService(
                        intercept(new ChatRoomServiceImpl(chatRoomRepository, userRepository), new HeaderInterceptor()))
                .addService(intercept(new ChatServiceImpl(userRepository, chatRoomRepository), new HeaderInterceptor()))
                .build();

        server.start();

        System.out.println("Server started");
        getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    ApplicationServer.this.stop();
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
