package org.apache.http;

import java.io.Serializable;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ProtocolVersion implements Serializable, Cloneable {
    private static final long serialVersionUID = 8950662842175091068L;
    protected final int major;
    protected final int minor;
    protected final String protocol;

    public ProtocolVersion(String protocol2, int major2, int minor2) {
        this.protocol = (String) Args.notNull(protocol2, "Protocol name");
        this.major = Args.notNegative(major2, "Protocol minor version");
        this.minor = Args.notNegative(minor2, "Protocol minor version");
    }

    public final String getProtocol() {
        return this.protocol;
    }

    public final int getMajor() {
        return this.major;
    }

    public final int getMinor() {
        return this.minor;
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public ProtocolVersion forVersion(int major2, int minor2) {
        return (major2 == this.major && minor2 == this.minor) ? this : new ProtocolVersion(this.protocol, major2, minor2);
    }

    public final int hashCode() {
        return (this.protocol.hashCode() ^ (this.major * 100000)) ^ this.minor;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProtocolVersion)) {
            return false;
        }
        ProtocolVersion that = (ProtocolVersion) obj;
        if (this.protocol.equals(that.protocol) && this.major == that.major && this.minor == that.minor) {
            return true;
        }
        return false;
    }

    public boolean isComparable(ProtocolVersion that) {
        return that != null && this.protocol.equals(that.protocol);
    }

    public int compareToVersion(ProtocolVersion that) {
        Args.notNull(that, "Protocol version");
        Args.check(this.protocol.equals(that.protocol), "Versions for different protocols cannot be compared: %s %s", this, that);
        int delta = getMajor() - that.getMajor();
        if (delta == 0) {
            return getMinor() - that.getMinor();
        }
        return delta;
    }

    public final boolean greaterEquals(ProtocolVersion version) {
        return isComparable(version) && compareToVersion(version) >= 0;
    }

    public final boolean lessEquals(ProtocolVersion version) {
        return isComparable(version) && compareToVersion(version) <= 0;
    }

    public String toString() {
        return this.protocol + '/' + Integer.toString(this.major) + '.' + Integer.toString(this.minor);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
