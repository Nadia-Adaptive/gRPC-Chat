import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReceiverMain {
    Server server;
    int port = 50051;

    public void start() throws IOException {
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()).addService(new ChatService()).build();
        server.start();
        System.out.println("Server started");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    ReceiverMain.this.stop();
                } catch (final InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        ReceiverMain main = new ReceiverMain();

        try {
            main.start();
            main.blockUntilShutdown();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if(server!=null)
            server.awaitTermination();
    }
}

