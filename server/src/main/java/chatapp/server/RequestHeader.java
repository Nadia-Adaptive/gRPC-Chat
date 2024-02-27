package chatapp.server;

import io.grpc.Context;
import io.grpc.Metadata;

public class RequestHeader {
    public static final Metadata.Key<String> CLIENT_ID = Metadata.Key.of("client_id", Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<Integer> CTX_CLIENT_ID = Context.key("client_id");

    public static final Metadata.Key<String> ROOM_ID = Metadata.Key.of("room_id", Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<Integer> CTX_ROOM_ID = Context.key("room_id");
}
