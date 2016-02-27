package k4unl.minecraft.Hydraulicraft.api.recipes;

import k4unl.minecraft.Hydraulicraft.lib.recipes.ChancedStack;
import k4unl.minecraft.Hydraulicraft.lib.recipes.IFluidInventory;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FluidShapedOreRecipe extends ShapedOreRecipe implements IFluidRecipe {
    List<FluidStack> inputFluids;
    List<FluidStack> outputFluids;
    int   craftingTime = 1;
    float pressure     = 0.1f;
    private ChancedStack resultChancedStack;

    public FluidShapedOreRecipe(Block result, Object... recipe) {
        super(result, recipe);
    }

    public FluidShapedOreRecipe(Item result, Object... recipe) {
        super(result, recipe);
    }

    public FluidShapedOreRecipe(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    public FluidShapedOreRecipe(ChancedStack result, Object... recipe) {
        super(new ItemStack(Blocks.cobblestone_wall), recipe); // just to feed it something
        this.resultChancedStack = result;
    }

    public FluidShapedOreRecipe addFluidInput(FluidStack fluidStack) {
        if (inputFluids == null)
            inputFluids = new ArrayList<FluidStack>();

        inputFluids.add(fluidStack);

        return this;
    }

    public FluidShapedOreRecipe addFluidOutput(FluidStack fluidStack) {
        if (outputFluids == null)
            outputFluids = new ArrayList<FluidStack>();

        outputFluids.add(fluidStack);
        return this;
    }

    @Override
    public List<FluidStack> getInputFluids() {
        return inputFluids;
    }

    @Override
    public List<FluidStack> getOutputFluids() {
        return outputFluids;
    }

    @Override
    public boolean matches(IFluidInventory inventory) {
        if (!super.matches(inventory.getInventory(), null))
            return false;

        int fluidsMatched = 0;
        if (inputFluids != null) {
            for (FluidStack fluid : getInputFluids()) {
                FluidStack fluidCopy = fluid.copy();
                fluidCopy.amount /= craftingTime;
                FluidStack drained = inventory.craftingDrain(fluidCopy, false);
                if (drained != null && drained.amount == fluidCopy.amount)
                    fluidsMatched++;
            }

            if (fluidsMatched != getInputFluids().size())
                return false;
        }

        fluidsMatched = 0;
        if (outputFluids != null) {
            for (FluidStack fluidStack : getOutputFluids()) {
                FluidStack fluidCopy = fluidStack.copy();
                fluidCopy.amount /= craftingTime;
                if (inventory.craftingFill(fluidCopy, false) == fluidCopy.amount)
                    fluidsMatched++;
            }

            return fluidsMatched == getOutputFluids().size();
        }

        return true;
    }

    @Override
    public int getCraftingTime() {
        return craftingTime;
    }

    public FluidShapedOreRecipe setCraftingTime(int craftingTime) {
        this.craftingTime = craftingTime;
        return this;
    }

    @Override
    public float getPressure() {
        return pressure;
    }

    public FluidShapedOreRecipe setPressure(float pressure) {
        this.pressure = pressure;
        return this;
    }

    @Override
    public ItemStack getRecipeOutput() {
        if (resultChancedStack != null)
            throw new IllegalArgumentException("Tried to access normal recipe output when chanced stack was set up!");

        return super.getRecipeOutput();
    }

    @Override
    public Object[] getInputItems() {
        return super.getInput();
    }

    @Override
    public ItemStack[] getChancedDrops() {
        if (resultChancedStack == null)
            throw new IllegalArgumentException("Tried to access chanced stack when they were not set up!");

        return resultChancedStack.getDrops(new Random()); // TODO get world seed?
    }
}
