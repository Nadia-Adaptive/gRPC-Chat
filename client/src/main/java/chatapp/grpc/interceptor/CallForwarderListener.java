package chatapp.grpc.interceptor;

import io.grpc.ClientCall.Listener;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;

public class CallForwarderListener extends SimpleForwardingClientCallListener {
    protected CallForwarderListener(final Listener delegate) {
        super(delegate);
    }

    @Override
    public void onHeaders(final Metadata headers) {
        newAttachHeadersInterceptor(headers);
    }
}
