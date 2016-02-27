package k4unl.minecraft.Hydraulicraft.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IPressurizableItem {
    float getPressure(ItemStack itemStack);

    void setPressure(ItemStack itemStack, float newStored);

    float getMaxPressure();

    FluidStack getFluid(ItemStack itemStack);

    void setFluid(ItemStack itemStack, FluidStack fluidStack);

    float getMaxFluid();
}
