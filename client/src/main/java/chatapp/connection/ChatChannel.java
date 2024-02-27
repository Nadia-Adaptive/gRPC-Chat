package chatapp.connection;

import io.grpc.Channel;

public class ChatChannel {
    private static Channel channel;

    public static Channel getChannel() {
        return channel;
    }

    public static void setChannel(final Channel channel) {
        if (ChatChannel.channel == null) {
            ChatChannel.channel = channel;
        }
    }
}
