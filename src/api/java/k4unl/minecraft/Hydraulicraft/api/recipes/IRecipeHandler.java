package k4unl.minecraft.Hydraulicraft.api.recipes;

/**
 * @author Koen Beckers (K-4U)
 */
public interface IRecipeHandler {
    public void addCrushingRecipe(IFluidRecipe toAdd);

    public void addAssemblerRecipe(IFluidRecipe toAdd);

    public void addWasherRecipe(IFluidRecipe toAdd);

    public void addFilterRecipe(IFluidRecipe toAdd);
}
