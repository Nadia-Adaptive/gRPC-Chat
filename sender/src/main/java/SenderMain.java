import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SenderMain {
    ChatServiceGrpc.ChatServiceBlockingStub serviceStub;

    StreamObserver responseObserver;

    public SenderMain(Channel channel) {
        serviceStub = ChatServiceGrpc.newBlockingStub(channel);
    }

    void sendMessage(final String message) {
        final var request = ChatApp.SendMessageRequest.newBuilder().setMessage(message).setRoomId(0).setUserId(0).build();
        final var result = serviceStub.sendMessage(request);
        while (result.hasNext()) {
            final var response = result.next();
            System.out.println("Received message: " + response.getMessage());
            System.out.println("At time: " + Instant.ofEpochSecond(response.getTimestamp()));
            System.out.println("With message id: " + response.getMessageId());
        }
    }


    public static void main(String[] args) throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
        SenderMain senderMain = new SenderMain(channel);

        Scanner scanner = new Scanner(System.in);

        var isDone = false;

        while (!isDone) {
            try {
                System.out.println("Enter your message: ");
                final var message = scanner.nextLine();

                if (message.equalsIgnoreCase("q")) {
                    isDone = true;
                    break;
                }

                try {
                    senderMain.sendMessage(message);

                } catch (final Exception e) {
                    System.out.println(e.getMessage());

                } finally {
                    // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
                    // resources the channel should be shut down when it will no longer be used. If it may be used
                    // again leave it running.
                    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                }
            } finally {
                scanner.close();
            }
        }
        System.out.println("clOSING CLIENT");
    }
}
