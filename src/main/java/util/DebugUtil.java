package util;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DebugUtil {

    private DebugUtil() {
    }

    public static String stringify(Object object) {
        if (object == null) {
            return "null";
        }

        return ReflectionToStringBuilder.toString(object);
    }
}
