package cn.afternode.commons.bukkit.annotations;

import com.google.gson.Gson;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SupportedAnnotationTypes({
        "cn.afternode.commons.bukkit.annotations.RegisterCommand",
        "cn.afternode.commons.bukkit.annotations.RegisterListener",
        "cn.afternode.commons.bukkit.annotations.RegisterPluginCommand"
})
public class AutoRegistrationProcessor extends AbstractProcessor {
    private Types types;
    private Messager messager;
    private Filer filer;

    private TypeElement registerCommand;
    private TypeElement registerListener;
    private TypeElement registerPluginCommand;

    private boolean generated = false;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Elements elements = processingEnv.getElementUtils();
        this.types = processingEnv.getTypeUtils();
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();

        this.registerCommand = elements.getTypeElement("cn.afternode.commons.bukkit.annotations.RegisterCommand");
        this.registerListener = elements.getTypeElement("cn.afternode.commons.bukkit.annotations.RegisterListener");
        this.registerPluginCommand = elements.getTypeElement("cn.afternode.commons.bukkit.annotations.RegisterPluginCommand");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (this.generated)
            return false;

        try {
            AutoRegistrationData data = new AutoRegistrationData(
                    this.processRegisterCommand(roundEnv),
                    this.processRegisterListener(roundEnv),
                    this.processRegisterPluginCommand(roundEnv)
            );
            String json = new Gson().toJson(data);
            try {
                this.filer.getResource(StandardLocation.CLASS_OUTPUT, "", AutoRegistrationData.LOCATION).delete();
            } catch (Throwable ignored) {}
            FileObject res = this.filer.createResource(StandardLocation.CLASS_OUTPUT, "", AutoRegistrationData.LOCATION);

            try (OutputStream os = res.openOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            this.generated = true;
        } catch (Throwable t) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, t.toString());
        }

        return true;
    }

    private List<String> processRegisterCommand(RoundEnvironment env) {
        List<String> result = new ArrayList<>();
        Set<TypeElement> elem = ElementFilter.typesIn(env.getElementsAnnotatedWith(this.registerCommand));

        for (TypeElement e : elem) {
            if (!isSuperClass(e, "org.bukkit.command.Command")) {   // invalid
                this.messager.printMessage(Diagnostic.Kind.WARNING, "%s was not a valid Bukkit command class".formatted(e.getQualifiedName()));
            } else check(result, e);
        }
        return result;
    }

    private List<String> processRegisterListener(RoundEnvironment env) {
        List<String> result = new ArrayList<>();

        Set<TypeElement> elem = ElementFilter.typesIn(env.getElementsAnnotatedWith(this.registerListener));
        for (TypeElement e : elem) {
            if (!isSuperClass(e, "org.bukkit.event.Listener")) {
                this.messager.printMessage(Diagnostic.Kind.WARNING, "%s was not a valid Bukkit listener class".formatted(e.getQualifiedName()));
            } else check(result, e);
        }

        return result;
    }

    private Map<String, String> processRegisterPluginCommand(RoundEnvironment env) {
        Map<String, String> result = new HashMap<>();

        Set<TypeElement> elem = ElementFilter.typesIn(env.getElementsAnnotatedWith(this.registerPluginCommand));
        for (TypeElement e : elem) {
            if (!isSuperClass(e, "org.bukkit.command.TabCompleter") && !isSuperClass(e, "org.bukkit.command.CommandExecutor")) {
                this.messager.printMessage(Diagnostic.Kind.WARNING, "%s was not a valid Bukkit plugin command class".formatted(e.getQualifiedName()));
            } else if (e.getModifiers().contains(Modifier.ABSTRACT)) {
                this.messager.printMessage(Diagnostic.Kind.WARNING, "%s was an abstract class".formatted(e.getQualifiedName()));
            } else if (e.getEnclosingElement().getKind().equals(ElementKind.CLASS) && !e.getModifiers().contains(Modifier.STATIC)) {
                this.messager.printMessage(Diagnostic.Kind.WARNING, "Nested class %s was not static".formatted(e.getQualifiedName()));
            } else if (checkConstructors(e)) {
                this.messager.printMessage(Diagnostic.Kind.WARNING, "%s has no constructor available".formatted(e.getQualifiedName()));
            } else {
                RegisterPluginCommand a = e.getAnnotation(RegisterPluginCommand.class);
                result.put(e.getQualifiedName().toString(), a.name());
            }
        }

        return result;
    }

    private void check(List<String> result, TypeElement e) {
        if (e.getModifiers().contains(Modifier.ABSTRACT)) {
            this.messager.printMessage(Diagnostic.Kind.WARNING, "%s was an abstract class".formatted(e.getQualifiedName()));
        } else if (e.getEnclosingElement().getKind().equals(ElementKind.CLASS) && !e.getModifiers().contains(Modifier.STATIC)) {
            this.messager.printMessage(Diagnostic.Kind.WARNING, "Nested class %s was not static".formatted(e.getQualifiedName()));
        } else if (checkConstructors(e)) {
            this.messager.printMessage(Diagnostic.Kind.WARNING, "%s has no constructor available".formatted(e.getQualifiedName()));
        } else {
            result.add(e.getQualifiedName().toString());
        }
    }

    private boolean checkConstructors(TypeElement element) {
        for (ExecutableElement constructors : ElementFilter.constructorsIn(element.getEnclosedElements())) {
            if (constructors.getParameters().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isSuperClass(TypeElement t, String target) {
        for (TypeMirror type : this.types.directSupertypes(t.asType())) {
            TypeElement elem = (TypeElement) ((DeclaredType) type).asElement();
            if (elem.getQualifiedName().contentEquals(target))
                return true;
            else if (isSuperClass(elem, target))
                return true;
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
