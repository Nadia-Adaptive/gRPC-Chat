package chatapp.service;

import chatapp.ConnectionService.ConnectionServiceGrpc;
import chatapp.ConnectionService.ConnectionServiceOuterClass;
import chatapp.server.UserRepository;
import io.grpc.stub.StreamObserver;

public class ConnectionServiceImpl extends ConnectionServiceGrpc.ConnectionServiceImplBase {
    final UserRepository userRepository;

    public ConnectionServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void connect(final ConnectionServiceOuterClass.ConnectionRequest request,
                        final StreamObserver<ConnectionServiceOuterClass.ConnectionResponse> responseObserver) {
        try {
            // TODO: authenticate user
            System.out.println("User connection request received.");

            final var clientId = userRepository.addUser(request.getUsername()).id();
            final var response =
                    ConnectionServiceOuterClass.ConnectionResponse.newBuilder().setClientId(clientId).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            System.out.println("User connection request completed.");
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
