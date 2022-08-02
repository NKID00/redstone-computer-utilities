package name.nkid00.rcutil.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    public static final Logger LOGGER = LoggerFactory.getLogger("rcutil");
    private static final String BRAND = "[rcutil] ";

    public static void info(String arg0) {
        LOGGER.info(BRAND + arg0);
    }

    public static void info(String arg0, Object... arg1) {
        LOGGER.info(BRAND + arg0, arg1);
    }

    public static void warn(String arg0) {
        LOGGER.warn(BRAND + arg0);
    }

    public static void warn(String arg0, Throwable arg1) {
        LOGGER.warn(BRAND + arg0, arg1);
    }

    public static void warn(String arg0, Object... arg1) {
        LOGGER.warn(BRAND + arg0, arg1);
    }

    public static void error(String arg0) {
        LOGGER.error(BRAND + arg0);
    }

    public static void error(String arg0, Throwable arg1) {
        LOGGER.error(BRAND + arg0, arg1);
    }

    public static void error(String arg0, Object... arg1) {
        LOGGER.error(BRAND + arg0, arg1);
    }
}
