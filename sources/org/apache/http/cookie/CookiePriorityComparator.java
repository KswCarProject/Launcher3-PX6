package org.apache.http.cookie;

import java.util.Comparator;
import java.util.Date;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.impl.cookie.BasicClientCookie;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class CookiePriorityComparator implements Comparator<Cookie> {
    public static final CookiePriorityComparator INSTANCE = new CookiePriorityComparator();

    private int getPathLength(Cookie cookie) {
        String path = cookie.getPath();
        if (path != null) {
            return path.length();
        }
        return 1;
    }

    public int compare(Cookie c1, Cookie c2) {
        int result = getPathLength(c2) - getPathLength(c1);
        if (result != 0 || !(c1 instanceof BasicClientCookie) || !(c2 instanceof BasicClientCookie)) {
            return result;
        }
        Date d1 = ((BasicClientCookie) c1).getCreationDate();
        Date d2 = ((BasicClientCookie) c2).getCreationDate();
        if (d1 == null || d2 == null) {
            return result;
        }
        return (int) (d1.getTime() - d2.getTime());
    }
}
