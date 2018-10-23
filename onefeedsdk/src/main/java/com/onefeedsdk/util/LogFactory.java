package com.onefeedsdk.util;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 17-July-2018
 * Time: 16:21
 */
public class LogFactory {

    public static final String TAG = "ONE-FEED-SDK";

    public static boolean DEBUG = true;
    public static boolean WARNING = true;
    public static boolean INFO = true;
    public static boolean ERROR = true;

    // LOG
    public static Log getLog(Class<?> cls) {
        return new Log(cls.getName());
    }

    public static class Log {
        final String name;

        private Log(String name) {
            this.name = name;
        }


        static String formatMessage(Object... objs) {
            // Empty?
            if (objs == null || objs.length == 0) {
                return "";
            }

            // Single?
            if (objs.length == 1) {
                return objs[0].toString();
            }

            // Many?
            final String msg;
            StringBuilder sb = new StringBuilder();
            boolean delim = false;
            for (Object o : objs) {
                if (delim) {
                    sb.append("\n");
                } else {
                    delim = true;
                }
                if (o instanceof Throwable) {
                    Throwable e = (Throwable) o;
                    do {
                        String emsg = e.getMessage();
                        sb.append(e.getClass().getName());
                        if (emsg != null) {
                            sb.append(": ");
                            sb.append(emsg);
                        }
                        sb.append("\n");
                        for (StackTraceElement ste : e.getStackTrace()) {
                            sb.append("  ");
                            sb.append(ste.toString());
                            sb.append("\n");
                        }
                        e = e.getCause();
                        if (e != null) {
                            sb.append("caused by:\n");
                        }
                    } while (e != null);
                } else if (o instanceof Class) { // prefix message with class
                    // name
                    Class<?> cls = (Class<?>) o;
                    String name = cls.getSimpleName();
                    if (name == null || name.length() == 0) {
                        name = cls.getName();
                    }
                    sb.append(name);
                    sb.append(": ");
                    delim = false;
                } else if (o == null) {
                    sb.append("<null>");
                } else {
                    sb.append(o.toString());
                }
            }
            msg = sb.toString();
            return msg;
        }

        String header(String level) {
            return String.format("%s [%s] %s: ", level, Thread.currentThread().getName(), name);
        }

        public void debug(Object... obj) {
            if (!DEBUG) {
                return;
            }
            android.util.Log.d(TAG, header("DEBUG") + formatMessage(obj));
        }

        public void info(Object... obj) {
            if (!INFO) {
                return;
            }
            android.util.Log.i(TAG, header("INFO") + formatMessage(obj));
        }

        public void error(Object... obj) {
            if (!ERROR) {
                return;
            }
            android.util.Log.i(TAG, header("ERROR") + formatMessage(obj));
        }

        /**
         *
         * @param e
         * @param obj
         */
        public void error(Throwable e, Object... obj) {
            if (!ERROR) {
                return;
            }
            android.util.Log.e(TAG, header("ERROR") + formatMessage(obj),e);
        }
    }
}
