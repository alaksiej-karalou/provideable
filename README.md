# Provideable

## Usage

This java library will help you to simplify process of creating provider (especially helpful in Dagger). 
It uses Java annotation processing to generate provider at build time

Provider is a
functional interface returns some type:

```java

@FunctionalInterface
public interface SomeClassProvider {
	SomeClass getSomeCode();
}
```

To generate a provider you just need to annotate your class with `@Provideable` annotation.

You can modify naming in the provider using such annotation properties:

| Nme         | Description                   | Default                            |
|-------------|-------------------------------|------------------------------------|
| packageName | Package name for the provider | *the same as target class package* |
| name        | Provider interface name       | *Target class name* + `Provider`   |   
| methodName  | Provider method name          | `get` + *Target class name*        |

## Installation

### Maven

```xml
<project>
    ...
    <dependency>
        <groupId>com.github.aliakseikaraliou.provideable</groupId>
        <artifactId>provideable</artifactId>
        <version>0.0.1</version>
    </dependency>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <annotationProcessorPaths>
                        <annotationProcessorPath>
                            <groupId>com.github.aliakseikaraliou.provideable</groupId>
                            <artifactId>provideable</artifactId>
                            <version>0.0.1</version>
                        </annotationProcessorPath>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
    

</project>
```

Then execute `mvn clean install` to rebuild the project