package cn.afternode.commons.library;

import cn.afternode.commons.ReflectionError;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClasspathAppender {
    private static Unsafe unsafe;
    private static MethodHandles.Lookup lookup;

    private static void init() {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.trySetAccessible();
            unsafe = (Unsafe) unsafeField.get(null);

            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookupField.trySetAccessible();
            lookup = (MethodHandles.Lookup) lookupField.get(null);
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    /**
     * Java 9+ AppClassLoader
     * @throws ReflectionError Unable to get ucp field
     * @throws RuntimeException addURL failed
     */
    public static void app(URL url, ClassLoader ldr) {
        Field ucpField;
        try {
            ucpField = ldr.getClass().getDeclaredField("ucp");
        } catch (NoSuchFieldException e) {
            try {
                ucpField = ldr.getClass().getSuperclass().getDeclaredField("ucp");
            } catch (NoSuchFieldException ex) {
                throw new ReflectionError(ldr.getClass(), ex);
            }
        }

        try {
            addURL(ldr, ucpField, url);
        } catch (Throwable t) {
            throw new RuntimeException("addURL failed", t);
        }
    }

    /**
     * URLClassLoader
     */
    public static void ucl(URL url, URLClassLoader ldr) {
        Method md;
        try {
            md = ldr.getClass().getDeclaredMethod("addURL", URL.class);
        } catch (Throwable t) {
            throw new ReflectionError(ldr.getClass(), t);
        }


        md.trySetAccessible();
        try {
            md.invoke(ldr, url);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionError(md, ldr.getClass(), e);
        }
    }

    private static void addURL(ClassLoader ldr, Field ucpField, URL url) throws Throwable {
        Object ucp = unsafe.getObject(ldr, unsafe.objectFieldOffset(ucpField));

        MethodHandle hnd = lookup.findVirtual(ucp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
        hnd.invoke(ldr, url);
    }

    static {
        init();
    }
}
