package org.apache.http.impl.client;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;

@Contract(threading = ThreadingBehavior.SAFE)
public class SystemDefaultCredentialsProvider implements CredentialsProvider {
    private static final Map<String, String> SCHEME_MAP = new ConcurrentHashMap();
    private final BasicCredentialsProvider internal = new BasicCredentialsProvider();

    static {
        SCHEME_MAP.put("Basic".toUpperCase(Locale.ROOT), "Basic");
        SCHEME_MAP.put("Digest".toUpperCase(Locale.ROOT), "Digest");
        SCHEME_MAP.put("NTLM".toUpperCase(Locale.ROOT), "NTLM");
        SCHEME_MAP.put("Negotiate".toUpperCase(Locale.ROOT), "SPNEGO");
        SCHEME_MAP.put("Kerberos".toUpperCase(Locale.ROOT), "Kerberos");
    }

    private static String translateScheme(String key) {
        if (key == null) {
            return null;
        }
        String s = SCHEME_MAP.get(key);
        return s == null ? key : s;
    }

    public void setCredentials(AuthScope authscope, Credentials credentials) {
        this.internal.setCredentials(authscope, credentials);
    }

    private static PasswordAuthentication getSystemCreds(AuthScope authscope, Authenticator.RequestorType requestorType) {
        String hostname = authscope.getHost();
        int port = authscope.getPort();
        HttpHost origin = authscope.getOrigin();
        return Authenticator.requestPasswordAuthentication(hostname, (InetAddress) null, port, origin != null ? origin.getSchemeName() : port == 443 ? "https" : HttpHost.DEFAULT_SCHEME_NAME, (String) null, translateScheme(authscope.getScheme()), (URL) null, requestorType);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0060  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.http.auth.Credentials getCredentials(org.apache.http.auth.AuthScope r15) {
        /*
            r14 = this;
            r10 = 0
            java.lang.String r11 = "Auth scope"
            org.apache.http.util.Args.notNull(r15, r11)
            org.apache.http.impl.client.BasicCredentialsProvider r11 = r14.internal
            org.apache.http.auth.Credentials r2 = r11.getCredentials(r15)
            if (r2 == 0) goto L_0x000f
        L_0x000e:
            return r2
        L_0x000f:
            java.lang.String r1 = r15.getHost()
            if (r1 == 0) goto L_0x00b6
            java.net.Authenticator$RequestorType r11 = java.net.Authenticator.RequestorType.SERVER
            java.net.PasswordAuthentication r8 = getSystemCreds(r15, r11)
            if (r8 != 0) goto L_0x00bb
            java.net.Authenticator$RequestorType r11 = java.net.Authenticator.RequestorType.PROXY
            java.net.PasswordAuthentication r8 = getSystemCreds(r15, r11)
            r9 = r8
        L_0x0024:
            if (r9 != 0) goto L_0x00b9
            java.lang.String r11 = "http.proxyHost"
            java.lang.String r3 = java.lang.System.getProperty(r11)
            if (r3 == 0) goto L_0x00b9
            java.lang.String r11 = "http.proxyPort"
            java.lang.String r5 = java.lang.System.getProperty(r11)
            if (r5 == 0) goto L_0x00b9
            org.apache.http.auth.AuthScope r7 = new org.apache.http.auth.AuthScope     // Catch:{ NumberFormatException -> 0x007f }
            int r11 = java.lang.Integer.parseInt(r5)     // Catch:{ NumberFormatException -> 0x007f }
            r7.<init>(r3, r11)     // Catch:{ NumberFormatException -> 0x007f }
            int r11 = r15.match(r7)     // Catch:{ NumberFormatException -> 0x007f }
            if (r11 < 0) goto L_0x00b9
            java.lang.String r11 = "http.proxyUser"
            java.lang.String r6 = java.lang.System.getProperty(r11)     // Catch:{ NumberFormatException -> 0x007f }
            if (r6 == 0) goto L_0x00b9
            java.lang.String r11 = "http.proxyPassword"
            java.lang.String r4 = java.lang.System.getProperty(r11)     // Catch:{ NumberFormatException -> 0x007f }
            java.net.PasswordAuthentication r8 = new java.net.PasswordAuthentication     // Catch:{ NumberFormatException -> 0x007f }
            if (r4 == 0) goto L_0x007b
            char[] r11 = r4.toCharArray()     // Catch:{ NumberFormatException -> 0x007f }
        L_0x005b:
            r8.<init>(r6, r11)     // Catch:{ NumberFormatException -> 0x007f }
        L_0x005e:
            if (r8 == 0) goto L_0x00b6
            java.lang.String r11 = "http.auth.ntlm.domain"
            java.lang.String r0 = java.lang.System.getProperty(r11)
            if (r0 == 0) goto L_0x0082
            org.apache.http.auth.NTCredentials r2 = new org.apache.http.auth.NTCredentials
            java.lang.String r11 = r8.getUserName()
            java.lang.String r12 = new java.lang.String
            char[] r13 = r8.getPassword()
            r12.<init>(r13)
            r2.<init>(r11, r12, r10, r0)
            goto L_0x000e
        L_0x007b:
            r11 = 0
            char[] r11 = new char[r11]     // Catch:{ NumberFormatException -> 0x007f }
            goto L_0x005b
        L_0x007f:
            r11 = move-exception
            r8 = r9
            goto L_0x005e
        L_0x0082:
            java.lang.String r11 = "NTLM"
            java.lang.String r12 = r15.getScheme()
            boolean r11 = r11.equalsIgnoreCase(r12)
            if (r11 == 0) goto L_0x00a2
            org.apache.http.auth.NTCredentials r2 = new org.apache.http.auth.NTCredentials
            java.lang.String r11 = r8.getUserName()
            java.lang.String r12 = new java.lang.String
            char[] r13 = r8.getPassword()
            r12.<init>(r13)
            r2.<init>(r11, r12, r10, r10)
            goto L_0x000e
        L_0x00a2:
            org.apache.http.auth.UsernamePasswordCredentials r2 = new org.apache.http.auth.UsernamePasswordCredentials
            java.lang.String r10 = r8.getUserName()
            java.lang.String r11 = new java.lang.String
            char[] r12 = r8.getPassword()
            r11.<init>(r12)
            r2.<init>(r10, r11)
            goto L_0x000e
        L_0x00b6:
            r2 = r10
            goto L_0x000e
        L_0x00b9:
            r8 = r9
            goto L_0x005e
        L_0x00bb:
            r9 = r8
            goto L_0x0024
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.client.SystemDefaultCredentialsProvider.getCredentials(org.apache.http.auth.AuthScope):org.apache.http.auth.Credentials");
    }

    public void clear() {
        this.internal.clear();
    }
}
