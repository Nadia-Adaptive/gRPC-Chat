package chatapp.grpc.interceptor;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class ClientHeaderInterceptor implements ClientInterceptor {
    static final Key<String> CLIENT_ID = Key.of("client_id", ASCII_STRING_MARSHALLER);
    static final Key<String> ROOM_ID = Key.of("room_id", ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method,
                                                               final CallOptions callOptions, final Channel next) {
        return new ClientForwarderCall(next.newCall(method, callOptions));
    }
}
