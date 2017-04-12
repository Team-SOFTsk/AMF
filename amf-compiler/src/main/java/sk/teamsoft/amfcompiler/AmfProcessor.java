package sk.teamsoft.amfcompiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

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
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import dagger.multibindings.IntoMap;
import sk.teamsoft.amfannotations.AutoSubcomponent;
import sk.teamsoft.amfannotations.Layout;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@SuppressWarnings("unused")
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AmfProcessor extends AbstractProcessor {

    private static final String DAGGER_ANDROID_PCK = "dagger.android";
    private static final String INJECTOR = "AndroidInjector";
    private static final String INJECTOR_BUILDER = "Builder";
    private static final String INJECTOR_FACTORY = "Factory";
    private static final String SUBCOMPONENT_BUILDER = "Builder";

    private static final String SUBCOMPONENT_PACKAGE = "sk.teamsoft.amf.subcomponent";
    private static final String BUILDERS_PACKAGE = "sk.teamsoft.amf.builders";

    private Filer filer;
    private boolean generatedLayout = false;
    private boolean generatedSubcomponents = false;
    private boolean generatedBuilders = false;

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
        Map<TypeMirror, TypeName> typeMirrorTypeNameMap = processSubcomponents(env);
        processAutoBuilder(env, typeMirrorTypeNameMap);

        return false;
    }

    private void processLayouts(RoundEnvironment env) {
        if (generatedLayout) {
            System.out.println("layouts already processed");
            return;
        }

        System.out.println("processing layouts");
        generatedLayout = true;

        try {
            final CodeBlock.Builder codeBlock = CodeBlock.builder()
                    .add("MAP = new java.util.HashMap<String,Integer>();");

            env.getElementsAnnotatedWith(Layout.class).forEach(element -> {
                final Layout annotation = element.getAnnotation(Layout.class);
                final String name = element.getEnclosingElement().toString() + "." + element.getSimpleName();
                //TODO validate value first?
                codeBlock.add("MAP.put($S, $L);", name, annotation.value());
            });

            final TypeSpec.Builder result = TypeSpec.classBuilder("Layout_Processed");
            result.addModifiers(PUBLIC, FINAL);
            result.addField(ParameterizedTypeName.get(Map.class, String.class, Integer.class),
                    "MAP", STATIC, PUBLIC, FINAL);
            result.addStaticBlock(codeBlock.build());

            final JavaFile javaFile = JavaFile.builder("sk.teamsoft.amf", result.build())
                    .addFileComment("Generated AMF code. Do not modify!")
                    .build();

            System.out.println("write file Layout_Processed");
            javaFile.writeTo(filer);
        } catch (Exception e) {
            System.out.println("Cannot process layouts :" + e.getMessage());
        }
    }

    private Map<TypeMirror, TypeName> processSubcomponents(RoundEnvironment env) {
        if (generatedSubcomponents) {
            System.out.println("subcomponents already processed");
            return null;
        }

        System.out.println("processing subcomponents");
        generatedSubcomponents = true;

        //map of (key) Element and (value) Subcomponent types
        final Map<TypeMirror, TypeName> mirrorMap = new HashMap<>();

        for (Element element : env.getElementsAnnotatedWith(AutoSubcomponent.class)) {
            System.out.println("processing subcomponents for " + element);

            try {
                AutoSubcomponent annotation = element.getAnnotation(AutoSubcomponent.class);
                TypeMirror module = moduleMirror(annotation);
                TypeMirror presenter = presenterMirror(annotation);
                String subcomponentName = annotation.name() + "Subcomponent";

                AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(Subcomponent.class)
                        .addMember("modules", module.toString() + ".class");

                ParameterizedTypeName parent = ParameterizedTypeName.get(
                        ClassName.get(DAGGER_ANDROID_PCK, INJECTOR),
                        TypeName.get(element.asType()));

                TypeSpec.Builder result = TypeSpec.interfaceBuilder(subcomponentName)
                        .addSuperinterface(parent)
                        .addModifiers(PUBLIC)
                        .addAnnotation(annotationBuilder.build());

                result.addMethod(MethodSpec.methodBuilder("presenter")
                        .addModifiers(PUBLIC, ABSTRACT)
                        .returns(TypeName.get(presenter))
                        .build());

                ParameterizedTypeName innerBuilder = ParameterizedTypeName.get(
                        ClassName.get(DAGGER_ANDROID_PCK, INJECTOR, INJECTOR_BUILDER),
                        TypeName.get(element.asType()));

                result.addType(TypeSpec.classBuilder(SUBCOMPONENT_BUILDER)
                        .addModifiers(PUBLIC, STATIC, ABSTRACT)
                        .addAnnotation(AnnotationSpec.builder(Subcomponent.Builder.class).build())
                        .superclass(innerBuilder)
                        .build());

                JavaFile javaFile = JavaFile.builder(SUBCOMPONENT_PACKAGE, result.build())
                        .addFileComment("Generated AMF Subcomponent code. Do not modify!")
                        .build();

                mirrorMap.put(element.asType(), ClassName.get(SUBCOMPONENT_PACKAGE, subcomponentName, SUBCOMPONENT_BUILDER));

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

        return mirrorMap;
    }

    private void processAutoBuilder(RoundEnvironment env, Map<TypeMirror, TypeName> mirrorMap) {
        if (generatedBuilders) {
            System.out.println("builders already processed");
            return;
        }

        System.out.println("processing builders");
        generatedBuilders = true;

        try {
            TypeSpec.Builder result = TypeSpec.interfaceBuilder("BuildersModule")
                    .addModifiers(PUBLIC)
                    .addAnnotation(Module.class);

            mirrorMap.forEach((typeMirror, typeName) -> {
                AnnotationSpec keyAnnotation = AnnotationSpec.builder(ClassName.get("dagger.android.support", "FragmentKey"))
                        .addMember("value", typeMirror.toString() + ".class")
                        .build();

                WildcardTypeName wildcard = WildcardTypeName.subtypeOf(ClassName.get("android.support.v4.app", "Fragment"));
                ParameterizedTypeName factory = ParameterizedTypeName.get(
                        ClassName.get(DAGGER_ANDROID_PCK, INJECTOR, INJECTOR_FACTORY), wildcard);

                //strip . separators and transform to start with lowercase
                String[] split = typeMirror.toString().split("\\.");
                String name = split[split.length - 1];
                name = name.replace(name.substring(0, 1), name.substring(0, 1).toLowerCase());

                result.addMethod(MethodSpec.methodBuilder(name + "Factory")
                        .addModifiers(PUBLIC, ABSTRACT)
                        .addAnnotation(Binds.class)
                        .addAnnotation(IntoMap.class)
                        .addAnnotation(keyAnnotation)
                        .addParameter(ParameterSpec.builder(typeName, "builder").build())
                        .returns(factory)
                        .build());
            });

            JavaFile javaFile = JavaFile.builder(BUILDERS_PACKAGE, result.build())
                    .addFileComment("Generated AMF Builders module. Do not modify!")
                    .build();

            try {
                System.out.println("write file BuildersModule");
                javaFile.writeTo(filer);
            } catch (IOException e) {
                System.out.println("Unable to write file: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Cannot generate Builders module :" + e.getMessage());
        }
    }

    //uses trick to get typeMirror of a class
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

    //uses trick to get typeMirror of a class
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
