package org.apache.http.impl.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;

@Contract(threading = ThreadingBehavior.SAFE)
public class BasicCookieStore implements CookieStore, Serializable {
    private static final long serialVersionUID = -7581093305228232025L;
    private final TreeSet<Cookie> cookies = new TreeSet<>(new CookieIdentityComparator());

    public synchronized void addCookie(Cookie cookie) {
        if (cookie != null) {
            this.cookies.remove(cookie);
            if (!cookie.isExpired(new Date())) {
                this.cookies.add(cookie);
            }
        }
    }

    public synchronized void addCookies(Cookie[] cookies2) {
        if (cookies2 != null) {
            for (Cookie cooky : cookies2) {
                addCookie(cooky);
            }
        }
    }

    public synchronized List<Cookie> getCookies() {
        return new ArrayList(this.cookies);
    }

    public synchronized boolean clearExpired(Date date) {
        boolean removed;
        if (date == null) {
            removed = false;
        } else {
            removed = false;
            Iterator<Cookie> it = this.cookies.iterator();
            while (it.hasNext()) {
                if (it.next().isExpired(date)) {
                    it.remove();
                    removed = true;
                }
            }
        }
        return removed;
    }

    public synchronized void clear() {
        this.cookies.clear();
    }

    public synchronized String toString() {
        return this.cookies.toString();
    }
}
