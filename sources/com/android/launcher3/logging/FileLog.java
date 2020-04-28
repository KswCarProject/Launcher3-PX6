package com.android.launcher3.logging;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.Utilities;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class FileLog {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(3, 3);
    protected static final boolean ENABLED = Utilities.IS_DEBUG_DEVICE;
    private static final String FILE_NAME_PREFIX = "log-";
    private static final long MAX_LOG_FILE_SIZE = 4194304;
    /* access modifiers changed from: private */
    public static Handler sHandler = null;
    /* access modifiers changed from: private */
    public static File sLogsDirectory = null;

    public static void setDir(File logsDir) {
        if (ENABLED) {
            synchronized (DATE_FORMAT) {
                if (sHandler != null && !logsDir.equals(sLogsDirectory)) {
                    ((HandlerThread) sHandler.getLooper().getThread()).quit();
                    sHandler = null;
                }
            }
        }
        sLogsDirectory = logsDir;
    }

    public static void d(String tag, String msg, Exception e) {
        Log.d(tag, msg, e);
        print(tag, msg, e);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        print(tag, msg);
    }

    public static void e(String tag, String msg, Exception e) {
        Log.e(tag, msg, e);
        print(tag, msg, e);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        print(tag, msg);
    }

    public static void print(String tag, String msg) {
        print(tag, msg, (Exception) null);
    }

    public static void print(String tag, String msg, Exception e) {
        if (ENABLED) {
            String out = String.format("%s %s %s", new Object[]{DATE_FORMAT.format(new Date()), tag, msg});
            if (e != null) {
                out = out + "\n" + Log.getStackTraceString(e);
            }
            Message.obtain(getHandler(), 1, out).sendToTarget();
        }
    }

    private static Handler getHandler() {
        synchronized (DATE_FORMAT) {
            if (sHandler == null) {
                HandlerThread thread = new HandlerThread("file-logger");
                thread.start();
                sHandler = new Handler(thread.getLooper(), new LogWriterCallback());
            }
        }
        return sHandler;
    }

    public static void flushAll(PrintWriter out) throws InterruptedException {
        if (ENABLED) {
            CountDownLatch latch = new CountDownLatch(1);
            Message.obtain(getHandler(), 3, Pair.create(out, latch)).sendToTarget();
            latch.await(2, TimeUnit.SECONDS);
        }
    }

    private static class LogWriterCallback implements Handler.Callback {
        private static final long CLOSE_DELAY = 5000;
        private static final int MSG_CLOSE = 2;
        private static final int MSG_FLUSH = 3;
        private static final int MSG_WRITE = 1;
        private String mCurrentFileName;
        private PrintWriter mCurrentWriter;

        private LogWriterCallback() {
            this.mCurrentFileName = null;
            this.mCurrentWriter = null;
        }

        private void closeWriter() {
            Utilities.closeSilently(this.mCurrentWriter);
            this.mCurrentWriter = null;
        }

        public boolean handleMessage(Message msg) {
            if (FileLog.sLogsDirectory == null || !FileLog.ENABLED) {
                return true;
            }
            switch (msg.what) {
                case 1:
                    Calendar cal = Calendar.getInstance();
                    String fileName = FileLog.FILE_NAME_PREFIX + (cal.get(6) & 1);
                    if (!fileName.equals(this.mCurrentFileName)) {
                        closeWriter();
                    }
                    try {
                        if (this.mCurrentWriter == null) {
                            this.mCurrentFileName = fileName;
                            boolean append = false;
                            File logFile = new File(FileLog.sLogsDirectory, fileName);
                            if (logFile.exists()) {
                                Calendar modifiedTime = Calendar.getInstance();
                                modifiedTime.setTimeInMillis(logFile.lastModified());
                                modifiedTime.add(10, 36);
                                append = cal.before(modifiedTime) && logFile.length() < FileLog.MAX_LOG_FILE_SIZE;
                            }
                            this.mCurrentWriter = new PrintWriter(new FileWriter(logFile, append));
                        }
                        this.mCurrentWriter.println((String) msg.obj);
                        this.mCurrentWriter.flush();
                        FileLog.sHandler.removeMessages(2);
                        FileLog.sHandler.sendEmptyMessageDelayed(2, CLOSE_DELAY);
                    } catch (Exception e) {
                        Log.e("FileLog", "Error writing logs to file", e);
                        closeWriter();
                    }
                    return true;
                case 2:
                    closeWriter();
                    return true;
                case 3:
                    closeWriter();
                    Pair<PrintWriter, CountDownLatch> p = (Pair) msg.obj;
                    if (p.first != null) {
                        FileLog.dumpFile((PrintWriter) p.first, "log-0");
                        FileLog.dumpFile((PrintWriter) p.first, "log-1");
                    }
                    ((CountDownLatch) p.second).countDown();
                    return true;
                default:
                    return true;
            }
        }
    }

    /* access modifiers changed from: private */
    public static void dumpFile(PrintWriter out, String fileName) {
        File logFile = new File(sLogsDirectory, fileName);
        if (logFile.exists()) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(logFile));
                out.println();
                out.println("--- logfile: " + fileName + " ---");
                while (true) {
                    String readLine = in.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    out.println(line);
                }
            } catch (Exception e) {
            } catch (Throwable th) {
                Utilities.closeSilently((Closeable) null);
                throw th;
            }
            Utilities.closeSilently(in);
        }
    }
}
