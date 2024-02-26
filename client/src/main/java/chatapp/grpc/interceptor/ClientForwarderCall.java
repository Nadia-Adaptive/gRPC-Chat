package chatapp.grpc.interceptor;

import chatapp.connection.ChatConnection;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;

import static chatapp.grpc.interceptor.ClientHeaderInterceptor.CLIENT_ID;
import static chatapp.grpc.interceptor.ClientHeaderInterceptor.ROOM_ID;

public class ClientForwarderCall extends SimpleForwardingClientCall {
    protected ClientForwarderCall(final ClientCall delegate) {
        super(delegate);
    }

    @Override
    public void start(final Listener responseListener, final Metadata headers) {
        headers.put(CLIENT_ID, String.valueOf(ChatConnection.clientId));
        headers.put(ROOM_ID, String.valueOf(ChatConnection.roomId));
        super.start(new CallForwarderListener(responseListener), headers);
    }
}
