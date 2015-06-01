package com.InfinityRaider.AgriCraft.utility.exception;

public class InvalidSeedException extends Exception {
    public InvalidSeedException() {
        super("This seed is not a valid seed for AgriCraft, it is either blacklisted, not registered correctly or it is simply not a seed");
    }
}
