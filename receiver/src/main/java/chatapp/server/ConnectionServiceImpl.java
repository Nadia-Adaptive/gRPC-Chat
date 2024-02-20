package chatapp.server;

import chatapp.ConnectionService.ConnectionServiceGrpc;
import chatapp.ConnectionService.ConnectionServiceOuterClass;
import chatapp.user.User;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class ConnectionServiceImpl extends ConnectionServiceGrpc.ConnectionServiceImplBase {
    final ServerState server;

    public ConnectionServiceImpl(final ServerState server) {
        this.server = server;
    }

    @Override
    public void connect(final ConnectionServiceOuterClass.ConnectionRequest request, final StreamObserver<ConnectionServiceOuterClass.ConnectionResponse> responseObserver) {
        try {
            // TODO: authenticate user
            System.out.println("User connection request received.");
            final var clientId = server.addUser(request.getUsername()).getId();
            final var response = ConnectionServiceOuterClass.ConnectionResponse.newBuilder().setClientId(clientId).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            System.out.println("User connection request completed.");
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
