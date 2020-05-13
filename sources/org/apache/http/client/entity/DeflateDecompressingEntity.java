package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;

public class DeflateDecompressingEntity extends DecompressingEntity {
    public DeflateDecompressingEntity(HttpEntity entity) {
        super(entity, new InputStreamFactory() {
            public InputStream create(InputStream instream) throws IOException {
                return new DeflateInputStream(instream);
            }
        });
    }
}
