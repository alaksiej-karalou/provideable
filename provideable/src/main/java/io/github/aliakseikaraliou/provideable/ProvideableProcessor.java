package io.github.aliakseikaraliou.provideable;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Generated;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedAnnotationTypes("io.github.aliakseikaraliou.provideable.Provideable")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class ProvideableProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Provideable.class)) {
			try {
				Provideable annotation = annotatedElement.getAnnotation(Provideable.class);

				if (annotatedElement instanceof TypeElement annotatedType) {
					processType(annotation, annotatedType);
				} else if (annotatedElement instanceof ExecutableElement executableElement) {
					processExecutable(annotation, executableElement);
				}
			} catch (ProvideableException e) {
				processingEnv.getMessager().printMessage(ERROR, e.getMessage());
			}
		}

		return false;
	}

	private void processType(Provideable annotation, TypeElement type) throws ProvideableException {
		ProvideableOptions options = ProvideableOptions.builder()
				.setPackageName(processingEnv.getElementUtils().getPackageOf(type).toString())
				.setTypeName(type.getSimpleName() + "Provider")
				.setMethodName("get" + type.getSimpleName())
				.setProvideable(annotation)
				.setTarget(type.asType())
				.build();

		process(options);
	}

	private void processExecutable(Provideable annotation, ExecutableElement executable) throws ProvideableException {
		if (executable.getParameters().size() > 0) {
			throw new ProvideableException("@Provideable annotated methods should not have parameters");
		}

		TypeElement returnType = processingEnv.getElementUtils().getTypeElement(executable.getReturnType().toString());

		ProvideableOptions options = ProvideableOptions.builder()
				.setPackageName(processingEnv.getElementUtils().getPackageOf(executable).toString())
				.setTypeName(returnType.getSimpleName() + "Provider")
				.setMethodName(executable.getSimpleName().toString())
				.setProvideable(annotation)
				.setTarget(executable.getReturnType())
				.build();

		process(options);
	}

	private void process(ProvideableOptions options) throws ProvideableException {
		try {
			JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(options.getFullName());

			try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
				JavaFile javaFile = generateProvider(options);
				javaFile.writeTo(out);
			}
		} catch (IOException e) {
			throw new ProvideableException(e);
		}
	}

	private JavaFile generateProvider(ProvideableOptions options) {
		AnnotationSpec generatedAnnotation = AnnotationSpec.builder(Generated.class)
				.addMember("value", "\"%s\"".formatted(ProvideableProcessor.class.getName()))
				.addMember("date", "\"%s\"".formatted(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT)))
				.build();

		MethodSpec method = MethodSpec.methodBuilder(options.getMethodName())
				.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
				.returns(TypeName.get(options.getTarget()))
				.build();

		TypeSpec provider = TypeSpec.interfaceBuilder(options.getTypeName())
				.addMethod(method)
				.addAnnotation(generatedAnnotation)
				.addAnnotation(FunctionalInterface.class)
				.addModifiers(Modifier.PUBLIC)
				.build();

		return JavaFile.builder(options.getPackageName(), provider)
				.build();
	}
}
