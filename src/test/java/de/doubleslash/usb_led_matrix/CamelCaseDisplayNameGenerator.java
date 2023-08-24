package de.doubleslash.usb_led_matrix;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * This is a display name generator which transforms class and method names written in camel case to separate readable
 * words. To use this class just write @DisplayNameGeneration(CamelCaseDisplayNameGenerator.class). To use it as default
 * display name generator add<br/>
 * junit.jupiter.displayname.generator.default = de.doubleslash.usb_led_matrix.CamelCaseDisplayNameGenerator<br/>
 * to src/test/resources/junit-platform.properties.<br/>
 * (Inspired by <a href=
 * "https://www.baeldung.com/junit-custom-display-name-generator#camel-case-replacement">https://www.baeldung.com/junit-custom-display-name-generator#camel-case-replacement</a>)
 * 
 * @author portmann
 */
public class CamelCaseDisplayNameGenerator extends DisplayNameGenerator.Standard {
   @Override
   public String generateDisplayNameForClass(final Class<?> testClass) {
      return convertCamelCaseNameToSentence(super.generateDisplayNameForClass(testClass));
   }

   @Override
   public String generateDisplayNameForNestedClass(final Class<?> nestedClass) {
      return convertCamelCaseNameToSentence(super.generateDisplayNameForNestedClass(nestedClass));
   }

   @Override
   public String generateDisplayNameForMethod(final Class<?> testClass, final Method testMethod) {
      return convertCamelCaseNameToSentence(testMethod.getName()) + " "
            + DisplayNameGenerator.parameterTypesAsString(testMethod);
   }

   private String convertCamelCaseNameToSentence(final String camelCaseName) {
      final StringBuilder sentence = new StringBuilder();
      sentence.append(Character.toUpperCase(camelCaseName.charAt(0)));
      for (int i = 1; i < camelCaseName.length(); i++) {
         if (Character.isUpperCase(camelCaseName.charAt(i))) {
            if (Character.isLowerCase(camelCaseName.charAt(i - 1))
                  || (i < (camelCaseName.length() - 1) && !Character.isUpperCase(camelCaseName.charAt(i + 1)))) {
               sentence.append(' ');
            }
         }
         sentence.append(camelCaseName.charAt(i));
      }
      return sentence.toString();
   }
}
