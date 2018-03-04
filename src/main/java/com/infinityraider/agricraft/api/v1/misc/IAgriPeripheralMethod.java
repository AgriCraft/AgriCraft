package com.infinityraider.agricraft.api.v1.misc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IAgriPeripheralMethod extends IAgriRegisterable {

    @Nonnull
    String getDescription();

    @Nonnull
    String getSignature();

    @Nullable
    Object[] call(@Nullable IBlockAccess world, @Nullable BlockPos pos, @Nullable ItemStack journal, @Nullable Object... args) throws InvocationException;

    public static class InvocationException extends Exception {

        private final IAgriPeripheralMethod method;
        private final String msg;

        public InvocationException(IAgriPeripheralMethod method, String msg) {
            this.method = method;
            this.msg = msg;
        }

        public String getDescription() {
            return "Method '" + method.getId() + "' errored: " + msg;
        }

    }

}
