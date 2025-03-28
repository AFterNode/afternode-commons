import cn.afternode.commons.bukkit.annotations.AutoRegistrationData;
import cn.afternode.commons.bukkit.annotations.AutoRegistrationProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;

public class TestAutoRegistrationProcessor {
    static final String SRC_COMMAND = """
            import org.bukkit.command.*;
            
            @cn.afternode.commons.bukkit.annotations.RegisterCommand
            public class TestCommand extends org.bukkit.command.Command {
                public TestCommand() {
                    super("test");
                }

                public boolean execute(CommandSender var1, String var2, String[] var3) {
                    return true;
                }
            }
            """;

    static final String SRC_LISTENER = """
            @cn.afternode.commons.bukkit.annotations.RegisterListener
            public class TestListener implements org.bukkit.event.Listener {}
            """;

    static final String SRC_PLUGIN_COMMAND = """
            import org.bukkit.command.*;
            
            @cn.afternode.commons.bukkit.annotations.RegisterPluginCommand(name="test")
            public class TestPluginCommand implements org.bukkit.command.TabExecutor {
                public boolean onCommand(CommandSender var1, Command v, String var2, String[] var3) {
                    return true;
                }
                public java.util.List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
                    return new java.util.ArrayList<String>();
                }

                @cn.afternode.commons.bukkit.annotations.RegisterPluginCommand(name="test2")
                public static class TestNestedCommand implements org.bukkit.command.CommandExecutor {
                    public boolean onCommand(CommandSender var1, Command v, String var2, String[] var3) {
                        return true;
                    }
                }
            }
            """;

    public static void main(String[] args) throws Exception {
        new TestAutoRegistrationProcessor().test();
    }

    public void test() throws IOException {

        Compilation result = Compiler.javac()
                .withProcessors(new AutoRegistrationProcessor())
                .compile(
                        JavaFileObjects.forSourceLines("TestCommand", SRC_COMMAND),
                        JavaFileObjects.forSourceLines("TestListener", SRC_LISTENER),
                        JavaFileObjects.forSourceLines("TestPluginCommand", SRC_PLUGIN_COMMAND)
                );

        CompilationSubject.assertThat(result).succeededWithoutWarnings();
        for (JavaFileObject generatedFile : result.generatedFiles()) {
            System.out.println(generatedFile.getName());
        }
        JavaFileObject resource = result.generatedFile(
                StandardLocation.CLASS_OUTPUT,
                AutoRegistrationData.LOCATION
        ).orElseThrow();
        System.out.println(resource.getCharContent(true).toString());
    }
}
