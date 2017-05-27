package com.infinityraider.agricraft.compat.computer.methods;

public class MethodException extends Exception {

    private IMethod method;
    private String msg;

    public MethodException(IMethod method, String msg) {
        this.method = method;
        this.msg = msg;
    }

    public String getDescription() {
        return "Method '" + method.getName() + "' errored: " + msg;
    }
}
