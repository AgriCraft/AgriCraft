/*
 * 
 */
package com.infinityraider.agricraft.api.v1.util;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 *
 * @author Ryan
 */
public final class UnlistedPropertyByte implements IUnlistedProperty<Byte> {
    
    private final String name;
    private final byte min;
    private final byte max;

    public UnlistedPropertyByte(@Nonnull String name) {
        this(name, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    public UnlistedPropertyByte(@Nonnull String name, byte min, byte max) {
        this.name = Preconditions.checkNotNull(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isValid(Byte v) {
        return (v >= min) && (v <= max);
    }

    @Override
    public Class<Byte> getType() {
        return Byte.class;
    }

    @Override
    public String valueToString(Byte v) {
        return Byte.toString(v);
    }
    
    public byte getValue(@Nonnull IBlockState state, byte orElse) {
        Preconditions.checkNotNull(state);
        if (state instanceof IExtendedBlockState) {
            final Byte wrapped = ((IExtendedBlockState) state).getValue(this);
            if (wrapped != null) {
                return wrapped;
            }
        }
        return orElse;
    }
    
    @Nonnull
    public <T extends IBlockState> T setValue(@Nonnull T state, byte value) {
        Preconditions.checkNotNull(state);
        if (state instanceof IExtendedBlockState) {
            return (T)((IExtendedBlockState)state).withProperty(this, value);
        } else {
            return state;
        }
    }
    
}
