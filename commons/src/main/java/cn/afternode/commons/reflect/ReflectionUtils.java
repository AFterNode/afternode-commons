package cn.afternode.commons.reflect;

public class ReflectionUtils {

    /**
     * @return Nullable class value
     */
    public static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        } catch (Throwable t) {
            return null;
        }
    }
}
