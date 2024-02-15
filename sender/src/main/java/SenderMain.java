import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SenderMain {


    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        ChatClient client = new ChatClient(channel);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println();
                System.out.print("Enter your message: ");
                final var message = scanner.nextLine();

                if (message.equalsIgnoreCase("q")) {
                    break;
                }

                try {
                    client.sendMessage(message);

                } catch (final Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Closing Client");
        }
    }
}
