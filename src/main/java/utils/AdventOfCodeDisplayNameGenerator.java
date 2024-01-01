package utils;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class AdventOfCodeDisplayNameGenerator extends DisplayNameGenerator.Standard {

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        final var annotations = testClass.getDeclaredAnnotationsByType(AdventOfCode.class);
        if (annotations.length == 0) {
            return super.generateDisplayNameForClass(testClass);
        }

        final var annotation = annotations[0];
        if (annotation.name().isBlank()) {
            return super.generateDisplayNameForClass(testClass);
        }

        return String.format("%d, Day %d: %s", annotation.year(), annotation.day(), annotation.name());
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return this.replaceCamelCase(testMethod.getName());
    }

    private String replaceCamelCase(String camelCase) {
        StringBuilder result = new StringBuilder();
        result.append(camelCase.charAt(0));
        for (int i=1; i<camelCase.length(); i++) {
            if (Character.isUpperCase(camelCase.charAt(i))) {
                result.append(' ');
                result.append(Character.toLowerCase(camelCase.charAt(i)));
            } else {
                result.append(camelCase.charAt(i));
            }
        }
        return result.toString();
    }
}
