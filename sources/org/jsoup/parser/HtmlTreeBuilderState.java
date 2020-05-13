package org.jsoup.parser;

import com.szchoiceway.index.InstallShortcutReceiver;
import com.szchoiceway.index.LauncherSettings;
import java.util.Iterator;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Token;

enum HtmlTreeBuilderState {
    Initial {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
                return true;
            } else if (t.isDoctype()) {
                Token.Doctype d = t.asDoctype();
                tb.getDocument().appendChild(new DocumentType(d.getName(), d.getPublicIdentifier(), d.getSystemIdentifier(), tb.getBaseUri()));
                if (d.isForceQuirks()) {
                    tb.getDocument().quirksMode(Document.QuirksMode.quirks);
                }
                tb.transition(BeforeHtml);
                return true;
            } else {
                tb.transition(BeforeHtml);
                return tb.process(t);
            }
        }
    },
    BeforeHtml {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isDoctype()) {
                tb.error(this);
                return false;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            } else {
                if (!t.isStartTag() || !t.asStartTag().name().equals("html")) {
                    if (t.isEndTag()) {
                        if (StringUtil.in(t.asEndTag().name(), "head", "body", "html", "br")) {
                            return anythingElse(t, tb);
                        }
                    }
                    if (!t.isEndTag()) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                }
                tb.insert(t.asStartTag());
                tb.transition(BeforeHead);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.insert("html");
            tb.transition(BeforeHead);
            return tb.process(t);
        }
    },
    BeforeHead {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
                return true;
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().name().equals("html")) {
                return InBody.process(t, tb);
            } else {
                if (!t.isStartTag() || !t.asStartTag().name().equals("head")) {
                    if (t.isEndTag()) {
                        if (StringUtil.in(t.asEndTag().name(), "head", "body", "html", "br")) {
                            tb.process(new Token.StartTag("head"));
                            return tb.process(t);
                        }
                    }
                    if (t.isEndTag()) {
                        tb.error(this);
                        return false;
                    }
                    tb.process(new Token.StartTag("head"));
                    return tb.process(t);
                }
                tb.setHeadElement(tb.insert(t.asStartTag()));
                tb.transition(InHead);
                return true;
            }
        }
    },
    InHead {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
                return true;
            }
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    return true;
                case Doctype:
                    tb.error(this);
                    return false;
                case StartTag:
                    Token.StartTag start = t.asStartTag();
                    String name = start.name();
                    if (name.equals("html")) {
                        return InBody.process(t, tb);
                    }
                    if (StringUtil.in(name, "base", "basefont", "bgsound", "command", "link")) {
                        Element el = tb.insertEmpty(start);
                        if (!name.equals("base") || !el.hasAttr("href")) {
                            return true;
                        }
                        tb.maybeSetBaseUri(el);
                        return true;
                    } else if (name.equals("meta")) {
                        tb.insertEmpty(start);
                        return true;
                    } else if (name.equals(LauncherSettings.BaseLauncherColumns.TITLE)) {
                        HtmlTreeBuilderState.handleRcData(start, tb);
                        return true;
                    } else {
                        if (StringUtil.in(name, "noframes", "style")) {
                            HtmlTreeBuilderState.handleRawtext(start, tb);
                            return true;
                        } else if (name.equals("noscript")) {
                            tb.insert(start);
                            tb.transition(InHeadNoscript);
                            return true;
                        } else if (name.equals("script")) {
                            tb.tokeniser.transition(TokeniserState.ScriptData);
                            tb.markInsertionMode();
                            tb.transition(Text);
                            tb.insert(start);
                            return true;
                        } else if (!name.equals("head")) {
                            return anythingElse(t, tb);
                        } else {
                            tb.error(this);
                            return false;
                        }
                    }
                case EndTag:
                    String name2 = t.asEndTag().name();
                    if (name2.equals("head")) {
                        tb.pop();
                        tb.transition(AfterHead);
                        return true;
                    }
                    if (StringUtil.in(name2, "body", "html", "br")) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                default:
                    return anythingElse(t, tb);
            }
        }

        private boolean anythingElse(Token t, TreeBuilder tb) {
            tb.process(new Token.EndTag("head"));
            return tb.process(t);
        }
    },
    InHeadNoscript {
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0086, code lost:
            if (org.jsoup.helper.StringUtil.in(r8.asStartTag().name(), "basefont", "bgsound", "link", "meta", "noframes", "style") != false) goto L_0x0088;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x00c8, code lost:
            if (org.jsoup.helper.StringUtil.in(r8.asStartTag().name(), "head", "noscript") == false) goto L_0x00ca;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean process(org.jsoup.parser.Token r8, org.jsoup.parser.HtmlTreeBuilder r9) {
            /*
                r7 = this;
                r6 = 2
                r1 = 1
                r0 = 0
                boolean r2 = r8.isDoctype()
                if (r2 == 0) goto L_0x000e
                r9.error(r7)
            L_0x000c:
                r0 = r1
            L_0x000d:
                return r0
            L_0x000e:
                boolean r2 = r8.isStartTag()
                if (r2 == 0) goto L_0x002b
                org.jsoup.parser.Token$StartTag r2 = r8.asStartTag()
                java.lang.String r2 = r2.name()
                java.lang.String r3 = "html"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x002b
                org.jsoup.parser.HtmlTreeBuilderState r0 = InBody
                boolean r0 = r9.process(r8, r0)
                goto L_0x000d
            L_0x002b:
                boolean r2 = r8.isEndTag()
                if (r2 == 0) goto L_0x004a
                org.jsoup.parser.Token$EndTag r2 = r8.asEndTag()
                java.lang.String r2 = r2.name()
                java.lang.String r3 = "noscript"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x004a
                r9.pop()
                org.jsoup.parser.HtmlTreeBuilderState r0 = InHead
                r9.transition(r0)
                goto L_0x000c
            L_0x004a:
                boolean r2 = org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(r8)
                if (r2 != 0) goto L_0x0088
                boolean r2 = r8.isComment()
                if (r2 != 0) goto L_0x0088
                boolean r2 = r8.isStartTag()
                if (r2 == 0) goto L_0x0090
                org.jsoup.parser.Token$StartTag r2 = r8.asStartTag()
                java.lang.String r2 = r2.name()
                r3 = 6
                java.lang.String[] r3 = new java.lang.String[r3]
                java.lang.String r4 = "basefont"
                r3[r0] = r4
                java.lang.String r4 = "bgsound"
                r3[r1] = r4
                java.lang.String r4 = "link"
                r3[r6] = r4
                r4 = 3
                java.lang.String r5 = "meta"
                r3[r4] = r5
                r4 = 4
                java.lang.String r5 = "noframes"
                r3[r4] = r5
                r4 = 5
                java.lang.String r5 = "style"
                r3[r4] = r5
                boolean r2 = org.jsoup.helper.StringUtil.in(r2, r3)
                if (r2 == 0) goto L_0x0090
            L_0x0088:
                org.jsoup.parser.HtmlTreeBuilderState r0 = InHead
                boolean r0 = r9.process(r8, r0)
                goto L_0x000d
            L_0x0090:
                boolean r2 = r8.isEndTag()
                if (r2 == 0) goto L_0x00ac
                org.jsoup.parser.Token$EndTag r2 = r8.asEndTag()
                java.lang.String r2 = r2.name()
                java.lang.String r3 = "br"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x00ac
                boolean r0 = r7.anythingElse(r8, r9)
                goto L_0x000d
            L_0x00ac:
                boolean r2 = r8.isStartTag()
                if (r2 == 0) goto L_0x00ca
                org.jsoup.parser.Token$StartTag r2 = r8.asStartTag()
                java.lang.String r2 = r2.name()
                java.lang.String[] r3 = new java.lang.String[r6]
                java.lang.String r4 = "head"
                r3[r0] = r4
                java.lang.String r4 = "noscript"
                r3[r1] = r4
                boolean r1 = org.jsoup.helper.StringUtil.in(r2, r3)
                if (r1 != 0) goto L_0x00d0
            L_0x00ca:
                boolean r1 = r8.isEndTag()
                if (r1 == 0) goto L_0x00d5
            L_0x00d0:
                r9.error(r7)
                goto L_0x000d
            L_0x00d5:
                boolean r0 = r7.anythingElse(r8, r9)
                goto L_0x000d
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.HtmlTreeBuilderState.AnonymousClass5.process(org.jsoup.parser.Token, org.jsoup.parser.HtmlTreeBuilder):boolean");
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            tb.process(new Token.EndTag("noscript"));
            return tb.process(t);
        }
    },
    AfterHead {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
            } else if (t.isStartTag()) {
                Token.StartTag startTag = t.asStartTag();
                String name = startTag.name();
                if (name.equals("html")) {
                    return tb.process(t, InBody);
                }
                if (name.equals("body")) {
                    tb.insert(startTag);
                    tb.framesetOk(false);
                    tb.transition(InBody);
                } else if (name.equals("frameset")) {
                    tb.insert(startTag);
                    tb.transition(InFrameset);
                } else {
                    if (StringUtil.in(name, "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", LauncherSettings.BaseLauncherColumns.TITLE)) {
                        tb.error(this);
                        Element head = tb.getHeadElement();
                        tb.push(head);
                        tb.process(t, InHead);
                        tb.removeFromStack(head);
                    } else if (name.equals("head")) {
                        tb.error(this);
                        return false;
                    } else {
                        anythingElse(t, tb);
                    }
                }
            } else if (t.isEndTag()) {
                if (StringUtil.in(t.asEndTag().name(), "body", "html")) {
                    anythingElse(t, tb);
                } else {
                    tb.error(this);
                    return false;
                }
            } else {
                anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.process(new Token.StartTag("body"));
            tb.framesetOk(true);
            return tb.process(t);
        }
    },
    InBody {
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Removed duplicated region for block: B:372:0x0cf5  */
        /* JADX WARNING: Removed duplicated region for block: B:379:0x0d41 A[LOOP:9: B:377:0x0d3b->B:379:0x0d41, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:386:0x0d8e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean process(org.jsoup.parser.Token r43, org.jsoup.parser.HtmlTreeBuilder r44) {
            /*
                r42 = this;
                int[] r39 = org.jsoup.parser.HtmlTreeBuilderState.AnonymousClass24.$SwitchMap$org$jsoup$parser$Token$TokenType
                r0 = r43
                org.jsoup.parser.Token$TokenType r0 = r0.type
                r40 = r0
                int r40 = r40.ordinal()
                r39 = r39[r40]
                switch(r39) {
                    case 1: goto L_0x0057;
                    case 2: goto L_0x0063;
                    case 3: goto L_0x006d;
                    case 4: goto L_0x098f;
                    case 5: goto L_0x0014;
                    default: goto L_0x0011;
                }
            L_0x0011:
                r39 = 1
            L_0x0013:
                return r39
            L_0x0014:
                org.jsoup.parser.Token$Character r9 = r43.asCharacter()
                java.lang.String r39 = r9.getData()
                java.lang.String r40 = org.jsoup.parser.HtmlTreeBuilderState.nullString
                boolean r39 = r39.equals(r40)
                if (r39 == 0) goto L_0x0030
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0030:
                boolean r39 = r44.framesetOk()
                if (r39 == 0) goto L_0x0045
                boolean r39 = org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(r9)
                if (r39 == 0) goto L_0x0045
                r44.reconstructFormattingElements()
                r0 = r44
                r0.insert((org.jsoup.parser.Token.Character) r9)
                goto L_0x0011
            L_0x0045:
                r44.reconstructFormattingElements()
                r0 = r44
                r0.insert((org.jsoup.parser.Token.Character) r9)
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                goto L_0x0011
            L_0x0057:
                org.jsoup.parser.Token$Comment r39 = r43.asComment()
                r0 = r44
                r1 = r39
                r0.insert((org.jsoup.parser.Token.Comment) r1)
                goto L_0x0011
            L_0x0063:
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x006d:
                org.jsoup.parser.Token$StartTag r37 = r43.asStartTag()
                java.lang.String r26 = r37.name()
                java.lang.String r39 = "html"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x00be
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.helper.DescendableLinkedList r39 = r44.getStack()
                java.lang.Object r19 = r39.getFirst()
                org.jsoup.nodes.Element r19 = (org.jsoup.nodes.Element) r19
                org.jsoup.nodes.Attributes r39 = r37.getAttributes()
                java.util.Iterator r21 = r39.iterator()
            L_0x009a:
                boolean r39 = r21.hasNext()
                if (r39 == 0) goto L_0x0011
                java.lang.Object r7 = r21.next()
                org.jsoup.nodes.Attribute r7 = (org.jsoup.nodes.Attribute) r7
                java.lang.String r39 = r7.getKey()
                r0 = r19
                r1 = r39
                boolean r39 = r0.hasAttr(r1)
                if (r39 != 0) goto L_0x009a
                org.jsoup.nodes.Attributes r39 = r19.attributes()
                r0 = r39
                r0.put(r7)
                goto L_0x009a
            L_0x00be:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartToHead
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x00da
                org.jsoup.parser.HtmlTreeBuilderState r39 = InHead
                r0 = r44
                r1 = r43
                r2 = r39
                boolean r39 = r0.process(r1, r2)
                goto L_0x0013
            L_0x00da:
                java.lang.String r39 = "body"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0164
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.helper.DescendableLinkedList r35 = r44.getStack()
                int r39 = r35.size()
                r40 = 1
                r0 = r39
                r1 = r40
                if (r0 == r1) goto L_0x0121
                int r39 = r35.size()
                r40 = 2
                r0 = r39
                r1 = r40
                if (r0 <= r1) goto L_0x0125
                r39 = 1
                r0 = r35
                r1 = r39
                java.lang.Object r39 = r0.get(r1)
                org.jsoup.nodes.Element r39 = (org.jsoup.nodes.Element) r39
                java.lang.String r39 = r39.nodeName()
                java.lang.String r40 = "body"
                boolean r39 = r39.equals(r40)
                if (r39 != 0) goto L_0x0125
            L_0x0121:
                r39 = 0
                goto L_0x0013
            L_0x0125:
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                r39 = 1
                r0 = r35
                r1 = r39
                java.lang.Object r8 = r0.get(r1)
                org.jsoup.nodes.Element r8 = (org.jsoup.nodes.Element) r8
                org.jsoup.nodes.Attributes r39 = r37.getAttributes()
                java.util.Iterator r21 = r39.iterator()
            L_0x0142:
                boolean r39 = r21.hasNext()
                if (r39 == 0) goto L_0x0011
                java.lang.Object r7 = r21.next()
                org.jsoup.nodes.Attribute r7 = (org.jsoup.nodes.Attribute) r7
                java.lang.String r39 = r7.getKey()
                r0 = r39
                boolean r39 = r8.hasAttr(r0)
                if (r39 != 0) goto L_0x0142
                org.jsoup.nodes.Attributes r39 = r8.attributes()
                r0 = r39
                r0.put(r7)
                goto L_0x0142
            L_0x0164:
                java.lang.String r39 = "frameset"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x01f0
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.helper.DescendableLinkedList r35 = r44.getStack()
                int r39 = r35.size()
                r40 = 1
                r0 = r39
                r1 = r40
                if (r0 == r1) goto L_0x01ab
                int r39 = r35.size()
                r40 = 2
                r0 = r39
                r1 = r40
                if (r0 <= r1) goto L_0x01af
                r39 = 1
                r0 = r35
                r1 = r39
                java.lang.Object r39 = r0.get(r1)
                org.jsoup.nodes.Element r39 = (org.jsoup.nodes.Element) r39
                java.lang.String r39 = r39.nodeName()
                java.lang.String r40 = "body"
                boolean r39 = r39.equals(r40)
                if (r39 != 0) goto L_0x01af
            L_0x01ab:
                r39 = 0
                goto L_0x0013
            L_0x01af:
                boolean r39 = r44.framesetOk()
                if (r39 != 0) goto L_0x01b9
                r39 = 0
                goto L_0x0013
            L_0x01b9:
                r39 = 1
                r0 = r35
                r1 = r39
                java.lang.Object r32 = r0.get(r1)
                org.jsoup.nodes.Element r32 = (org.jsoup.nodes.Element) r32
                org.jsoup.nodes.Element r39 = r32.parent()
                if (r39 == 0) goto L_0x01ce
                r32.remove()
            L_0x01ce:
                int r39 = r35.size()
                r40 = 1
                r0 = r39
                r1 = r40
                if (r0 <= r1) goto L_0x01de
                r35.removeLast()
                goto L_0x01ce
            L_0x01de:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                org.jsoup.parser.HtmlTreeBuilderState r39 = InFrameset
                r0 = r44
                r1 = r39
                r0.transition(r1)
                goto L_0x0011
            L_0x01f0:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartPClosers
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0221
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x0218
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x0218:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                goto L_0x0011
            L_0x0221:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x026e
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x0249
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x0249:
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                java.lang.String[] r40 = org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings
                boolean r39 = org.jsoup.helper.StringUtil.in(r39, r40)
                if (r39 == 0) goto L_0x0265
                r0 = r44
                r1 = r42
                r0.error(r1)
                r44.pop()
            L_0x0265:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                goto L_0x0011
            L_0x026e:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartPreListing
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x02a8
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x0296
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x0296:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                goto L_0x0011
            L_0x02a8:
                java.lang.String r39 = "form"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x02ec
                org.jsoup.nodes.FormElement r39 = r44.getFormElement()
                if (r39 == 0) goto L_0x02c5
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x02c5:
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x02df
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x02df:
                r39 = 1
                r0 = r44
                r1 = r37
                r2 = r39
                r0.insertForm(r1, r2)
                goto L_0x0011
            L_0x02ec:
                java.lang.String r39 = "li"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x036d
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                org.jsoup.helper.DescendableLinkedList r35 = r44.getStack()
                int r39 = r35.size()
                int r20 = r39 + -1
            L_0x030b:
                if (r20 <= 0) goto L_0x0331
                r0 = r35
                r1 = r20
                java.lang.Object r14 = r0.get(r1)
                org.jsoup.nodes.Element r14 = (org.jsoup.nodes.Element) r14
                java.lang.String r39 = r14.nodeName()
                java.lang.String r40 = "li"
                boolean r39 = r39.equals(r40)
                if (r39 == 0) goto L_0x0354
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "li"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x0331:
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x034b
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x034b:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                goto L_0x0011
            L_0x0354:
                r0 = r44
                boolean r39 = r0.isSpecial(r14)
                if (r39 == 0) goto L_0x036a
                java.lang.String r39 = r14.nodeName()
                java.lang.String[] r40 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartLiBreakers
                boolean r39 = org.jsoup.helper.StringUtil.in(r39, r40)
                if (r39 == 0) goto L_0x0331
            L_0x036a:
                int r20 = r20 + -1
                goto L_0x030b
            L_0x036d:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.DdDt
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x03f4
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                org.jsoup.helper.DescendableLinkedList r35 = r44.getStack()
                int r39 = r35.size()
                int r20 = r39 + -1
            L_0x038e:
                if (r20 <= 0) goto L_0x03b8
                r0 = r35
                r1 = r20
                java.lang.Object r14 = r0.get(r1)
                org.jsoup.nodes.Element r14 = (org.jsoup.nodes.Element) r14
                java.lang.String r39 = r14.nodeName()
                java.lang.String[] r40 = org.jsoup.parser.HtmlTreeBuilderState.Constants.DdDt
                boolean r39 = org.jsoup.helper.StringUtil.in(r39, r40)
                if (r39 == 0) goto L_0x03db
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = r14.nodeName()
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x03b8:
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x03d2
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x03d2:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                goto L_0x0011
            L_0x03db:
                r0 = r44
                boolean r39 = r0.isSpecial(r14)
                if (r39 == 0) goto L_0x03f1
                java.lang.String r39 = r14.nodeName()
                java.lang.String[] r40 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartLiBreakers
                boolean r39 = org.jsoup.helper.StringUtil.in(r39, r40)
                if (r39 == 0) goto L_0x03b8
            L_0x03f1:
                int r20 = r20 + -1
                goto L_0x038e
            L_0x03f4:
                java.lang.String r39 = "plaintext"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x042e
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x041a
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x041a:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r0 = r44
                org.jsoup.parser.Tokeniser r0 = r0.tokeniser
                r39 = r0
                org.jsoup.parser.TokeniserState r40 = org.jsoup.parser.TokeniserState.PLAINTEXT
                r39.transition(r40)
                goto L_0x0011
            L_0x042e:
                java.lang.String r39 = "button"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0479
                java.lang.String r39 = "button"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x0464
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "button"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                r0 = r44
                r1 = r37
                r0.process(r1)
                goto L_0x0011
            L_0x0464:
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                goto L_0x0011
            L_0x0479:
                java.lang.String r39 = "a"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x04d2
                java.lang.String r39 = "a"
                r0 = r44
                r1 = r39
                org.jsoup.nodes.Element r39 = r0.getActiveFormattingElement(r1)
                if (r39 == 0) goto L_0x04c0
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "a"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                java.lang.String r39 = "a"
                r0 = r44
                r1 = r39
                org.jsoup.nodes.Element r30 = r0.getFromStack(r1)
                if (r30 == 0) goto L_0x04c0
                r0 = r44
                r1 = r30
                r0.removeFromActiveFormattingElements(r1)
                r0 = r44
                r1 = r30
                r0.removeFromStack(r1)
            L_0x04c0:
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                org.jsoup.nodes.Element r3 = r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r0 = r44
                r0.pushActiveFormattingElements(r3)
                goto L_0x0011
            L_0x04d2:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.Formatters
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x04f2
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                org.jsoup.nodes.Element r14 = r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r0 = r44
                r0.pushActiveFormattingElements(r14)
                goto L_0x0011
            L_0x04f2:
                java.lang.String r39 = "nobr"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0534
                r44.reconstructFormattingElements()
                java.lang.String r39 = "nobr"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 == 0) goto L_0x0525
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "nobr"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                r44.reconstructFormattingElements()
            L_0x0525:
                r0 = r44
                r1 = r37
                org.jsoup.nodes.Element r14 = r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r0 = r44
                r0.pushActiveFormattingElements(r14)
                goto L_0x0011
            L_0x0534:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartApplets
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x055a
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r44.insertMarkerToFormattingElements()
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                goto L_0x0011
            L_0x055a:
                java.lang.String r39 = "table"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x05ab
                org.jsoup.nodes.Document r39 = r44.getDocument()
                org.jsoup.nodes.Document$QuirksMode r39 = r39.quirksMode()
                org.jsoup.nodes.Document$QuirksMode r40 = org.jsoup.nodes.Document.QuirksMode.quirks
                r0 = r39
                r1 = r40
                if (r0 == r1) goto L_0x0590
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x0590
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x0590:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                org.jsoup.parser.HtmlTreeBuilderState r39 = InTable
                r0 = r44
                r1 = r39
                r0.transition(r1)
                goto L_0x0011
            L_0x05ab:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartEmptyFormatters
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x05ce
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                r0.insertEmpty(r1)
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                goto L_0x0011
            L_0x05ce:
                java.lang.String r39 = "input"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0600
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                org.jsoup.nodes.Element r14 = r0.insertEmpty(r1)
                java.lang.String r39 = "type"
                r0 = r39
                java.lang.String r39 = r14.attr(r0)
                java.lang.String r40 = "hidden"
                boolean r39 = r39.equalsIgnoreCase(r40)
                if (r39 != 0) goto L_0x0011
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                goto L_0x0011
            L_0x0600:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartMedia
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0617
                r0 = r44
                r1 = r37
                r0.insertEmpty(r1)
                goto L_0x0011
            L_0x0617:
                java.lang.String r39 = "hr"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x064f
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x063d
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x063d:
                r0 = r44
                r1 = r37
                r0.insertEmpty(r1)
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                goto L_0x0011
            L_0x064f:
                java.lang.String r39 = "image"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0684
                java.lang.String r39 = "svg"
                r0 = r44
                r1 = r39
                org.jsoup.nodes.Element r39 = r0.getFromStack(r1)
                if (r39 != 0) goto L_0x067b
                java.lang.String r39 = "img"
                r0 = r37
                r1 = r39
                org.jsoup.parser.Token$Tag r39 = r0.name(r1)
                r0 = r44
                r1 = r39
                boolean r39 = r0.process(r1)
                goto L_0x0013
            L_0x067b:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                goto L_0x0011
            L_0x0684:
                java.lang.String r39 = "isindex"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x07a6
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.nodes.FormElement r39 = r44.getFormElement()
                if (r39 == 0) goto L_0x06a1
                r39 = 0
                goto L_0x0013
            L_0x06a1:
                r0 = r44
                org.jsoup.parser.Tokeniser r0 = r0.tokeniser
                r39 = r0
                r39.acknowledgeSelfClosingFlag()
                org.jsoup.parser.Token$StartTag r39 = new org.jsoup.parser.Token$StartTag
                java.lang.String r40 = "form"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                r0 = r37
                org.jsoup.nodes.Attributes r0 = r0.attributes
                r39 = r0
                java.lang.String r40 = "action"
                boolean r39 = r39.hasKey(r40)
                if (r39 == 0) goto L_0x06e1
                org.jsoup.nodes.FormElement r16 = r44.getFormElement()
                java.lang.String r39 = "action"
                r0 = r37
                org.jsoup.nodes.Attributes r0 = r0.attributes
                r40 = r0
                java.lang.String r41 = "action"
                java.lang.String r40 = r40.get(r41)
                r0 = r16
                r1 = r39
                r2 = r40
                r0.attr((java.lang.String) r1, (java.lang.String) r2)
            L_0x06e1:
                org.jsoup.parser.Token$StartTag r39 = new org.jsoup.parser.Token$StartTag
                java.lang.String r40 = "hr"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                org.jsoup.parser.Token$StartTag r39 = new org.jsoup.parser.Token$StartTag
                java.lang.String r40 = "label"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                r0 = r37
                org.jsoup.nodes.Attributes r0 = r0.attributes
                r39 = r0
                java.lang.String r40 = "prompt"
                boolean r39 = r39.hasKey(r40)
                if (r39 == 0) goto L_0x0756
                r0 = r37
                org.jsoup.nodes.Attributes r0 = r0.attributes
                r39 = r0
                java.lang.String r40 = "prompt"
                java.lang.String r29 = r39.get(r40)
            L_0x0717:
                org.jsoup.parser.Token$Character r39 = new org.jsoup.parser.Token$Character
                r0 = r39
                r1 = r29
                r0.<init>(r1)
                r0 = r44
                r1 = r39
                r0.process(r1)
                org.jsoup.nodes.Attributes r22 = new org.jsoup.nodes.Attributes
                r22.<init>()
                r0 = r37
                org.jsoup.nodes.Attributes r0 = r0.attributes
                r39 = r0
                java.util.Iterator r21 = r39.iterator()
            L_0x0736:
                boolean r39 = r21.hasNext()
                if (r39 == 0) goto L_0x0759
                java.lang.Object r6 = r21.next()
                org.jsoup.nodes.Attribute r6 = (org.jsoup.nodes.Attribute) r6
                java.lang.String r39 = r6.getKey()
                java.lang.String[] r40 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartInputAttribs
                boolean r39 = org.jsoup.helper.StringUtil.in(r39, r40)
                if (r39 != 0) goto L_0x0736
                r0 = r22
                r0.put(r6)
                goto L_0x0736
            L_0x0756:
                java.lang.String r29 = "This is a searchable index. Enter search keywords: "
                goto L_0x0717
            L_0x0759:
                java.lang.String r39 = "name"
                java.lang.String r40 = "isindex"
                r0 = r22
                r1 = r39
                r2 = r40
                r0.put(r1, r2)
                org.jsoup.parser.Token$StartTag r39 = new org.jsoup.parser.Token$StartTag
                java.lang.String r40 = "input"
                r0 = r39
                r1 = r40
                r2 = r22
                r0.<init>(r1, r2)
                r0 = r44
                r1 = r39
                r0.process(r1)
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "label"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                org.jsoup.parser.Token$StartTag r39 = new org.jsoup.parser.Token$StartTag
                java.lang.String r40 = "hr"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "form"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                goto L_0x0011
            L_0x07a6:
                java.lang.String r39 = "textarea"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x07db
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r0 = r44
                org.jsoup.parser.Tokeniser r0 = r0.tokeniser
                r39 = r0
                org.jsoup.parser.TokeniserState r40 = org.jsoup.parser.TokeniserState.Rcdata
                r39.transition(r40)
                r44.markInsertionMode()
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                org.jsoup.parser.HtmlTreeBuilderState r39 = Text
                r0 = r44
                r1 = r39
                r0.transition(r1)
                goto L_0x0011
            L_0x07db:
                java.lang.String r39 = "xmp"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0816
                java.lang.String r39 = "p"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inButtonScope(r1)
                if (r39 == 0) goto L_0x0801
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "p"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x0801:
                r44.reconstructFormattingElements()
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                r0 = r37
                r1 = r44
                org.jsoup.parser.HtmlTreeBuilderState.handleRawtext(r0, r1)
                goto L_0x0011
            L_0x0816:
                java.lang.String r39 = "iframe"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0834
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                r0 = r37
                r1 = r44
                org.jsoup.parser.HtmlTreeBuilderState.handleRawtext(r0, r1)
                goto L_0x0011
            L_0x0834:
                java.lang.String r39 = "noembed"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0849
                r0 = r37
                r1 = r44
                org.jsoup.parser.HtmlTreeBuilderState.handleRawtext(r0, r1)
                goto L_0x0011
            L_0x0849:
                java.lang.String r39 = "select"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x08aa
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r39 = 0
                r0 = r44
                r1 = r39
                r0.framesetOk(r1)
                org.jsoup.parser.HtmlTreeBuilderState r38 = r44.state()
                org.jsoup.parser.HtmlTreeBuilderState r39 = InTable
                boolean r39 = r38.equals(r39)
                if (r39 != 0) goto L_0x0894
                org.jsoup.parser.HtmlTreeBuilderState r39 = InCaption
                boolean r39 = r38.equals(r39)
                if (r39 != 0) goto L_0x0894
                org.jsoup.parser.HtmlTreeBuilderState r39 = InTableBody
                boolean r39 = r38.equals(r39)
                if (r39 != 0) goto L_0x0894
                org.jsoup.parser.HtmlTreeBuilderState r39 = InRow
                boolean r39 = r38.equals(r39)
                if (r39 != 0) goto L_0x0894
                org.jsoup.parser.HtmlTreeBuilderState r39 = InCell
                boolean r39 = r38.equals(r39)
                if (r39 == 0) goto L_0x089f
            L_0x0894:
                org.jsoup.parser.HtmlTreeBuilderState r39 = InSelectInTable
                r0 = r44
                r1 = r39
                r0.transition(r1)
                goto L_0x0011
            L_0x089f:
                org.jsoup.parser.HtmlTreeBuilderState r39 = InSelect
                r0 = r44
                r1 = r39
                r0.transition(r1)
                goto L_0x0011
            L_0x08aa:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartOptions
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x08e2
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                java.lang.String r40 = "option"
                boolean r39 = r39.equals(r40)
                if (r39 == 0) goto L_0x08d6
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "option"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
            L_0x08d6:
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                goto L_0x0011
            L_0x08e2:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartRuby
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0928
                java.lang.String r39 = "ruby"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 == 0) goto L_0x0011
                r44.generateImpliedEndTags()
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                java.lang.String r40 = "ruby"
                boolean r39 = r39.equals(r40)
                if (r39 != 0) goto L_0x091f
                r0 = r44
                r1 = r42
                r0.error(r1)
                java.lang.String r39 = "ruby"
                r0 = r44
                r1 = r39
                r0.popStackToBefore(r1)
            L_0x091f:
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                goto L_0x0011
            L_0x0928:
                java.lang.String r39 = "math"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0949
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r0 = r44
                org.jsoup.parser.Tokeniser r0 = r0.tokeniser
                r39 = r0
                r39.acknowledgeSelfClosingFlag()
                goto L_0x0011
            L_0x0949:
                java.lang.String r39 = "svg"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x096a
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                r0 = r44
                org.jsoup.parser.Tokeniser r0 = r0.tokeniser
                r39 = r0
                r39.acknowledgeSelfClosingFlag()
                goto L_0x0011
            L_0x096a:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartDrop
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0983
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0983:
                r44.reconstructFormattingElements()
                r0 = r44
                r1 = r37
                r0.insert((org.jsoup.parser.Token.StartTag) r1)
                goto L_0x0011
            L_0x098f:
                org.jsoup.parser.Token$EndTag r15 = r43.asEndTag()
                java.lang.String r26 = r15.name()
                java.lang.String r39 = "body"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x09c5
                java.lang.String r39 = "body"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 != 0) goto L_0x09ba
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x09ba:
                org.jsoup.parser.HtmlTreeBuilderState r39 = AfterBody
                r0 = r44
                r1 = r39
                r0.transition(r1)
                goto L_0x0011
            L_0x09c5:
                java.lang.String r39 = "html"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x09ea
                org.jsoup.parser.Token$EndTag r39 = new org.jsoup.parser.Token$EndTag
                java.lang.String r40 = "body"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                boolean r28 = r0.process(r1)
                if (r28 == 0) goto L_0x0011
                r0 = r44
                boolean r39 = r0.process(r15)
                goto L_0x0013
            L_0x09ea:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyEndClosers
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0a32
                r0 = r44
                r1 = r26
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 != 0) goto L_0x0a0d
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0a0d:
                r44.generateImpliedEndTags()
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                r0 = r39
                r1 = r26
                boolean r39 = r0.equals(r1)
                if (r39 != 0) goto L_0x0a29
                r0 = r44
                r1 = r42
                r0.error(r1)
            L_0x0a29:
                r0 = r44
                r1 = r26
                r0.popStackToClose((java.lang.String) r1)
                goto L_0x0011
            L_0x0a32:
                java.lang.String r39 = "form"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0a85
                org.jsoup.nodes.FormElement r13 = r44.getFormElement()
                r39 = 0
                r0 = r44
                r1 = r39
                r0.setFormElement(r1)
                if (r13 == 0) goto L_0x0a57
                r0 = r44
                r1 = r26
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 != 0) goto L_0x0a62
            L_0x0a57:
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0a62:
                r44.generateImpliedEndTags()
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                r0 = r39
                r1 = r26
                boolean r39 = r0.equals(r1)
                if (r39 != 0) goto L_0x0a7e
                r0 = r44
                r1 = r42
                r0.error(r1)
            L_0x0a7e:
                r0 = r44
                r0.removeFromStack(r13)
                goto L_0x0011
            L_0x0a85:
                java.lang.String r39 = "p"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0ae3
                r0 = r44
                r1 = r26
                boolean r39 = r0.inButtonScope(r1)
                if (r39 != 0) goto L_0x0aba
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.parser.Token$StartTag r39 = new org.jsoup.parser.Token$StartTag
                r0 = r39
                r1 = r26
                r0.<init>(r1)
                r0 = r44
                r1 = r39
                r0.process(r1)
                r0 = r44
                boolean r39 = r0.process(r15)
                goto L_0x0013
            L_0x0aba:
                r0 = r44
                r1 = r26
                r0.generateImpliedEndTags(r1)
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                r0 = r39
                r1 = r26
                boolean r39 = r0.equals(r1)
                if (r39 != 0) goto L_0x0ada
                r0 = r44
                r1 = r42
                r0.error(r1)
            L_0x0ada:
                r0 = r44
                r1 = r26
                r0.popStackToClose((java.lang.String) r1)
                goto L_0x0011
            L_0x0ae3:
                java.lang.String r39 = "li"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0b2d
                r0 = r44
                r1 = r26
                boolean r39 = r0.inListItemScope(r1)
                if (r39 != 0) goto L_0x0b04
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0b04:
                r0 = r44
                r1 = r26
                r0.generateImpliedEndTags(r1)
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                r0 = r39
                r1 = r26
                boolean r39 = r0.equals(r1)
                if (r39 != 0) goto L_0x0b24
                r0 = r44
                r1 = r42
                r0.error(r1)
            L_0x0b24:
                r0 = r44
                r1 = r26
                r0.popStackToClose((java.lang.String) r1)
                goto L_0x0011
            L_0x0b2d:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.DdDt
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0b79
                r0 = r44
                r1 = r26
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 != 0) goto L_0x0b50
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0b50:
                r0 = r44
                r1 = r26
                r0.generateImpliedEndTags(r1)
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                r0 = r39
                r1 = r26
                boolean r39 = r0.equals(r1)
                if (r39 != 0) goto L_0x0b70
                r0 = r44
                r1 = r42
                r0.error(r1)
            L_0x0b70:
                r0 = r44
                r1 = r26
                r0.popStackToClose((java.lang.String) r1)
                goto L_0x0011
            L_0x0b79:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0bcd
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings
                r0 = r44
                r1 = r39
                boolean r39 = r0.inScope((java.lang.String[]) r1)
                if (r39 != 0) goto L_0x0ba0
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0ba0:
                r0 = r44
                r1 = r26
                r0.generateImpliedEndTags(r1)
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                r0 = r39
                r1 = r26
                boolean r39 = r0.equals(r1)
                if (r39 != 0) goto L_0x0bc0
                r0 = r44
                r1 = r42
                r0.error(r1)
            L_0x0bc0:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings
                r0 = r44
                r1 = r39
                r0.popStackToClose((java.lang.String[]) r1)
                goto L_0x0011
            L_0x0bcd:
                java.lang.String r39 = "sarcasm"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0bdf
                boolean r39 = r42.anyOtherEndTag(r43, r44)
                goto L_0x0013
            L_0x0bdf:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyEndAdoptionFormatters
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0dbc
                r20 = 0
            L_0x0bef:
                r39 = 8
                r0 = r20
                r1 = r39
                if (r0 >= r1) goto L_0x0011
                r0 = r44
                r1 = r26
                org.jsoup.nodes.Element r17 = r0.getActiveFormattingElement(r1)
                if (r17 != 0) goto L_0x0c07
                boolean r39 = r42.anyOtherEndTag(r43, r44)
                goto L_0x0013
            L_0x0c07:
                r0 = r44
                r1 = r17
                boolean r39 = r0.onStack(r1)
                if (r39 != 0) goto L_0x0c23
                r0 = r44
                r1 = r42
                r0.error(r1)
                r0 = r44
                r1 = r17
                r0.removeFromActiveFormattingElements(r1)
                r39 = 1
                goto L_0x0013
            L_0x0c23:
                java.lang.String r39 = r17.nodeName()
                r0 = r44
                r1 = r39
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 != 0) goto L_0x0c3c
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0c3c:
                org.jsoup.nodes.Element r39 = r44.currentElement()
                r0 = r39
                r1 = r17
                if (r0 == r1) goto L_0x0c4d
                r0 = r44
                r1 = r42
                r0.error(r1)
            L_0x0c4d:
                r18 = 0
                r12 = 0
                r33 = 0
                org.jsoup.helper.DescendableLinkedList r35 = r44.getStack()
                int r36 = r35.size()
                r34 = 0
            L_0x0c5c:
                r0 = r34
                r1 = r36
                if (r0 >= r1) goto L_0x0c95
                r39 = 64
                r0 = r34
                r1 = r39
                if (r0 >= r1) goto L_0x0c95
                r0 = r35
                r1 = r34
                java.lang.Object r14 = r0.get(r1)
                org.jsoup.nodes.Element r14 = (org.jsoup.nodes.Element) r14
                r0 = r17
                if (r14 != r0) goto L_0x0c89
                int r39 = r34 + -1
                r0 = r35
                r1 = r39
                java.lang.Object r12 = r0.get(r1)
                org.jsoup.nodes.Element r12 = (org.jsoup.nodes.Element) r12
                r33 = 1
            L_0x0c86:
                int r34 = r34 + 1
                goto L_0x0c5c
            L_0x0c89:
                if (r33 == 0) goto L_0x0c86
                r0 = r44
                boolean r39 = r0.isSpecial(r14)
                if (r39 == 0) goto L_0x0c86
                r18 = r14
            L_0x0c95:
                if (r18 != 0) goto L_0x0cad
                java.lang.String r39 = r17.nodeName()
                r0 = r44
                r1 = r39
                r0.popStackToClose((java.lang.String) r1)
                r0 = r44
                r1 = r17
                r0.removeFromActiveFormattingElements(r1)
                r39 = 1
                goto L_0x0013
            L_0x0cad:
                r27 = r18
                r24 = r18
                r23 = 0
            L_0x0cb3:
                r39 = 3
                r0 = r23
                r1 = r39
                if (r0 >= r1) goto L_0x0ce7
                r0 = r44
                r1 = r27
                boolean r39 = r0.onStack(r1)
                if (r39 == 0) goto L_0x0ccd
                r0 = r44
                r1 = r27
                org.jsoup.nodes.Element r27 = r0.aboveOnStack(r1)
            L_0x0ccd:
                r0 = r44
                r1 = r27
                boolean r39 = r0.isInActiveFormattingElements(r1)
                if (r39 != 0) goto L_0x0ce1
                r0 = r44
                r1 = r27
                r0.removeFromStack(r1)
            L_0x0cde:
                int r23 = r23 + 1
                goto L_0x0cb3
            L_0x0ce1:
                r0 = r27
                r1 = r17
                if (r0 != r1) goto L_0x0d49
            L_0x0ce7:
                java.lang.String r39 = r12.nodeName()
                java.lang.String[] r40 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyEndTableFosters
                boolean r39 = org.jsoup.helper.StringUtil.in(r39, r40)
                if (r39 == 0) goto L_0x0d8e
                org.jsoup.nodes.Element r39 = r24.parent()
                if (r39 == 0) goto L_0x0cfe
                r24.remove()
            L_0x0cfe:
                r0 = r44
                r1 = r24
                r0.insertInFosterParent(r1)
            L_0x0d05:
                org.jsoup.nodes.Element r4 = new org.jsoup.nodes.Element
                org.jsoup.parser.Tag r39 = r17.tag()
                java.lang.String r40 = r44.getBaseUri()
                r0 = r39
                r1 = r40
                r4.<init>(r0, r1)
                org.jsoup.nodes.Attributes r39 = r4.attributes()
                org.jsoup.nodes.Attributes r40 = r17.attributes()
                r39.addAll(r40)
                java.util.List r39 = r18.childNodes()
                int r40 = r18.childNodeSize()
                r0 = r40
                org.jsoup.nodes.Node[] r0 = new org.jsoup.nodes.Node[r0]
                r40 = r0
                java.lang.Object[] r11 = r39.toArray(r40)
                org.jsoup.nodes.Node[] r11 = (org.jsoup.nodes.Node[]) r11
                r5 = r11
                int r0 = r5.length
                r25 = r0
                r21 = 0
            L_0x0d3b:
                r0 = r21
                r1 = r25
                if (r0 >= r1) goto L_0x0d9e
                r10 = r5[r21]
                r4.appendChild(r10)
                int r21 = r21 + 1
                goto L_0x0d3b
            L_0x0d49:
                org.jsoup.nodes.Element r31 = new org.jsoup.nodes.Element
                java.lang.String r39 = r27.nodeName()
                org.jsoup.parser.Tag r39 = org.jsoup.parser.Tag.valueOf(r39)
                java.lang.String r40 = r44.getBaseUri()
                r0 = r31
                r1 = r39
                r2 = r40
                r0.<init>(r1, r2)
                r0 = r44
                r1 = r27
                r2 = r31
                r0.replaceActiveFormattingElement(r1, r2)
                r0 = r44
                r1 = r27
                r2 = r31
                r0.replaceOnStack(r1, r2)
                r27 = r31
                r0 = r24
                r1 = r18
                if (r0 != r1) goto L_0x0d7a
            L_0x0d7a:
                org.jsoup.nodes.Element r39 = r24.parent()
                if (r39 == 0) goto L_0x0d83
                r24.remove()
            L_0x0d83:
                r0 = r27
                r1 = r24
                r0.appendChild(r1)
                r24 = r27
                goto L_0x0cde
            L_0x0d8e:
                org.jsoup.nodes.Element r39 = r24.parent()
                if (r39 == 0) goto L_0x0d97
                r24.remove()
            L_0x0d97:
                r0 = r24
                r12.appendChild(r0)
                goto L_0x0d05
            L_0x0d9e:
                r0 = r18
                r0.appendChild(r4)
                r0 = r44
                r1 = r17
                r0.removeFromActiveFormattingElements(r1)
                r0 = r44
                r1 = r17
                r0.removeFromStack(r1)
                r0 = r44
                r1 = r18
                r0.insertOnStackAfter(r1, r4)
                int r20 = r20 + 1
                goto L_0x0bef
            L_0x0dbc:
                java.lang.String[] r39 = org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartApplets
                r0 = r26
                r1 = r39
                boolean r39 = org.jsoup.helper.StringUtil.in(r0, r1)
                if (r39 == 0) goto L_0x0e13
                java.lang.String r39 = "name"
                r0 = r44
                r1 = r39
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 != 0) goto L_0x0011
                r0 = r44
                r1 = r26
                boolean r39 = r0.inScope((java.lang.String) r1)
                if (r39 != 0) goto L_0x0deb
                r0 = r44
                r1 = r42
                r0.error(r1)
                r39 = 0
                goto L_0x0013
            L_0x0deb:
                r44.generateImpliedEndTags()
                org.jsoup.nodes.Element r39 = r44.currentElement()
                java.lang.String r39 = r39.nodeName()
                r0 = r39
                r1 = r26
                boolean r39 = r0.equals(r1)
                if (r39 != 0) goto L_0x0e07
                r0 = r44
                r1 = r42
                r0.error(r1)
            L_0x0e07:
                r0 = r44
                r1 = r26
                r0.popStackToClose((java.lang.String) r1)
                r44.clearFormattingElementsToLastMarker()
                goto L_0x0011
            L_0x0e13:
                java.lang.String r39 = "br"
                r0 = r26
                r1 = r39
                boolean r39 = r0.equals(r1)
                if (r39 == 0) goto L_0x0e38
                r0 = r44
                r1 = r42
                r0.error(r1)
                org.jsoup.parser.Token$StartTag r39 = new org.jsoup.parser.Token$StartTag
                java.lang.String r40 = "br"
                r39.<init>(r40)
                r0 = r44
                r1 = r39
                r0.process(r1)
                r39 = 0
                goto L_0x0013
            L_0x0e38:
                boolean r39 = r42.anyOtherEndTag(r43, r44)
                goto L_0x0013
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.HtmlTreeBuilderState.AnonymousClass7.process(org.jsoup.parser.Token, org.jsoup.parser.HtmlTreeBuilder):boolean");
        }

        /* access modifiers changed from: package-private */
        public boolean anyOtherEndTag(Token t, HtmlTreeBuilder tb) {
            Element node;
            String name = t.asEndTag().name();
            Iterator<Element> it = tb.getStack().descendingIterator();
            do {
                if (it.hasNext()) {
                    node = it.next();
                    if (node.nodeName().equals(name)) {
                        tb.generateImpliedEndTags(name);
                        if (!name.equals(tb.currentElement().nodeName())) {
                            tb.error(this);
                        }
                        tb.popStackToClose(name);
                    }
                }
                return true;
            } while (!tb.isSpecial(node));
            tb.error(this);
            return false;
        }
    },
    Text {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isCharacter()) {
                tb.insert(t.asCharacter());
            } else if (t.isEOF()) {
                tb.error(this);
                tb.pop();
                tb.transition(tb.originalState());
                return tb.process(t);
            } else if (t.isEndTag()) {
                tb.pop();
                tb.transition(tb.originalState());
            }
            return true;
        }
    },
    InTable {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isCharacter()) {
                tb.newPendingTableCharacters();
                tb.markInsertionMode();
                tb.transition(InTableText);
                return tb.process(t);
            } else if (t.isComment()) {
                tb.insert(t.asComment());
                return true;
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag()) {
                Token.StartTag startTag = t.asStartTag();
                String name = startTag.name();
                if (name.equals("caption")) {
                    tb.clearStackToTableContext();
                    tb.insertMarkerToFormattingElements();
                    tb.insert(startTag);
                    tb.transition(InCaption);
                    return true;
                } else if (name.equals("colgroup")) {
                    tb.clearStackToTableContext();
                    tb.insert(startTag);
                    tb.transition(InColumnGroup);
                    return true;
                } else if (name.equals("col")) {
                    tb.process(new Token.StartTag("colgroup"));
                    return tb.process(t);
                } else {
                    if (StringUtil.in(name, "tbody", "tfoot", "thead")) {
                        tb.clearStackToTableContext();
                        tb.insert(startTag);
                        tb.transition(InTableBody);
                        return true;
                    }
                    if (StringUtil.in(name, "td", "th", "tr")) {
                        tb.process(new Token.StartTag("tbody"));
                        return tb.process(t);
                    } else if (name.equals("table")) {
                        tb.error(this);
                        if (tb.process(new Token.EndTag("table"))) {
                            return tb.process(t);
                        }
                        return true;
                    } else {
                        if (StringUtil.in(name, "style", "script")) {
                            return tb.process(t, InHead);
                        }
                        if (name.equals("input")) {
                            if (!startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
                                return anythingElse(t, tb);
                            }
                            tb.insertEmpty(startTag);
                            return true;
                        } else if (!name.equals("form")) {
                            return anythingElse(t, tb);
                        } else {
                            tb.error(this);
                            if (tb.getFormElement() != null) {
                                return false;
                            }
                            tb.insertForm(startTag, false);
                            return true;
                        }
                    }
                }
            } else if (t.isEndTag()) {
                String name2 = t.asEndTag().name();
                if (!name2.equals("table")) {
                    if (!StringUtil.in(name2, "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                } else if (!tb.inTableScope(name2)) {
                    tb.error(this);
                    return false;
                } else {
                    tb.popStackToClose("table");
                    tb.resetInsertionMode();
                    return true;
                }
            } else if (!t.isEOF()) {
                return anythingElse(t, tb);
            } else {
                if (!tb.currentElement().nodeName().equals("html")) {
                    return true;
                }
                tb.error(this);
                return true;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            if (!StringUtil.in(tb.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
                return tb.process(t, InBody);
            }
            tb.setFosterInserts(true);
            boolean processed = tb.process(t, InBody);
            tb.setFosterInserts(false);
            return processed;
        }
    },
    InTableText {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            switch (AnonymousClass24.$SwitchMap$org$jsoup$parser$Token$TokenType[t.type.ordinal()]) {
                case 5:
                    Token.Character c = t.asCharacter();
                    if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
                        tb.error(this);
                        return false;
                    }
                    tb.getPendingTableCharacters().add(c);
                    return true;
                default:
                    if (tb.getPendingTableCharacters().size() > 0) {
                        for (Token.Character character : tb.getPendingTableCharacters()) {
                            if (!HtmlTreeBuilderState.isWhitespace(character)) {
                                tb.error(this);
                                if (StringUtil.in(tb.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
                                    tb.setFosterInserts(true);
                                    tb.process(character, InBody);
                                    tb.setFosterInserts(false);
                                } else {
                                    tb.process(character, InBody);
                                }
                            } else {
                                tb.insert(character);
                            }
                        }
                        tb.newPendingTableCharacters();
                    }
                    tb.transition(tb.originalState());
                    return tb.process(t);
            }
        }
    },
    InCaption {
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0091, code lost:
            if (org.jsoup.helper.StringUtil.in(r13.asStartTag().name(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr") == false) goto L_0x0093;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean process(org.jsoup.parser.Token r13, org.jsoup.parser.HtmlTreeBuilder r14) {
            /*
                r12 = this;
                r11 = 4
                r10 = 3
                r9 = 2
                r4 = 1
                r3 = 0
                boolean r5 = r13.isEndTag()
                if (r5 == 0) goto L_0x0052
                org.jsoup.parser.Token$EndTag r5 = r13.asEndTag()
                java.lang.String r5 = r5.name()
                java.lang.String r6 = "caption"
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x0052
                org.jsoup.parser.Token$EndTag r0 = r13.asEndTag()
                java.lang.String r1 = r0.name()
                boolean r5 = r14.inTableScope(r1)
                if (r5 != 0) goto L_0x002d
                r14.error(r12)
            L_0x002c:
                return r3
            L_0x002d:
                r14.generateImpliedEndTags()
                org.jsoup.nodes.Element r3 = r14.currentElement()
                java.lang.String r3 = r3.nodeName()
                java.lang.String r5 = "caption"
                boolean r3 = r3.equals(r5)
                if (r3 != 0) goto L_0x0043
                r14.error(r12)
            L_0x0043:
                java.lang.String r3 = "caption"
                r14.popStackToClose((java.lang.String) r3)
                r14.clearFormattingElementsToLastMarker()
                org.jsoup.parser.HtmlTreeBuilderState r3 = InTable
                r14.transition(r3)
            L_0x0050:
                r3 = r4
                goto L_0x002c
            L_0x0052:
                boolean r5 = r13.isStartTag()
                if (r5 == 0) goto L_0x0093
                org.jsoup.parser.Token$StartTag r5 = r13.asStartTag()
                java.lang.String r5 = r5.name()
                r6 = 9
                java.lang.String[] r6 = new java.lang.String[r6]
                java.lang.String r7 = "caption"
                r6[r3] = r7
                java.lang.String r7 = "col"
                r6[r4] = r7
                java.lang.String r7 = "colgroup"
                r6[r9] = r7
                java.lang.String r7 = "tbody"
                r6[r10] = r7
                java.lang.String r7 = "td"
                r6[r11] = r7
                r7 = 5
                java.lang.String r8 = "tfoot"
                r6[r7] = r8
                r7 = 6
                java.lang.String r8 = "th"
                r6[r7] = r8
                r7 = 7
                java.lang.String r8 = "thead"
                r6[r7] = r8
                r7 = 8
                java.lang.String r8 = "tr"
                r6[r7] = r8
                boolean r5 = org.jsoup.helper.StringUtil.in(r5, r6)
                if (r5 != 0) goto L_0x00a9
            L_0x0093:
                boolean r5 = r13.isEndTag()
                if (r5 == 0) goto L_0x00bf
                org.jsoup.parser.Token$EndTag r5 = r13.asEndTag()
                java.lang.String r5 = r5.name()
                java.lang.String r6 = "table"
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x00bf
            L_0x00a9:
                r14.error(r12)
                org.jsoup.parser.Token$EndTag r3 = new org.jsoup.parser.Token$EndTag
                java.lang.String r5 = "caption"
                r3.<init>(r5)
                boolean r2 = r14.process(r3)
                if (r2 == 0) goto L_0x0050
                boolean r3 = r14.process(r13)
                goto L_0x002c
            L_0x00bf:
                boolean r5 = r13.isEndTag()
                if (r5 == 0) goto L_0x010b
                org.jsoup.parser.Token$EndTag r5 = r13.asEndTag()
                java.lang.String r5 = r5.name()
                r6 = 10
                java.lang.String[] r6 = new java.lang.String[r6]
                java.lang.String r7 = "body"
                r6[r3] = r7
                java.lang.String r7 = "col"
                r6[r4] = r7
                java.lang.String r4 = "colgroup"
                r6[r9] = r4
                java.lang.String r4 = "html"
                r6[r10] = r4
                java.lang.String r4 = "tbody"
                r6[r11] = r4
                r4 = 5
                java.lang.String r7 = "td"
                r6[r4] = r7
                r4 = 6
                java.lang.String r7 = "tfoot"
                r6[r4] = r7
                r4 = 7
                java.lang.String r7 = "th"
                r6[r4] = r7
                r4 = 8
                java.lang.String r7 = "thead"
                r6[r4] = r7
                r4 = 9
                java.lang.String r7 = "tr"
                r6[r4] = r7
                boolean r4 = org.jsoup.helper.StringUtil.in(r5, r6)
                if (r4 == 0) goto L_0x010b
                r14.error(r12)
                goto L_0x002c
            L_0x010b:
                org.jsoup.parser.HtmlTreeBuilderState r3 = InBody
                boolean r3 = r14.process(r13, r3)
                goto L_0x002c
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.HtmlTreeBuilderState.AnonymousClass11.process(org.jsoup.parser.Token, org.jsoup.parser.HtmlTreeBuilder):boolean");
        }
    },
    InColumnGroup {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
                return true;
            }
            switch (AnonymousClass24.$SwitchMap$org$jsoup$parser$Token$TokenType[t.type.ordinal()]) {
                case 1:
                    tb.insert(t.asComment());
                    return true;
                case 2:
                    tb.error(this);
                    return true;
                case 3:
                    Token.StartTag startTag = t.asStartTag();
                    String name = startTag.name();
                    if (name.equals("html")) {
                        return tb.process(t, InBody);
                    }
                    if (!name.equals("col")) {
                        return anythingElse(t, tb);
                    }
                    tb.insertEmpty(startTag);
                    return true;
                case 4:
                    if (!t.asEndTag().name().equals("colgroup")) {
                        return anythingElse(t, tb);
                    }
                    if (tb.currentElement().nodeName().equals("html")) {
                        tb.error(this);
                        return false;
                    }
                    tb.pop();
                    tb.transition(InTable);
                    return true;
                case 6:
                    if (!tb.currentElement().nodeName().equals("html")) {
                        return anythingElse(t, tb);
                    }
                    return true;
                default:
                    return anythingElse(t, tb);
            }
        }

        private boolean anythingElse(Token t, TreeBuilder tb) {
            if (tb.process(new Token.EndTag("colgroup"))) {
                return tb.process(t);
            }
            return true;
        }
    },
    InTableBody {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            switch (AnonymousClass24.$SwitchMap$org$jsoup$parser$Token$TokenType[t.type.ordinal()]) {
                case 3:
                    Token.StartTag startTag = t.asStartTag();
                    String name = startTag.name();
                    if (name.equals("tr")) {
                        tb.clearStackToTableBodyContext();
                        tb.insert(startTag);
                        tb.transition(InRow);
                        break;
                    } else {
                        if (StringUtil.in(name, "th", "td")) {
                            tb.error(this);
                            tb.process(new Token.StartTag("tr"));
                            return tb.process(startTag);
                        }
                        if (StringUtil.in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead")) {
                            return exitTableBody(t, tb);
                        }
                        return anythingElse(t, tb);
                    }
                case 4:
                    String name2 = t.asEndTag().name();
                    if (StringUtil.in(name2, "tbody", "tfoot", "thead")) {
                        if (tb.inTableScope(name2)) {
                            tb.clearStackToTableBodyContext();
                            tb.pop();
                            tb.transition(InTable);
                            break;
                        } else {
                            tb.error(this);
                            return false;
                        }
                    } else if (name2.equals("table")) {
                        return exitTableBody(t, tb);
                    } else {
                        if (!StringUtil.in(name2, "body", "caption", "col", "colgroup", "html", "td", "th", "tr")) {
                            return anythingElse(t, tb);
                        }
                        tb.error(this);
                        return false;
                    }
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean exitTableBody(Token t, HtmlTreeBuilder tb) {
            if (tb.inTableScope("tbody") || tb.inTableScope("thead") || tb.inScope("tfoot")) {
                tb.clearStackToTableBodyContext();
                tb.process(new Token.EndTag(tb.currentElement().nodeName()));
                return tb.process(t);
            }
            tb.error(this);
            return false;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InTable);
        }
    },
    InRow {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isStartTag()) {
                Token.StartTag startTag = t.asStartTag();
                String name = startTag.name();
                if (StringUtil.in(name, "th", "td")) {
                    tb.clearStackToTableRowContext();
                    tb.insert(startTag);
                    tb.transition(InCell);
                    tb.insertMarkerToFormattingElements();
                } else {
                    if (StringUtil.in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr")) {
                        return handleMissingTr(t, tb);
                    }
                    return anythingElse(t, tb);
                }
            } else if (!t.isEndTag()) {
                return anythingElse(t, tb);
            } else {
                String name2 = t.asEndTag().name();
                if (name2.equals("tr")) {
                    if (!tb.inTableScope(name2)) {
                        tb.error(this);
                        return false;
                    }
                    tb.clearStackToTableRowContext();
                    tb.pop();
                    tb.transition(InTableBody);
                } else if (name2.equals("table")) {
                    return handleMissingTr(t, tb);
                } else {
                    if (!StringUtil.in(name2, "tbody", "tfoot", "thead")) {
                        if (!StringUtil.in(name2, "body", "caption", "col", "colgroup", "html", "td", "th")) {
                            return anythingElse(t, tb);
                        }
                        tb.error(this);
                        return false;
                    } else if (!tb.inTableScope(name2)) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.process(new Token.EndTag("tr"));
                        return tb.process(t);
                    }
                }
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InTable);
        }

        private boolean handleMissingTr(Token t, TreeBuilder tb) {
            if (tb.process(new Token.EndTag("tr"))) {
                return tb.process(t);
            }
            return false;
        }
    },
    InCell {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isEndTag()) {
                String name = t.asEndTag().name();
                if (!StringUtil.in(name, "td", "th")) {
                    if (StringUtil.in(name, "body", "caption", "col", "colgroup", "html")) {
                        tb.error(this);
                        return false;
                    }
                    if (!StringUtil.in(name, "table", "tbody", "tfoot", "thead", "tr")) {
                        return anythingElse(t, tb);
                    }
                    if (!tb.inTableScope(name)) {
                        tb.error(this);
                        return false;
                    }
                    closeCell(tb);
                    return tb.process(t);
                } else if (!tb.inTableScope(name)) {
                    tb.error(this);
                    tb.transition(InRow);
                    return false;
                } else {
                    tb.generateImpliedEndTags();
                    if (!tb.currentElement().nodeName().equals(name)) {
                        tb.error(this);
                    }
                    tb.popStackToClose(name);
                    tb.clearFormattingElementsToLastMarker();
                    tb.transition(InRow);
                    return true;
                }
            } else {
                if (t.isStartTag()) {
                    if (StringUtil.in(t.asStartTag().name(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                        if (tb.inTableScope("td") || tb.inTableScope("th")) {
                            closeCell(tb);
                            return tb.process(t);
                        }
                        tb.error(this);
                        return false;
                    }
                }
                return anythingElse(t, tb);
            }
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InBody);
        }

        private void closeCell(HtmlTreeBuilder tb) {
            if (tb.inTableScope("td")) {
                tb.process(new Token.EndTag("td"));
            } else {
                tb.process(new Token.EndTag("th"));
            }
        }
    },
    InSelect {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            switch (AnonymousClass24.$SwitchMap$org$jsoup$parser$Token$TokenType[t.type.ordinal()]) {
                case 1:
                    tb.insert(t.asComment());
                    break;
                case 2:
                    tb.error(this);
                    return false;
                case 3:
                    Token.StartTag start = t.asStartTag();
                    String name = start.name();
                    if (name.equals("html")) {
                        return tb.process(start, InBody);
                    }
                    if (name.equals("option")) {
                        tb.process(new Token.EndTag("option"));
                        tb.insert(start);
                        break;
                    } else if (name.equals("optgroup")) {
                        if (tb.currentElement().nodeName().equals("option")) {
                            tb.process(new Token.EndTag("option"));
                        } else if (tb.currentElement().nodeName().equals("optgroup")) {
                            tb.process(new Token.EndTag("optgroup"));
                        }
                        tb.insert(start);
                        break;
                    } else if (name.equals("select")) {
                        tb.error(this);
                        return tb.process(new Token.EndTag("select"));
                    } else {
                        if (StringUtil.in(name, "input", "keygen", "textarea")) {
                            tb.error(this);
                            if (!tb.inSelectScope("select")) {
                                return false;
                            }
                            tb.process(new Token.EndTag("select"));
                            return tb.process(start);
                        } else if (name.equals("script")) {
                            return tb.process(t, InHead);
                        } else {
                            return anythingElse(t, tb);
                        }
                    }
                case 4:
                    String name2 = t.asEndTag().name();
                    if (name2.equals("optgroup")) {
                        if (tb.currentElement().nodeName().equals("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).nodeName().equals("optgroup")) {
                            tb.process(new Token.EndTag("option"));
                        }
                        if (!tb.currentElement().nodeName().equals("optgroup")) {
                            tb.error(this);
                            break;
                        } else {
                            tb.pop();
                            break;
                        }
                    } else if (name2.equals("option")) {
                        if (!tb.currentElement().nodeName().equals("option")) {
                            tb.error(this);
                            break;
                        } else {
                            tb.pop();
                            break;
                        }
                    } else if (name2.equals("select")) {
                        if (tb.inSelectScope(name2)) {
                            tb.popStackToClose(name2);
                            tb.resetInsertionMode();
                            break;
                        } else {
                            tb.error(this);
                            return false;
                        }
                    } else {
                        return anythingElse(t, tb);
                    }
                case 5:
                    Token.Character c = t.asCharacter();
                    if (!c.getData().equals(HtmlTreeBuilderState.nullString)) {
                        tb.insert(c);
                        break;
                    } else {
                        tb.error(this);
                        return false;
                    }
                case 6:
                    if (!tb.currentElement().nodeName().equals("html")) {
                        tb.error(this);
                        break;
                    }
                    break;
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            return false;
        }
    },
    InSelectInTable {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isStartTag()) {
                if (StringUtil.in(t.asStartTag().name(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
                    tb.error(this);
                    tb.process(new Token.EndTag("select"));
                    return tb.process(t);
                }
            }
            if (t.isEndTag()) {
                if (StringUtil.in(t.asEndTag().name(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
                    tb.error(this);
                    if (!tb.inTableScope(t.asEndTag().name())) {
                        return false;
                    }
                    tb.process(new Token.EndTag("select"));
                    return tb.process(t);
                }
            }
            return tb.process(t, InSelect);
        }
    },
    AfterBody {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return tb.process(t, InBody);
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().name().equals("html")) {
                return tb.process(t, InBody);
            } else {
                if (!t.isEndTag() || !t.asEndTag().name().equals("html")) {
                    if (!t.isEOF()) {
                        tb.error(this);
                        tb.transition(InBody);
                        return tb.process(t);
                    }
                } else if (tb.isFragmentParsing()) {
                    tb.error(this);
                    return false;
                } else {
                    tb.transition(AfterAfterBody);
                }
            }
            return true;
        }
    },
    InFrameset {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag()) {
                Token.StartTag start = t.asStartTag();
                String name = start.name();
                if (name.equals("html")) {
                    return tb.process(start, InBody);
                }
                if (name.equals("frameset")) {
                    tb.insert(start);
                } else if (name.equals("frame")) {
                    tb.insertEmpty(start);
                } else if (name.equals("noframes")) {
                    return tb.process(start, InHead);
                } else {
                    tb.error(this);
                    return false;
                }
            } else if (!t.isEndTag() || !t.asEndTag().name().equals("frameset")) {
                if (!t.isEOF()) {
                    tb.error(this);
                    return false;
                } else if (!tb.currentElement().nodeName().equals("html")) {
                    tb.error(this);
                    return true;
                }
            } else if (tb.currentElement().nodeName().equals("html")) {
                tb.error(this);
                return false;
            } else {
                tb.pop();
                if (!tb.isFragmentParsing() && !tb.currentElement().nodeName().equals("frameset")) {
                    tb.transition(AfterFrameset);
                }
            }
            return true;
        }
    },
    AfterFrameset {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().name().equals("html")) {
                return tb.process(t, InBody);
            } else {
                if (t.isEndTag() && t.asEndTag().name().equals("html")) {
                    tb.transition(AfterAfterFrameset);
                } else if (t.isStartTag() && t.asStartTag().name().equals("noframes")) {
                    return tb.process(t, InHead);
                } else {
                    if (!t.isEOF()) {
                        tb.error(this);
                        return false;
                    }
                }
            }
            return true;
        }
    },
    AfterAfterBody {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype() || HtmlTreeBuilderState.isWhitespace(t) || (t.isStartTag() && t.asStartTag().name().equals("html"))) {
                return tb.process(t, InBody);
            } else {
                if (!t.isEOF()) {
                    tb.error(this);
                    tb.transition(InBody);
                    return tb.process(t);
                }
            }
            return true;
        }
    },
    AfterAfterFrameset {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype() || HtmlTreeBuilderState.isWhitespace(t) || (t.isStartTag() && t.asStartTag().name().equals("html"))) {
                return tb.process(t, InBody);
            } else {
                if (!t.isEOF()) {
                    if (t.isStartTag() && t.asStartTag().name().equals("noframes")) {
                        return tb.process(t, InHead);
                    }
                    tb.error(this);
                    return false;
                }
            }
            return true;
        }
    },
    ForeignContent {
        /* access modifiers changed from: package-private */
        public boolean process(Token t, HtmlTreeBuilder tb) {
            return true;
        }
    };
    
    /* access modifiers changed from: private */
    public static String nullString;

    /* access modifiers changed from: package-private */
    public abstract boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder);

    static {
        nullString = String.valueOf(0);
    }

    /* access modifiers changed from: private */
    public static boolean isWhitespace(Token t) {
        if (!t.isCharacter()) {
            return false;
        }
        String data = t.asCharacter().getData();
        for (int i = 0; i < data.length(); i++) {
            if (!StringUtil.isWhitespace(data.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static void handleRcData(Token.StartTag startTag, HtmlTreeBuilder tb) {
        tb.insert(startTag);
        tb.tokeniser.transition(TokeniserState.Rcdata);
        tb.markInsertionMode();
        tb.transition(Text);
    }

    /* access modifiers changed from: private */
    public static void handleRawtext(Token.StartTag startTag, HtmlTreeBuilder tb) {
        tb.insert(startTag);
        tb.tokeniser.transition(TokeniserState.Rawtext);
        tb.markInsertionMode();
        tb.transition(Text);
    }

    private static final class Constants {
        /* access modifiers changed from: private */
        public static final String[] DdDt = null;
        /* access modifiers changed from: private */
        public static final String[] Formatters = null;
        /* access modifiers changed from: private */
        public static final String[] Headings = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyEndAdoptionFormatters = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyEndClosers = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyEndTableFosters = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartApplets = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartDrop = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartEmptyFormatters = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartInputAttribs = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartLiBreakers = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartMedia = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartOptions = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartPClosers = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartPreListing = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartRuby = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartToHead = null;

        private Constants() {
        }

        static {
            InBodyStartToHead = new String[]{"base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", LauncherSettings.BaseLauncherColumns.TITLE};
            InBodyStartPClosers = new String[]{"address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", "summary", "ul"};
            Headings = new String[]{"h1", "h2", "h3", "h4", "h5", "h6"};
            InBodyStartPreListing = new String[]{"pre", "listing"};
            InBodyStartLiBreakers = new String[]{"address", "div", "p"};
            DdDt = new String[]{"dd", "dt"};
            Formatters = new String[]{"b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", "tt", "u"};
            InBodyStartApplets = new String[]{"applet", "marquee", "object"};
            InBodyStartEmptyFormatters = new String[]{"area", "br", "embed", "img", "keygen", "wbr"};
            InBodyStartMedia = new String[]{"param", "source", "track"};
            InBodyStartInputAttribs = new String[]{InstallShortcutReceiver.NAME_KEY, "action", "prompt"};
            InBodyStartOptions = new String[]{"optgroup", "option"};
            InBodyStartRuby = new String[]{"rp", "rt"};
            InBodyStartDrop = new String[]{"caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", "tr"};
            InBodyEndClosers = new String[]{"address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", "pre", "section", "summary", "ul"};
            InBodyEndAdoptionFormatters = new String[]{"a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", "strike", "strong", "tt", "u"};
            InBodyEndTableFosters = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
        }
    }
}
