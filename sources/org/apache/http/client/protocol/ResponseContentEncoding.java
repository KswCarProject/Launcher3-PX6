package org.apache.http.client.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.entity.DecompressingEntity;
import org.apache.http.client.entity.DeflateInputStream;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ResponseContentEncoding implements HttpResponseInterceptor {
    private static final InputStreamFactory DEFLATE = new InputStreamFactory() {
        public InputStream create(InputStream instream) throws IOException {
            return new DeflateInputStream(instream);
        }
    };
    private static final InputStreamFactory GZIP = new InputStreamFactory() {
        public InputStream create(InputStream instream) throws IOException {
            return new GZIPInputStream(instream);
        }
    };
    public static final String UNCOMPRESSED = "http.client.response.uncompressed";
    private final Lookup<InputStreamFactory> decoderRegistry;
    private final boolean ignoreUnknown;

    public ResponseContentEncoding(Lookup<InputStreamFactory> decoderRegistry2, boolean ignoreUnknown2) {
        this.decoderRegistry = decoderRegistry2 == null ? RegistryBuilder.create().register("gzip", GZIP).register("x-gzip", GZIP).register("deflate", DEFLATE).build() : decoderRegistry2;
        this.ignoreUnknown = ignoreUnknown2;
    }

    public ResponseContentEncoding(boolean ignoreUnknown2) {
        this((Lookup<InputStreamFactory>) null, ignoreUnknown2);
    }

    public ResponseContentEncoding(Lookup<InputStreamFactory> decoderRegistry2) {
        this(decoderRegistry2, true);
    }

    public ResponseContentEncoding() {
        this((Lookup<InputStreamFactory>) null);
    }

    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Header ceheader;
        HttpEntity entity = response.getEntity();
        if (HttpClientContext.adapt(context).getRequestConfig().isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0 && (ceheader = entity.getContentEncoding()) != null) {
            for (HeaderElement codec : ceheader.getElements()) {
                String codecname = codec.getName().toLowerCase(Locale.ROOT);
                InputStreamFactory decoderFactory = this.decoderRegistry.lookup(codecname);
                if (decoderFactory != null) {
                    response.setEntity(new DecompressingEntity(response.getEntity(), decoderFactory));
                    response.removeHeaders("Content-Length");
                    response.removeHeaders("Content-Encoding");
                    response.removeHeaders(HttpHeaders.CONTENT_MD5);
                } else if (!HTTP.IDENTITY_CODING.equals(codecname) && !this.ignoreUnknown) {
                    throw new HttpException("Unsupported Content-Encoding: " + codec.getName());
                }
            }
        }
    }
}
