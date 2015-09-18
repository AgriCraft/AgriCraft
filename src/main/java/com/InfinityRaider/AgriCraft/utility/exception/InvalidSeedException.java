package com.InfinityRaider.AgriCraft.utility.exception;

public class InvalidSeedException extends Exception {
    public InvalidSeedException() {
        super("This is not a vaild seed for AgriCraft. The seed is either blacklisted, not registered correctly, or not a seed.");
    }
}
