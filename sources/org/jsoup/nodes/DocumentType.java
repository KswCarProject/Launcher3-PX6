package org.jsoup.nodes;

import com.szchoiceway.index.InstallShortcutReceiver;
import org.apache.http.message.TokenParser;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;

public class DocumentType extends Node {
    public DocumentType(String name, String publicId, String systemId, String baseUri) {
        super(baseUri);
        attr(InstallShortcutReceiver.NAME_KEY, name);
        attr("publicId", publicId);
        attr("systemId", systemId);
    }

    public String nodeName() {
        return "#doctype";
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
        accum.append("<!DOCTYPE");
        if (!StringUtil.isBlank(attr(InstallShortcutReceiver.NAME_KEY))) {
            accum.append(" ").append(attr(InstallShortcutReceiver.NAME_KEY));
        }
        if (!StringUtil.isBlank(attr("publicId"))) {
            accum.append(" PUBLIC \"").append(attr("publicId")).append(TokenParser.DQUOTE);
        }
        if (!StringUtil.isBlank(attr("systemId"))) {
            accum.append(" \"").append(attr("systemId")).append(TokenParser.DQUOTE);
        }
        accum.append('>');
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
    }
}
