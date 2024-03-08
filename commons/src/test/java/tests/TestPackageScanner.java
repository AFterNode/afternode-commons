package tests;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.IOException;

@TestPackageScanner.TestAnno
public class TestPackageScanner {
    @Test
    public void test() throws IOException {
        Reflections ref = new Reflections("tests");
        System.out.println(ref.get(Scanners.SubTypes.of(Scanners.TypesAnnotated.of(TestAnno.class)).asClass()).toArray(new Class<?>[0])[0].getName());
    }

    public @interface TestAnno {}
}
