package org.apache.http.impl.cookie;

import java.io.IOException;
import java.io.Reader;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixList;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class PublicSuffixListParser {
    private final PublicSuffixFilter filter;
    private final org.apache.http.conn.util.PublicSuffixListParser parser = new org.apache.http.conn.util.PublicSuffixListParser();

    PublicSuffixListParser(PublicSuffixFilter filter2) {
        this.filter = filter2;
    }

    public void parse(Reader reader) throws IOException {
        PublicSuffixList suffixList = this.parser.parse(reader);
        this.filter.setPublicSuffixes(suffixList.getRules());
        this.filter.setExceptions(suffixList.getExceptions());
    }
}
