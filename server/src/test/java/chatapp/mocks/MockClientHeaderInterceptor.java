package chatapp.mocks;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;
import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;

public class MockClientHeaderInterceptor implements ClientInterceptor {
    static final Key<String> CLIENT_ID = Key.of("client_id", ASCII_STRING_MARSHALLER);
    static final Key<String> ROOM_ID = Key.of("room_id", ASCII_STRING_MARSHALLER);


    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method,
                                                               final CallOptions callOptions, final Channel next) {
        return new SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(final Listener responseListener, final Metadata headers) {
                headers.put(CLIENT_ID, String.valueOf(1));
                headers.put(ROOM_ID, String.valueOf(0));

                super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(final Metadata headers) {
                        newAttachHeadersInterceptor(headers);
                    }
                }, headers);
            }
        };
    }
}
