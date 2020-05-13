package org.jsoup.nodes;

import com.baidu.location.BDLocation;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import org.apache.http.message.TokenParser;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class Entities {
    private static final Map<String, Character> base = loadEntities("entities-base.properties");
    /* access modifiers changed from: private */
    public static final Map<Character, String> baseByVal = toCharacterKey(base);
    private static final Map<String, Character> full = loadEntities("entities-full.properties");
    /* access modifiers changed from: private */
    public static final Map<Character, String> fullByVal = toCharacterKey(full);
    private static final Object[][] xhtmlArray = {new Object[]{"quot", 34}, new Object[]{"amp", 38}, new Object[]{"lt", 60}, new Object[]{"gt", 62}};
    /* access modifiers changed from: private */
    public static final Map<Character, String> xhtmlByVal = new HashMap();

    public enum EscapeMode {
        xhtml(Entities.xhtmlByVal),
        base(Entities.baseByVal),
        extended(Entities.fullByVal);
        
        private Map<Character, String> map;

        private EscapeMode(Map<Character, String> map2) {
            this.map = map2;
        }

        public Map<Character, String> getMap() {
            return this.map;
        }
    }

    private Entities() {
    }

    public static boolean isNamedEntity(String name) {
        return full.containsKey(name);
    }

    public static boolean isBaseNamedEntity(String name) {
        return base.containsKey(name);
    }

    public static Character getCharacterByName(String name) {
        return full.get(name);
    }

    static String escape(String string, Document.OutputSettings out) {
        StringBuilder accum = new StringBuilder(string.length() * 2);
        escape(accum, string, out, false, false, false);
        return accum.toString();
    }

    static void escape(StringBuilder accum, String string, Document.OutputSettings out, boolean inAttribute, boolean normaliseWhite, boolean stripLeadingWhite) {
        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;
        EscapeMode escapeMode = out.escapeMode();
        CharsetEncoder encoder = out.encoder();
        Map<Character, String> map = escapeMode.getMap();
        int length = string.length();
        int offset = 0;
        while (offset < length) {
            int codePoint = string.codePointAt(offset);
            if (normaliseWhite) {
                if (StringUtil.isWhitespace(codePoint)) {
                    if ((!stripLeadingWhite || reachedNonWhite) && !lastWasWhite) {
                        accum.append(TokenParser.SP);
                        lastWasWhite = true;
                    }
                    offset += Character.charCount(codePoint);
                } else {
                    lastWasWhite = false;
                    reachedNonWhite = true;
                }
            }
            if (codePoint < 65536) {
                char c = (char) codePoint;
                switch (c) {
                    case '\"':
                        if (!inAttribute) {
                            accum.append(c);
                            break;
                        } else {
                            accum.append("&quot;");
                            break;
                        }
                    case '&':
                        accum.append("&amp;");
                        break;
                    case '<':
                        if (inAttribute) {
                            accum.append(c);
                            break;
                        } else {
                            accum.append("&lt;");
                            break;
                        }
                    case BDLocation.TypeCriteriaException:
                        if (inAttribute) {
                            accum.append(c);
                            break;
                        } else {
                            accum.append("&gt;");
                            break;
                        }
                    case 160:
                        if (escapeMode == EscapeMode.xhtml) {
                            accum.append(c);
                            break;
                        } else {
                            accum.append("&nbsp;");
                            break;
                        }
                    default:
                        if (!encoder.canEncode(c)) {
                            if (!map.containsKey(Character.valueOf(c))) {
                                accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
                                break;
                            } else {
                                accum.append('&').append(map.get(Character.valueOf(c))).append(';');
                                break;
                            }
                        } else {
                            accum.append(c);
                            break;
                        }
                }
            } else {
                String c2 = new String(Character.toChars(codePoint));
                if (encoder.canEncode(c2)) {
                    accum.append(c2);
                } else {
                    accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
                }
            }
            offset += Character.charCount(codePoint);
        }
    }

    static String unescape(String string) {
        return unescape(string, false);
    }

    static String unescape(String string, boolean strict) {
        return Parser.unescapeEntities(string, strict);
    }

    static {
        for (Object[] entity : xhtmlArray) {
            xhtmlByVal.put(Character.valueOf((char) ((Integer) entity[1]).intValue()), (String) entity[0]);
        }
    }

    private static Map<String, Character> loadEntities(String filename) {
        Properties properties = new Properties();
        Map<String, Character> entities = new HashMap<>();
        try {
            InputStream in = Entities.class.getResourceAsStream(filename);
            properties.load(in);
            in.close();
            for (Map.Entry entry : properties.entrySet()) {
                entities.put((String) entry.getKey(), Character.valueOf((char) Integer.parseInt((String) entry.getValue(), 16)));
            }
            return entities;
        } catch (IOException e) {
            throw new MissingResourceException("Error loading entities resource: " + e.getMessage(), "Entities", filename);
        }
    }

    private static Map<Character, String> toCharacterKey(Map<String, Character> inMap) {
        Map<Character, String> outMap = new HashMap<>();
        for (Map.Entry<String, Character> entry : inMap.entrySet()) {
            Character character = entry.getValue();
            String name = entry.getKey();
            if (!outMap.containsKey(character)) {
                outMap.put(character, name);
            } else if (name.toLowerCase().equals(name)) {
                outMap.put(character, name);
            }
        }
        return outMap;
    }
}
