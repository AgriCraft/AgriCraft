package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.v1.misc.IAgriPeripheralMethod;

public class MethodException extends Exception {

    private IAgriPeripheralMethod method;
    private String msg;

    public MethodException(IAgriPeripheralMethod method, String msg) {
        this.method = method;
        this.msg = msg;
    }

    public String getDescription() {
        return "Method '" + method.getId() + "' errored: " + msg;
    }
}
