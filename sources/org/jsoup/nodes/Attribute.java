package org.jsoup.nodes;

import java.util.Arrays;
import java.util.Map;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.message.TokenParser;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;

public class Attribute implements Map.Entry<String, String>, Cloneable {
    private static final String[] booleanAttributes = {"allowfullscreen", "async", "autofocus", "checked", "compact", "declare", CookieSpecs.DEFAULT, "defer", "disabled", "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected", "sortable", "truespeed", "typemustmatch"};
    private String key;
    private String value;

    public Attribute(String key2, String value2) {
        Validate.notEmpty(key2);
        Validate.notNull(value2);
        this.key = key2.trim().toLowerCase();
        this.value = value2;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key2) {
        Validate.notEmpty(key2);
        this.key = key2.trim().toLowerCase();
    }

    public String getValue() {
        return this.value;
    }

    public String setValue(String value2) {
        Validate.notNull(value2);
        String old = this.value;
        this.value = value2;
        return old;
    }

    public String html() {
        StringBuilder accum = new StringBuilder();
        html(accum, new Document("").outputSettings());
        return accum.toString();
    }

    /* access modifiers changed from: protected */
    public void html(StringBuilder accum, Document.OutputSettings out) {
        accum.append(this.key);
        if (!shouldCollapseAttribute(out)) {
            accum.append("=\"");
            Entities.escape(accum, this.value, out, true, false, false);
            accum.append(TokenParser.DQUOTE);
        }
    }

    public String toString() {
        return html();
    }

    public static Attribute createFromEncoded(String unencodedKey, String encodedValue) {
        return new Attribute(unencodedKey, Entities.unescape(encodedValue, true));
    }

    /* access modifiers changed from: protected */
    public boolean isDataAttribute() {
        return this.key.startsWith("data-") && this.key.length() > "data-".length();
    }

    /* access modifiers changed from: protected */
    public final boolean shouldCollapseAttribute(Document.OutputSettings out) {
        return ("".equals(this.value) || this.value.equalsIgnoreCase(this.key)) && out.syntax() == Document.OutputSettings.Syntax.html && Arrays.binarySearch(booleanAttributes, this.key) >= 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute attribute = (Attribute) o;
        if (this.key == null ? attribute.key != null : !this.key.equals(attribute.key)) {
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(attribute.value)) {
                return true;
            }
        } else if (attribute.value == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int i = 0;
        if (this.key != null) {
            result = this.key.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 31;
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return i2 + i;
    }

    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
