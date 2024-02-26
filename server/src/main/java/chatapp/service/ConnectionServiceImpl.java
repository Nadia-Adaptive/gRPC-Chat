package chatapp.service;

import chatapp.ConnectionService.ConnectionServiceGrpc.ConnectionServiceImplBase;
import chatapp.ConnectionService.ConnectionServiceOuterClass.ConnectionRequest;
import chatapp.ConnectionService.ConnectionServiceOuterClass.ConnectionResponse;
import chatapp.user.UserRepository;
import io.grpc.stub.StreamObserver;

public class ConnectionServiceImpl extends ConnectionServiceImplBase {
    final UserRepository userRepository;

    public ConnectionServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void connect(final ConnectionRequest request,
                        final StreamObserver<ConnectionResponse> responseObserver) {
        try {
            // TODO: authenticate user
            System.out.println("User connection request received.");

            final var clientId = userRepository.addUser(request.getUsername()).id();
            final var response =
                    ConnectionResponse.newBuilder().setClientId(clientId).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            System.out.println("User connection request completed.");
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
