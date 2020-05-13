package org.apache.http.conn.ssl;

import org.apache.http.util.Args;

final class SubjectName {
    static final int DNS = 2;
    static final int IP = 7;
    private final int type;
    private final String value;

    static SubjectName IP(String value2) {
        return new SubjectName(value2, 7);
    }

    static SubjectName DNS(String value2) {
        return new SubjectName(value2, 2);
    }

    SubjectName(String value2, int type2) {
        this.value = (String) Args.notNull(value2, "Value");
        this.type = Args.positive(type2, "Type");
    }

    public int getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.value;
    }
}
