package org.apache.http.conn.ssl;

import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

@Deprecated
public class SSLContextBuilder {
    static final String SSL = "SSL";
    static final String TLS = "TLS";
    private final Set<KeyManager> keymanagers = new LinkedHashSet();
    private String protocol;
    private SecureRandom secureRandom;
    private final Set<TrustManager> trustmanagers = new LinkedHashSet();

    public SSLContextBuilder useTLS() {
        this.protocol = "TLS";
        return this;
    }

    public SSLContextBuilder useSSL() {
        this.protocol = "SSL";
        return this;
    }

    public SSLContextBuilder useProtocol(String protocol2) {
        this.protocol = protocol2;
        return this;
    }

    public SSLContextBuilder setSecureRandom(SecureRandom secureRandom2) {
        this.secureRandom = secureRandom2;
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(KeyStore truststore, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(truststore);
        TrustManager[] tms = tmfactory.getTrustManagers();
        if (tms != null) {
            if (trustStrategy != null) {
                for (int i = 0; i < tms.length; i++) {
                    TrustManager tm = tms[i];
                    if (tm instanceof X509TrustManager) {
                        tms[i] = new TrustManagerDelegate((X509TrustManager) tm, trustStrategy);
                    }
                }
            }
            for (TrustManager tm2 : tms) {
                this.trustmanagers.add(tm2);
            }
        }
        return this;
    }

    public SSLContextBuilder loadTrustMaterial(KeyStore truststore) throws NoSuchAlgorithmException, KeyStoreException {
        return loadTrustMaterial(truststore, (TrustStrategy) null);
    }

    public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        loadKeyMaterial(keystore, keyPassword, (PrivateKeyStrategy) null);
        return this;
    }

    public SSLContextBuilder loadKeyMaterial(KeyStore keystore, char[] keyPassword, PrivateKeyStrategy aliasStrategy) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keyPassword);
        KeyManager[] kms = kmfactory.getKeyManagers();
        if (kms != null) {
            if (aliasStrategy != null) {
                for (int i = 0; i < kms.length; i++) {
                    KeyManager km = kms[i];
                    if (km instanceof X509KeyManager) {
                        kms[i] = new KeyManagerDelegate((X509KeyManager) km, aliasStrategy);
                    }
                }
            }
            for (KeyManager km2 : kms) {
                this.keymanagers.add(km2);
            }
        }
        return this;
    }

    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        KeyManager[] keyManagerArr;
        SSLContext sslcontext = SSLContext.getInstance(this.protocol != null ? this.protocol : "TLS");
        if (!this.keymanagers.isEmpty()) {
            keyManagerArr = (KeyManager[]) this.keymanagers.toArray(new KeyManager[this.keymanagers.size()]);
        } else {
            keyManagerArr = null;
        }
        sslcontext.init(keyManagerArr, !this.trustmanagers.isEmpty() ? (TrustManager[]) this.trustmanagers.toArray(new TrustManager[this.trustmanagers.size()]) : null, this.secureRandom);
        return sslcontext;
    }

    static class TrustManagerDelegate implements X509TrustManager {
        private final X509TrustManager trustManager;
        private final TrustStrategy trustStrategy;

        TrustManagerDelegate(X509TrustManager trustManager2, TrustStrategy trustStrategy2) {
            this.trustManager = trustManager2;
            this.trustStrategy = trustStrategy2;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.trustManager.checkClientTrusted(chain, authType);
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (!this.trustStrategy.isTrusted(chain, authType)) {
                this.trustManager.checkServerTrusted(chain, authType);
            }
        }

        public X509Certificate[] getAcceptedIssuers() {
            return this.trustManager.getAcceptedIssuers();
        }
    }

    static class KeyManagerDelegate implements X509KeyManager {
        private final PrivateKeyStrategy aliasStrategy;
        private final X509KeyManager keyManager;

        KeyManagerDelegate(X509KeyManager keyManager2, PrivateKeyStrategy aliasStrategy2) {
            this.keyManager = keyManager2;
            this.aliasStrategy = aliasStrategy2;
        }

        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return this.keyManager.getClientAliases(keyType, issuers);
        }

        public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
            Map<String, PrivateKeyDetails> validAliases = new HashMap<>();
            for (String keyType : keyTypes) {
                String[] aliases = this.keyManager.getClientAliases(keyType, issuers);
                if (aliases != null) {
                    for (String alias : aliases) {
                        validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
                    }
                }
            }
            return this.aliasStrategy.chooseAlias(validAliases, socket);
        }

        public String[] getServerAliases(String keyType, Principal[] issuers) {
            return this.keyManager.getServerAliases(keyType, issuers);
        }

        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            Map<String, PrivateKeyDetails> validAliases = new HashMap<>();
            String[] aliases = this.keyManager.getServerAliases(keyType, issuers);
            if (aliases != null) {
                for (String alias : aliases) {
                    validAliases.put(alias, new PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias)));
                }
            }
            return this.aliasStrategy.chooseAlias(validAliases, socket);
        }

        public X509Certificate[] getCertificateChain(String alias) {
            return this.keyManager.getCertificateChain(alias);
        }

        public PrivateKey getPrivateKey(String alias) {
            return this.keyManager.getPrivateKey(alias);
        }
    }
}
