/*
 */
package com.infinityraider.agricraft.api.v1.util;

/**
 * Enum for representing the result state of actions.
 */
public enum MethodResult {

    /**
     * The method was executed successfully.
     */
    SUCCESS,
    /**
     * The method skipped execution.
     */
    PASS,
    /**
     * The method failed during its execution.
     */
    FAIL;

}
