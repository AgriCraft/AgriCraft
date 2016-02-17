package com.InfinityRaider.AgriCraft.utility.exception;

public class MissingArgumentsException extends Exception {
    public MissingArgumentsException() {
        super("Not all necessary parameters where given to create a new plant");
    }
}
