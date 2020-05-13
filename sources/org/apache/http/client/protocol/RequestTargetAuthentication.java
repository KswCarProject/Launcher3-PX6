package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthState;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class RequestTargetAuthentication extends RequestAuthenticationBase {
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        if (!request.getRequestLine().getMethod().equalsIgnoreCase("CONNECT") && !request.containsHeader("Authorization")) {
            AuthState authState = (AuthState) context.getAttribute("http.auth.target-scope");
            if (authState == null) {
                this.log.debug("Target auth state not set in the context");
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Target auth state: " + authState.getState());
            }
            process(authState, request, context);
        }
    }
}
