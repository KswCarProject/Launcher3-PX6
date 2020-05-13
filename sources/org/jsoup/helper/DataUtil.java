package org.jsoup.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

public class DataUtil {
    private static final int bufferSize = 131072;
    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
    static final String defaultCharset = "UTF-8";

    private DataUtil() {
    }

    public static Document load(File in, String charsetName, String baseUri) throws IOException {
        return parseByteData(readFileToByteBuffer(in), charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri) throws IOException {
        return parseByteData(readToByteBuffer(in), charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri, Parser parser) throws IOException {
        return parseByteData(readToByteBuffer(in), charsetName, baseUri, parser);
    }

    static Document parseByteData(ByteBuffer byteData, String charsetName, String baseUri, Parser parser) {
        String docData;
        String foundCharset;
        Document doc = null;
        if (charsetName == null) {
            docData = Charset.forName("UTF-8").decode(byteData).toString();
            doc = parser.parseInput(docData, baseUri);
            Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
            if (meta != null) {
                if (meta.hasAttr("http-equiv")) {
                    foundCharset = getCharsetFromContentType(meta.attr("content"));
                    if (foundCharset == null && meta.hasAttr("charset")) {
                        try {
                            if (Charset.isSupported(meta.attr("charset"))) {
                                foundCharset = meta.attr("charset");
                            }
                        } catch (IllegalCharsetNameException e) {
                            foundCharset = null;
                        }
                    }
                } else {
                    foundCharset = meta.attr("charset");
                }
                if (!(foundCharset == null || foundCharset.length() == 0 || foundCharset.equals("UTF-8"))) {
                    String foundCharset2 = foundCharset.trim().replaceAll("[\"']", "");
                    charsetName = foundCharset2;
                    byteData.rewind();
                    docData = Charset.forName(foundCharset2).decode(byteData).toString();
                    doc = null;
                }
            }
        } else {
            Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
            docData = Charset.forName(charsetName).decode(byteData).toString();
        }
        if (docData.length() > 0 && docData.charAt(0) == 65279) {
            byteData.rewind();
            docData = Charset.forName("UTF-8").decode(byteData).toString().substring(1);
            charsetName = "UTF-8";
            doc = null;
        }
        if (doc != null) {
            return doc;
        }
        Document doc2 = parser.parseInput(docData, baseUri);
        doc2.outputSettings().charset(charsetName);
        return doc2;
    }

    static ByteBuffer readToByteBuffer(InputStream inStream, int maxSize) throws IOException {
        boolean z;
        boolean capped;
        if (maxSize >= 0) {
            z = true;
        } else {
            z = false;
        }
        Validate.isTrue(z, "maxSize must be 0 (unlimited) or larger");
        if (maxSize > 0) {
            capped = true;
        } else {
            capped = false;
        }
        byte[] buffer = new byte[131072];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(131072);
        int remaining = maxSize;
        while (true) {
            int read = inStream.read(buffer);
            if (read == -1) {
                break;
            }
            if (capped) {
                if (read > remaining) {
                    outStream.write(buffer, 0, remaining);
                    break;
                }
                remaining -= read;
            }
            outStream.write(buffer, 0, read);
        }
        return ByteBuffer.wrap(outStream.toByteArray());
    }

    static ByteBuffer readToByteBuffer(InputStream inStream) throws IOException {
        return readToByteBuffer(inStream, 0);
    }

    static ByteBuffer readFileToByteBuffer(File file) throws IOException {
        RandomAccessFile randomAccessFile = null;
        try {
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "r");
            try {
                byte[] bytes = new byte[((int) randomAccessFile2.length())];
                randomAccessFile2.readFully(bytes);
                ByteBuffer wrap = ByteBuffer.wrap(bytes);
                if (randomAccessFile2 != null) {
                    randomAccessFile2.close();
                }
                return wrap;
            } catch (Throwable th) {
                th = th;
                randomAccessFile = randomAccessFile2;
            }
        } catch (Throwable th2) {
            th = th2;
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            throw th;
        }
    }

    static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            String charset = m.group(1).trim().replace("charset=", "");
            if (charset.length() == 0) {
                return null;
            }
            try {
                if (Charset.isSupported(charset)) {
                    return charset;
                }
                String charset2 = charset.toUpperCase(Locale.ENGLISH);
                if (Charset.isSupported(charset2)) {
                    return charset2;
                }
            } catch (IllegalCharsetNameException e) {
                return null;
            }
        }
        return null;
    }
}
