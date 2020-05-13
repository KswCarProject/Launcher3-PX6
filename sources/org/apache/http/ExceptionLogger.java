package org.apache.http;

public interface ExceptionLogger {
    public static final ExceptionLogger NO_OP = new ExceptionLogger() {
        public void log(Exception ex) {
        }
    };
    public static final ExceptionLogger STD_ERR = new ExceptionLogger() {
        public void log(Exception ex) {
            ex.printStackTrace();
        }
    };

    void log(Exception exc);
}
