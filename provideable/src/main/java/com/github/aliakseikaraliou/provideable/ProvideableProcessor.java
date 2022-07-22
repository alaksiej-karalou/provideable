package com.github.aliakseikaraliou.provideable;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedAnnotationTypes("com.github.aliakseikaraliou.provideable.Provideable")
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
				}
			} catch (IOException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		return true;
	}

	private void processType(Provideable annotation, TypeElement annotated) throws IOException, ClassNotFoundException {
		ProvideableOptions options = ProvideableOptions.builder()
				.setPackageName(processingEnv.getElementUtils().getPackageOf(annotated).toString())
				.setTypeName(annotated.getSimpleName() + "Provider")
				.setMethodName("get" + annotated.getSimpleName())
				.setProvideable(annotation)
				.setTarget(annotated.asType())
				.build();

		provide(options);
	}

	private void provide(ProvideableOptions options) throws IOException {
		JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(options.getFullName());

		try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
			JavaFile javaFile = generateProvider(options);
			javaFile.writeTo(out);
		}
	}

	private JavaFile generateProvider(ProvideableOptions options) {
		MethodSpec method = MethodSpec.methodBuilder(options.getMethodName())
				.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
				.returns(TypeName.get(options.getTarget()))
				.build();

		TypeSpec provider = TypeSpec.interfaceBuilder(options.getTypeName())
				.addMethod(method)
				.addModifiers(Modifier.PUBLIC)
				.build();

		return JavaFile.builder(options.getPackageName(), provider)
				.build();
	}
}
