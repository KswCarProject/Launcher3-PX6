package org.apache.http.impl.cookie;

import java.util.BitSet;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.cookie.SetCookie;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxExpiresHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler {
    private static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("^([0-9]{1,2})([^0-9].*)?$");
    private static final BitSet DELIMS;
    private static final Map<String, Integer> MONTHS;
    private static final Pattern MONTH_PATTERN = Pattern.compile("^(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)(.*)?$", 2);
    private static final Pattern TIME_PATTERN = Pattern.compile("^([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2})([^0-9].*)?$");
    static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Pattern YEAR_PATTERN = Pattern.compile("^([0-9]{2,4})([^0-9].*)?$");

    static {
        BitSet bitSet = new BitSet();
        bitSet.set(9);
        for (int b = 32; b <= 47; b++) {
            bitSet.set(b);
        }
        for (int b2 = 59; b2 <= 64; b2++) {
            bitSet.set(b2);
        }
        for (int b3 = 91; b3 <= 96; b3++) {
            bitSet.set(b3);
        }
        for (int b4 = 123; b4 <= 126; b4++) {
            bitSet.set(b4);
        }
        DELIMS = bitSet;
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>(12);
        map.put("jan", 0);
        map.put("feb", 1);
        map.put("mar", 2);
        map.put("apr", 3);
        map.put("may", 4);
        map.put("jun", 5);
        map.put("jul", 6);
        map.put("aug", 7);
        map.put("sep", 8);
        map.put("oct", 9);
        map.put("nov", 10);
        map.put("dec", 11);
        MONTHS = map;
    }

    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        Args.notNull(cookie, SM.COOKIE);
        ParserCursor cursor = new ParserCursor(0, value.length());
        StringBuilder content = new StringBuilder();
        int second = 0;
        int minute = 0;
        int hour = 0;
        int day = 0;
        int month = 0;
        int year = 0;
        boolean foundTime = false;
        boolean foundDayOfMonth = false;
        boolean foundMonth = false;
        boolean foundYear = false;
        while (!cursor.atEnd()) {
            try {
                skipDelims(value, cursor);
                content.setLength(0);
                copyContent(value, cursor, content);
                if (content.length() == 0) {
                    break;
                }
                if (!foundTime) {
                    Matcher matcher = TIME_PATTERN.matcher(content);
                    if (matcher.matches()) {
                        foundTime = true;
                        hour = Integer.parseInt(matcher.group(1));
                        minute = Integer.parseInt(matcher.group(2));
                        second = Integer.parseInt(matcher.group(3));
                    }
                }
                if (!foundDayOfMonth) {
                    Matcher matcher2 = DAY_OF_MONTH_PATTERN.matcher(content);
                    if (matcher2.matches()) {
                        foundDayOfMonth = true;
                        day = Integer.parseInt(matcher2.group(1));
                    }
                }
                if (!foundMonth) {
                    Matcher matcher3 = MONTH_PATTERN.matcher(content);
                    if (matcher3.matches()) {
                        foundMonth = true;
                        month = MONTHS.get(matcher3.group(1).toLowerCase(Locale.ROOT)).intValue();
                    }
                }
                if (!foundYear) {
                    Matcher matcher4 = YEAR_PATTERN.matcher(content);
                    if (matcher4.matches()) {
                        foundYear = true;
                        year = Integer.parseInt(matcher4.group(1));
                    }
                }
            } catch (NumberFormatException e) {
                throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
            }
        }
        if (!foundTime || !foundDayOfMonth || !foundMonth || !foundYear) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
        }
        if (year >= 70 && year <= 99) {
            year += 1900;
        }
        if (year >= 0 && year <= 69) {
            year += 2000;
        }
        if (day < 1 || day > 31 || year < 1601 || hour > 23 || minute > 59 || second > 59) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
        }
        Calendar c = Calendar.getInstance();
        c.setTimeZone(UTC);
        c.setTimeInMillis(0);
        c.set(13, second);
        c.set(12, minute);
        c.set(11, hour);
        c.set(5, day);
        c.set(2, month);
        c.set(1, year);
        cookie.setExpiryDate(c.getTime());
    }

    private void skipDelims(CharSequence buf, ParserCursor cursor) {
        int pos = cursor.getPos();
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        for (int i = indexFrom; i < indexTo; i++) {
            if (!DELIMS.get(buf.charAt(i))) {
                break;
            }
            pos++;
        }
        cursor.updatePos(pos);
    }

    private void copyContent(CharSequence buf, ParserCursor cursor, StringBuilder dst) {
        int pos = cursor.getPos();
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        for (int i = indexFrom; i < indexTo; i++) {
            char current = buf.charAt(i);
            if (DELIMS.get(current)) {
                break;
            }
            pos++;
            dst.append(current);
        }
        cursor.updatePos(pos);
    }

    public String getAttributeName() {
        return ClientCookie.EXPIRES_ATTR;
    }
}
