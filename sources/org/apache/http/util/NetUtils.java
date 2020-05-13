package org.apache.http.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class NetUtils {
    public static void formatAddress(StringBuilder buffer, SocketAddress socketAddress) {
        Args.notNull(buffer, "Buffer");
        Args.notNull(socketAddress, "Socket address");
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress socketaddr = (InetSocketAddress) socketAddress;
            InetAddress inetaddr = socketaddr.getAddress();
            Object obj = inetaddr;
            if (inetaddr != null) {
                obj = inetaddr.getHostAddress();
            }
            buffer.append(obj).append(':').append(socketaddr.getPort());
            return;
        }
        buffer.append(socketAddress);
    }
}
