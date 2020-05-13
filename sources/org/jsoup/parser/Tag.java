package org.jsoup.parser;

import com.szchoiceway.index.LauncherSettings;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.helper.Validate;

public class Tag {
    private static final String[] blockTags = {"html", "head", "body", "frameset", "script", "noscript", "style", "meta", "link", LauncherSettings.BaseLauncherColumns.TITLE, "frame", "noframes", "section", "nav", "aside", "hgroup", "header", "footer", "p", "h1", "h2", "h3", "h4", "h5", "h6", "ul", "ol", "pre", "div", "blockquote", "hr", "address", "figure", "figcaption", "form", "fieldset", "ins", "del", "s", "dl", "dt", "dd", "li", "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th", "td", "video", "audio", "canvas", "details", "menu", "plaintext"};
    private static final String[] emptyTags = {"meta", "link", "base", "frame", "img", "br", "wbr", "embed", "hr", "input", "keygen", "col", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track"};
    private static final String[] formListedTags = {"button", "fieldset", "input", "keygen", "object", "output", "select", "textarea"};
    private static final String[] formSubmitTags = {"input", "keygen", "object", "select", "textarea"};
    private static final String[] formatAsInlineTags = {LauncherSettings.BaseLauncherColumns.TITLE, "a", "p", "h1", "h2", "h3", "h4", "h5", "h6", "pre", "address", "li", "th", "td", "script", "style", "ins", "del", "s"};
    private static final String[] inlineTags = {"object", "base", "font", "tt", "i", "b", "u", "big", "small", "em", "strong", "dfn", "code", "samp", "kbd", "var", "cite", "abbr", "time", "acronym", "mark", "ruby", "rt", "rp", "a", "img", "br", "wbr", "map", "q", "sub", "sup", "bdo", "iframe", "embed", "span", "input", "select", "textarea", "label", "button", "optgroup", "option", "legend", "datalist", "keygen", "output", "progress", "meter", "area", "param", "source", "track", "summary", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track"};
    private static final String[] preserveWhitespaceTags = {"pre", "plaintext", LauncherSettings.BaseLauncherColumns.TITLE, "textarea"};
    private static final Map<String, Tag> tags = new HashMap();
    private boolean canContainBlock = true;
    private boolean canContainInline = true;
    private boolean empty = false;
    private boolean formList = false;
    private boolean formSubmit = false;
    private boolean formatAsBlock = true;
    private boolean isBlock = true;
    private boolean preserveWhitespace = false;
    private boolean selfClosing = false;
    private String tagName;

    static {
        for (String tagName2 : blockTags) {
            register(new Tag(tagName2));
        }
        for (String tagName3 : inlineTags) {
            Tag tag = new Tag(tagName3);
            tag.isBlock = false;
            tag.canContainBlock = false;
            tag.formatAsBlock = false;
            register(tag);
        }
        for (String tagName4 : emptyTags) {
            Tag tag2 = tags.get(tagName4);
            Validate.notNull(tag2);
            tag2.canContainBlock = false;
            tag2.canContainInline = false;
            tag2.empty = true;
        }
        for (String tagName5 : formatAsInlineTags) {
            Tag tag3 = tags.get(tagName5);
            Validate.notNull(tag3);
            tag3.formatAsBlock = false;
        }
        for (String tagName6 : preserveWhitespaceTags) {
            Tag tag4 = tags.get(tagName6);
            Validate.notNull(tag4);
            tag4.preserveWhitespace = true;
        }
        for (String tagName7 : formListedTags) {
            Tag tag5 = tags.get(tagName7);
            Validate.notNull(tag5);
            tag5.formList = true;
        }
        for (String tagName8 : formSubmitTags) {
            Tag tag6 = tags.get(tagName8);
            Validate.notNull(tag6);
            tag6.formSubmit = true;
        }
    }

    private Tag(String tagName2) {
        this.tagName = tagName2.toLowerCase();
    }

    public String getName() {
        return this.tagName;
    }

    public static Tag valueOf(String tagName2) {
        Validate.notNull(tagName2);
        Tag tag = tags.get(tagName2);
        if (tag != null) {
            return tag;
        }
        String tagName3 = tagName2.trim().toLowerCase();
        Validate.notEmpty(tagName3);
        Tag tag2 = tags.get(tagName3);
        if (tag2 != null) {
            return tag2;
        }
        Tag tag3 = new Tag(tagName3);
        tag3.isBlock = false;
        tag3.canContainBlock = true;
        return tag3;
    }

    public boolean isBlock() {
        return this.isBlock;
    }

    public boolean formatAsBlock() {
        return this.formatAsBlock;
    }

    public boolean canContainBlock() {
        return this.canContainBlock;
    }

    public boolean isInline() {
        return !this.isBlock;
    }

    public boolean isData() {
        return !this.canContainInline && !isEmpty();
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public boolean isSelfClosing() {
        return this.empty || this.selfClosing;
    }

    public boolean isKnownTag() {
        return tags.containsKey(this.tagName);
    }

    public static boolean isKnownTag(String tagName2) {
        return tags.containsKey(tagName2);
    }

    public boolean preserveWhitespace() {
        return this.preserveWhitespace;
    }

    public boolean isFormListed() {
        return this.formList;
    }

    public boolean isFormSubmittable() {
        return this.formSubmit;
    }

    /* access modifiers changed from: package-private */
    public Tag setSelfClosing() {
        this.selfClosing = true;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        if (this.canContainBlock != tag.canContainBlock) {
            return false;
        }
        if (this.canContainInline != tag.canContainInline) {
            return false;
        }
        if (this.empty != tag.empty) {
            return false;
        }
        if (this.formatAsBlock != tag.formatAsBlock) {
            return false;
        }
        if (this.isBlock != tag.isBlock) {
            return false;
        }
        if (this.preserveWhitespace != tag.preserveWhitespace) {
            return false;
        }
        if (this.selfClosing != tag.selfClosing) {
            return false;
        }
        if (this.formList != tag.formList) {
            return false;
        }
        if (this.formSubmit != tag.formSubmit) {
            return false;
        }
        if (!this.tagName.equals(tag.tagName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8 = 1;
        int hashCode = ((this.tagName.hashCode() * 31) + (this.isBlock ? 1 : 0)) * 31;
        if (this.formatAsBlock) {
            i = 1;
        } else {
            i = 0;
        }
        int i9 = (hashCode + i) * 31;
        if (this.canContainBlock) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        int i10 = (i9 + i2) * 31;
        if (this.canContainInline) {
            i3 = 1;
        } else {
            i3 = 0;
        }
        int i11 = (i10 + i3) * 31;
        if (this.empty) {
            i4 = 1;
        } else {
            i4 = 0;
        }
        int i12 = (i11 + i4) * 31;
        if (this.selfClosing) {
            i5 = 1;
        } else {
            i5 = 0;
        }
        int i13 = (i12 + i5) * 31;
        if (this.preserveWhitespace) {
            i6 = 1;
        } else {
            i6 = 0;
        }
        int i14 = (i13 + i6) * 31;
        if (this.formList) {
            i7 = 1;
        } else {
            i7 = 0;
        }
        int i15 = (i14 + i7) * 31;
        if (!this.formSubmit) {
            i8 = 0;
        }
        return i15 + i8;
    }

    public String toString() {
        return this.tagName;
    }

    private static void register(Tag tag) {
        tags.put(tag.tagName, tag);
    }
}
