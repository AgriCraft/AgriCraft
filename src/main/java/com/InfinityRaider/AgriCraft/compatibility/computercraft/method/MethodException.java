package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

public class MethodException extends Exception {
    private MethodCropBase method;
    private String msg;

    public MethodException(MethodCropBase method, String msg) {
        this.method = method;
        this.msg = msg;
    }

    public String getDescription() {
        return "Method '"+method+"' errored: "+msg;
    }
}
