package chatapp.server;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class HeaderInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call,
                                                                 final Metadata requestHeaders,
                                                                 final ServerCallHandler<ReqT, RespT> next) {
        var context = Context.current()
                .withValues(RequestHeader.CTX_CLIENT_ID, getIdHeader(requestHeaders, RequestHeader.CLIENT_ID),
                        RequestHeader.CTX_ROOM_ID, getIdHeader(requestHeaders, RequestHeader.ROOM_ID));

        return Contexts.interceptCall(context, call, requestHeaders, next);
    }

    private int getIdHeader(final Metadata requestHeaders, final Metadata.Key<String> headerKey) {
        try {
            final var id = Integer.parseInt(requestHeaders.get(headerKey));
            return id;
        } catch (final NumberFormatException e) {
            System.out.println("Could not parse id of " + requestHeaders.get(headerKey));
            throw new RuntimeException("Invalid request header id");
        }
    }
}
