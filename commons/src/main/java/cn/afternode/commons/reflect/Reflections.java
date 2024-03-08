package cn.afternode.commons.reflect;

import cn.afternode.commons.Filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reflections {
    /**
     * Find classes with provided ProtectionDomain
     * <br>
     * Will execute multiple time of Class.forName, and may cause performance problem
     * @param domain Target ProtectionDomain
     * @param filter Class name filter
     * @throws IOException Cannot open target JAR
     */
    public static Class<?>[] findClasses(ProtectionDomain domain, Filter<String> filter) throws IOException {
        File file = new File(domain.getCodeSource().getLocation().getFile());
        if (!file.exists()) throw new FileNotFoundException("Cannot find target package");

        List<String> names = new ArrayList<>();
        try (JarFile jf = new JarFile(file)) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                var name = entries.nextElement().getRealName();
                if (name.endsWith(".class")) {
                    name = name.replace(".", "/").split(".class")[0];
                    if (filter.accept(name)) names.add(name);
                }
            }
        }

        List<Class<?>> classes = new ArrayList<>();
        for (String name: names) {
            try {
                classes.add(Class.forName(name));
            } catch (ClassNotFoundException ignored) {}
        }

        return classes.toArray(new Class<?>[0]);
    }

    /**
     * Find classes from provided ProtectionDomain and filter with class names and annotations
     * @param annotation Target annotation
     * @param domain Target ProtectionDomain
     * @param nameFilter Class names filter
     * @throws IOException Error in findClasses
     */
    public static Class<?>[] findClassByAnnotation(Class<? extends Annotation> annotation, ProtectionDomain domain, Filter<String> nameFilter) throws IOException {
        List<Class<?>> results = new ArrayList<>();
        for (Class<?> c: findClasses(domain, nameFilter)) {
            if (c.isAnnotationPresent(annotation)) results.add(c);
        }
        return results.toArray(new Class<?>[0]);
    }

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
