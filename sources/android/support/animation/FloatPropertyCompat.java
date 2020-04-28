package android.support.animation;

import android.support.annotation.RequiresApi;
import android.util.FloatProperty;

public abstract class FloatPropertyCompat<T> {
    final String mPropertyName;

    public abstract float getValue(T t);

    public abstract void setValue(T t, float f);

    public FloatPropertyCompat(String name) {
        this.mPropertyName = name;
    }

    @RequiresApi(24)
    public static <T> FloatPropertyCompat<T> createFloatPropertyCompat(final FloatProperty<T> property) {
        return new FloatPropertyCompat<T>(property.getName()) {
            public float getValue(T object) {
                return ((Float) property.get(object)).floatValue();
            }

            public void setValue(T object, float value) {
                property.setValue(object, value);
            }
        };
    }
}
