/*
 * 
 */
package com.infinityraider.agricraft.utility;

/**
 *
 * @author RlonRyan
 */
public final class StringUtil {

    // Private constructor to prevent instantiation of static helper method class.
    private StringUtil() {
    }

    public static String convertCamelCaseToSnakeCase(String stringCamelCase) {
        return stringCamelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
    
    public static String convertSnakeCaseToCamelCase(String stringSnakeCase) {
        return stringSnakeCase.replaceAll("_([a-z])", "\\U$1");
    }

}
