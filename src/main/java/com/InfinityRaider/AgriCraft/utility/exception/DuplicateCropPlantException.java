package com.infinityraider.agricraft.utility.exception;

public final class DuplicateCropPlantException extends Exception {
    public DuplicateCropPlantException() {
        super("This plant is already registered");
    }
}
