/*
 */
package com.infinityraider.agricraft.api.irrigation;

import net.minecraft.util.IStringSerializable;

/**
 *
 * @author Ryan
 */
public enum IrrigationConnectionType implements IStringSerializable {
    NONE(false, false, false),
    PRIMARY(true, true, false),
    AUXILIARY(true, false, true);

    private final boolean connection;
    private final boolean primary;
    private final boolean auxiliary;

    private IrrigationConnectionType(boolean connection, boolean primary, boolean auxiliary) {
        this.connection = connection;
        this.primary = primary;
        this.auxiliary = auxiliary;
    }

    public boolean isConnection() {
        return connection;
    }

    public boolean isPrimary() {
        return primary;
    }

    public boolean isAuxiliary() {
        return auxiliary;
    }

    @Override
    public String getName() {
        return this.name().toLowerCase();
    }

    public static IrrigationConnectionType fromIndex(int index) {
        index = (index < 0) ? 0 : index;
        index = (index >= IrrigationConnectionType.values().length) ? IrrigationConnectionType.values().length - 1 : index;
        return IrrigationConnectionType.values()[index];
    }

}
