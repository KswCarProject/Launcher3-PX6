package org.apache.http.client.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;

public class URIBuilder {
    private Charset charset;
    private String encodedAuthority;
    private String encodedFragment;
    private String encodedPath;
    private String encodedQuery;
    private String encodedSchemeSpecificPart;
    private String encodedUserInfo;
    private String fragment;
    private String host;
    private String path;
    private int port;
    private String query;
    private List<NameValuePair> queryParams;
    private String scheme;
    private String userInfo;

    public URIBuilder() {
        this.port = -1;
    }

    public URIBuilder(String string) throws URISyntaxException {
        digestURI(new URI(string));
    }

    public URIBuilder(URI uri) {
        digestURI(uri);
    }

    public URIBuilder setCharset(Charset charset2) {
        this.charset = charset2;
        return this;
    }

    public Charset getCharset() {
        return this.charset;
    }

    private List<NameValuePair> parseQuery(String query2, Charset charset2) {
        if (query2 == null || query2.isEmpty()) {
            return null;
        }
        return URLEncodedUtils.parse(query2, charset2);
    }

    public URI build() throws URISyntaxException {
        return new URI(buildString());
    }

    private String buildString() {
        StringBuilder sb = new StringBuilder();
        if (this.scheme != null) {
            sb.append(this.scheme).append(':');
        }
        if (this.encodedSchemeSpecificPart != null) {
            sb.append(this.encodedSchemeSpecificPart);
        } else {
            if (this.encodedAuthority != null) {
                sb.append("//").append(this.encodedAuthority);
            } else if (this.host != null) {
                sb.append("//");
                if (this.encodedUserInfo != null) {
                    sb.append(this.encodedUserInfo).append("@");
                } else if (this.userInfo != null) {
                    sb.append(encodeUserInfo(this.userInfo)).append("@");
                }
                if (InetAddressUtils.isIPv6Address(this.host)) {
                    sb.append("[").append(this.host).append("]");
                } else {
                    sb.append(this.host);
                }
                if (this.port >= 0) {
                    sb.append(":").append(this.port);
                }
            }
            if (this.encodedPath != null) {
                sb.append(normalizePath(this.encodedPath));
            } else if (this.path != null) {
                sb.append(encodePath(normalizePath(this.path)));
            }
            if (this.encodedQuery != null) {
                sb.append("?").append(this.encodedQuery);
            } else if (this.queryParams != null) {
                sb.append("?").append(encodeUrlForm(this.queryParams));
            } else if (this.query != null) {
                sb.append("?").append(encodeUric(this.query));
            }
        }
        if (this.encodedFragment != null) {
            sb.append("#").append(this.encodedFragment);
        } else if (this.fragment != null) {
            sb.append("#").append(encodeUric(this.fragment));
        }
        return sb.toString();
    }

    private void digestURI(URI uri) {
        this.scheme = uri.getScheme();
        this.encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
        this.encodedAuthority = uri.getRawAuthority();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.encodedUserInfo = uri.getRawUserInfo();
        this.userInfo = uri.getUserInfo();
        this.encodedPath = uri.getRawPath();
        this.path = uri.getPath();
        this.encodedQuery = uri.getRawQuery();
        this.queryParams = parseQuery(uri.getRawQuery(), this.charset != null ? this.charset : Consts.UTF_8);
        this.encodedFragment = uri.getRawFragment();
        this.fragment = uri.getFragment();
    }

    private String encodeUserInfo(String userInfo2) {
        return URLEncodedUtils.encUserInfo(userInfo2, this.charset != null ? this.charset : Consts.UTF_8);
    }

    private String encodePath(String path2) {
        return URLEncodedUtils.encPath(path2, this.charset != null ? this.charset : Consts.UTF_8);
    }

    private String encodeUrlForm(List<NameValuePair> params) {
        return URLEncodedUtils.format((Iterable<? extends NameValuePair>) params, this.charset != null ? this.charset : Consts.UTF_8);
    }

    private String encodeUric(String fragment2) {
        return URLEncodedUtils.encUric(fragment2, this.charset != null ? this.charset : Consts.UTF_8);
    }

    public URIBuilder setScheme(String scheme2) {
        this.scheme = scheme2;
        return this;
    }

    public URIBuilder setUserInfo(String userInfo2) {
        this.userInfo = userInfo2;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        this.encodedUserInfo = null;
        return this;
    }

    public URIBuilder setUserInfo(String username, String password) {
        return setUserInfo(username + ':' + password);
    }

    public URIBuilder setHost(String host2) {
        this.host = host2;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    public URIBuilder setPort(int port2) {
        if (port2 < 0) {
            port2 = -1;
        }
        this.port = port2;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    public URIBuilder setPath(String path2) {
        this.path = path2;
        this.encodedSchemeSpecificPart = null;
        this.encodedPath = null;
        return this;
    }

    public URIBuilder removeQuery() {
        this.queryParams = null;
        this.query = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    @Deprecated
    public URIBuilder setQuery(String query2) {
        this.queryParams = parseQuery(query2, this.charset != null ? this.charset : Consts.UTF_8);
        this.query = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    public URIBuilder setParameters(List<NameValuePair> nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList();
        } else {
            this.queryParams.clear();
        }
        this.queryParams.addAll(nvps);
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder addParameters(List<NameValuePair> nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList();
        }
        this.queryParams.addAll(nvps);
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder setParameters(NameValuePair... nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList();
        } else {
            this.queryParams.clear();
        }
        for (NameValuePair nvp : nvps) {
            this.queryParams.add(nvp);
        }
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder addParameter(String param, String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList();
        }
        this.queryParams.add(new BasicNameValuePair(param, value));
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder setParameter(String param, String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList();
        }
        if (!this.queryParams.isEmpty()) {
            Iterator<NameValuePair> it = this.queryParams.iterator();
            while (it.hasNext()) {
                if (it.next().getName().equals(param)) {
                    it.remove();
                }
            }
        }
        this.queryParams.add(new BasicNameValuePair(param, value));
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    public URIBuilder clearParameters() {
        this.queryParams = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    public URIBuilder setCustomQuery(String query2) {
        this.query = query2;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.queryParams = null;
        return this;
    }

    public URIBuilder setFragment(String fragment2) {
        this.fragment = fragment2;
        this.encodedFragment = null;
        return this;
    }

    public boolean isAbsolute() {
        return this.scheme != null;
    }

    public boolean isOpaque() {
        return this.path == null;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getPath() {
        return this.path;
    }

    public List<NameValuePair> getQueryParams() {
        if (this.queryParams != null) {
            return new ArrayList(this.queryParams);
        }
        return new ArrayList();
    }

    public String getFragment() {
        return this.fragment;
    }

    public String toString() {
        return buildString();
    }

    private static String normalizePath(String path2) {
        String s = path2;
        if (s == null) {
            return "/";
        }
        int n = 0;
        while (n < s.length() && s.charAt(n) == '/') {
            n++;
        }
        if (n > 1) {
            s = s.substring(n - 1);
        }
        if (!s.startsWith("/")) {
            s = "/" + s;
        }
        return s;
    }
}