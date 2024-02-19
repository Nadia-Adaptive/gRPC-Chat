package service;

import chatapp.ConnectionService.ConnectionServiceGrpc;
import chatapp.ConnectionService.ConnectionServiceOuterClass;
import io.grpc.Channel;

public class ChatConnection {
    private final ConnectionServiceGrpc.ConnectionServiceBlockingStub stub;
    public int clientId;

    public ChatConnection(Channel channel) {
        stub = ConnectionServiceGrpc.newBlockingStub(channel);
    }

    public boolean connectToServer(final String username) {
        clientId = stub.connect(ConnectionServiceOuterClass.ConnectionRequest.newBuilder().setUsername(username).setPassword("password").build()).getClientId();
        System.out.println("Connected with client id of " + clientId);
        return clientId != Integer.MIN_VALUE;
    }
}