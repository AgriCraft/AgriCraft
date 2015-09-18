package com.InfinityRaider.AgriCraft.utility.exception;

public final class DuplicateCropPlantException extends Exception {
    public DuplicateCropPlantException() {
        super("This plant is already registered.");
    }
}
