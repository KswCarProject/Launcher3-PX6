package com.android.launcher3.util;

import android.content.ComponentName;
import android.os.UserHandle;
import java.util.Arrays;

public class ComponentKey {
    public final ComponentName componentName;
    private final int mHashCode;
    public final UserHandle user;

    public ComponentKey(ComponentName componentName2, UserHandle user2) {
        Preconditions.assertNotNull(componentName2);
        Preconditions.assertNotNull(user2);
        this.componentName = componentName2;
        this.user = user2;
        this.mHashCode = Arrays.hashCode(new Object[]{componentName2, user2});
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object o) {
        ComponentKey other = (ComponentKey) o;
        return other.componentName.equals(this.componentName) && other.user.equals(this.user);
    }

    public String toString() {
        return this.componentName.flattenToString() + "#" + this.user;
    }
}
