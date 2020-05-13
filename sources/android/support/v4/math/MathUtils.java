package android.support.v4.math;

public class MathUtils {
    public static float clamp(float value, int min, int max) {
        if (value < ((float) min)) {
            return (float) min;
        }
        if (value > ((float) max)) {
            return (float) max;
        }
        return value;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
