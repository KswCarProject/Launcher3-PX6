package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

public class BasicHeaderElement implements HeaderElement, Cloneable {
    private final String name;
    private final NameValuePair[] parameters;
    private final String value;

    public BasicHeaderElement(String name2, String value2, NameValuePair[] parameters2) {
        this.name = (String) Args.notNull(name2, "Name");
        this.value = value2;
        if (parameters2 != null) {
            this.parameters = parameters2;
        } else {
            this.parameters = new NameValuePair[0];
        }
    }

    public BasicHeaderElement(String name2, String value2) {
        this(name2, value2, (NameValuePair[]) null);
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public NameValuePair[] getParameters() {
        return (NameValuePair[]) this.parameters.clone();
    }

    public int getParameterCount() {
        return this.parameters.length;
    }

    public NameValuePair getParameter(int index) {
        return this.parameters[index];
    }

    public NameValuePair getParameterByName(String name2) {
        Args.notNull(name2, "Name");
        for (NameValuePair current : this.parameters) {
            if (current.getName().equalsIgnoreCase(name2)) {
                return current;
            }
        }
        return null;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof HeaderElement)) {
            return false;
        }
        BasicHeaderElement that = (BasicHeaderElement) object;
        if (!this.name.equals(that.name) || !LangUtils.equals((Object) this.value, (Object) that.value) || !LangUtils.equals((Object[]) this.parameters, (Object[]) that.parameters)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = LangUtils.hashCode(LangUtils.hashCode(17, (Object) this.name), (Object) this.value);
        for (NameValuePair parameter : this.parameters) {
            hash = LangUtils.hashCode(hash, (Object) parameter);
        }
        return hash;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.name);
        if (this.value != null) {
            buffer.append("=");
            buffer.append(this.value);
        }
        for (NameValuePair parameter : this.parameters) {
            buffer.append("; ");
            buffer.append(parameter);
        }
        return buffer.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
