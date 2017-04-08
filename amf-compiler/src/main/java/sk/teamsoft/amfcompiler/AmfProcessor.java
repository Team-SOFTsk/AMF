package sk.teamsoft.amfcompiler;

import android.support.annotation.LayoutRes;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import dagger.Subcomponent;
import sk.teamsoft.amfannotations.AutoSubcomponent;
import sk.teamsoft.amfannotations.Layout;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@SuppressWarnings("unused")
@AutoService(Processor.class)
public class AmfProcessor extends AbstractProcessor {

    private static final String DAGGER_ANDROID_PCK = "dagger.android";
    private static final String INJECTOR = "AndroidInjector";
    private static final String BUILDER = "Builder";

    private Filer filer;

    @Override public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Layout.class.getCanonicalName());
        types.add(AutoSubcomponent.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        processLayouts(env);
        processSubcomponents(env);
        processAutoBuilder(env);
        return false;
    }

    private void processLayouts(RoundEnvironment env) {
        System.out.println("processing layouts");

        try {
            Map<String, Integer> layoutMap = new HashMap<>();

            for (Element element : env.getElementsAnnotatedWith(Layout.class)) {
                Layout annotation = element.getAnnotation(Layout.class);
                int value = annotation.value();
                final String name = element.getEnclosingElement().toString() + "." + element.getSimpleName();
                layoutMap.put(name, value);
            }

            TypeSpec.Builder result = TypeSpec.classBuilder("Layout_Processed")
                    .addModifiers(PUBLIC);
            CodeBlock.Builder codeBlock = CodeBlock.builder()
                    .add("map = new HashMap<String,Integer>();");
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getLayout")
                    .returns(Integer.class)
                    .addAnnotation(LayoutRes.class)
                    .addParameter(TypeName.get(String.class), "key")
                    .addModifiers(PUBLIC, STATIC)
                    .addCode(CodeBlock.builder()
                            .add("if (!map.containsKey(key)) { throw new IllegalStateException(\"Missing layout definition\"); } else { return (Integer) map.get(key); }")
                            .build());

            for (String key : layoutMap.keySet()) {
                codeBlock.add("map.put($S, $L);", key, layoutMap.get(key));
            }

            result.addModifiers(FINAL);
            result.addField(TypeName.get(layoutMap.getClass()), "map", STATIC, PRIVATE);
            result.addStaticBlock(codeBlock.build());
            result.addMethod(methodBuilder.build());

            JavaFile javaFile = JavaFile.builder("sk.teamsoft.amf", result.build())
                    .addFileComment("Generated AMF code. Do not modify!")
                    .build();

            try {
                System.out.println("write file Layout_Processed");
                javaFile.writeTo(filer);
            } catch (IOException e) {
                System.out.println("Unable to write file: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Cannot process layouts :" + e.getMessage());
        }
    }

    private void processSubcomponents(RoundEnvironment env) {
        System.out.println("processing subcomponents");

        for (Element element : env.getElementsAnnotatedWith(AutoSubcomponent.class)) {
            System.out.println("processing subcomponents for " + element);

            try {
                AutoSubcomponent annotation = element.getAnnotation(AutoSubcomponent.class);
                TypeMirror module = moduleMirror(annotation);
                TypeMirror presenter = presenterMirror(annotation);
                String subcomponentName = annotation.name() + "Subcomponent";

                AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(Subcomponent.class)
                        .addMember("modules", module.toString() + ".class");

                ParameterizedTypeName parent = ParameterizedTypeName.get(ClassName.get(DAGGER_ANDROID_PCK, INJECTOR), TypeName.get(element.asType()));

                TypeSpec.Builder result = TypeSpec.interfaceBuilder(subcomponentName)
                        .addSuperinterface(parent)
                        .addModifiers(PUBLIC)
                        .addAnnotation(annotationBuilder.build());

                result.addMethod(MethodSpec.methodBuilder("presenter")
                        .addModifiers(PUBLIC, ABSTRACT)
                        .returns(TypeName.get(presenter))
                        .build());

                ParameterizedTypeName innerBuilder = ParameterizedTypeName.get(ClassName.get(DAGGER_ANDROID_PCK, INJECTOR, BUILDER), TypeName.get(element.asType()));

                result.addType(TypeSpec.classBuilder("Builder")
                        .addModifiers(PUBLIC, STATIC, ABSTRACT)
                        .addAnnotation(AnnotationSpec.builder(Subcomponent.Builder.class).build())
                        .superclass(innerBuilder)
                        .build());

                JavaFile javaFile = JavaFile.builder("sk.teamsoft.amf.subcomponent", result.build())
                        .addFileComment("Generated AMF Subcomponent code. Do not modify!")
                        .build();

                try {
                    System.out.println("write file " + subcomponentName);
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    System.out.println("Unable to write file: " + e.getMessage());
                    error(element, "Unable to write file");
                }
            } catch (Exception e) {
                System.out.println("Cannot generate subcomponent for " + element + " :" + e.getMessage());
                error(element, "Cannot generate subcomponent ERROR:%s", e.getMessage());
            }
        }
    }

    private void processAutoBuilder(RoundEnvironment env) {
        System.out.println("processing AutoBuilder");

        //TODO
    }

    private TypeMirror moduleMirror(AutoSubcomponent annotation) {
        TypeMirror value = null;
        if (annotation != null) {
            try {
                annotation.module();
            } catch (MirroredTypeException mte) {
                value = mte.getTypeMirror();
            }
        }
        return value;
    }

    private TypeMirror presenterMirror(AutoSubcomponent annotation) {
        TypeMirror value = null;
        if (annotation != null) {
            try {
                annotation.presenter();
            } catch (MirroredTypeException mte) {
                value = mte.getTypeMirror();
            }
        }
        return value;
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }
}
