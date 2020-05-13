package org.jsoup.nodes;

import com.szchoiceway.index.InstallShortcutReceiver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.http.client.methods.HttpPost;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.helper.Validate;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class FormElement extends Element {
    private final Elements elements = new Elements();

    public FormElement(Tag tag, String baseUri, Attributes attributes) {
        super(tag, baseUri, attributes);
    }

    public Elements elements() {
        return this.elements;
    }

    public FormElement addElement(Element element) {
        this.elements.add(element);
        return this;
    }

    public Connection submit() {
        String action = hasAttr("action") ? absUrl("action") : baseUri();
        Validate.notEmpty(action, "Could not determine a form action URL for submit. Ensure you set a base URI when parsing.");
        return Jsoup.connect(action).data((Collection<Connection.KeyVal>) formData()).method(attr("method").toUpperCase().equals(HttpPost.METHOD_NAME) ? Connection.Method.POST : Connection.Method.GET);
    }

    public List<Connection.KeyVal> formData() {
        ArrayList<Connection.KeyVal> data = new ArrayList<>();
        Iterator<Element> it = this.elements.iterator();
        while (it.hasNext()) {
            Element el = it.next();
            if (el.tag().isFormSubmittable()) {
                String name = el.attr(InstallShortcutReceiver.NAME_KEY);
                if (name.length() != 0) {
                    if ("select".equals(el.tagName())) {
                        Iterator i$ = el.select("option[selected]").iterator();
                        while (i$.hasNext()) {
                            data.add(HttpConnection.KeyVal.create(name, i$.next().val()));
                        }
                    } else {
                        data.add(HttpConnection.KeyVal.create(name, el.val()));
                    }
                }
            }
        }
        return data;
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }
}
