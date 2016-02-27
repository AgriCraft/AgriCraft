package k4unl.minecraft.Hydraulicraft.api;

import codechicken.multipart.TMultiPart;
import cpw.mods.fml.common.Loader;
import k4unl.minecraft.Hydraulicraft.api.recipes.IRecipeHandler;
import k4unl.minecraft.Hydraulicraft.lib.config.ModInfo;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Koen Beckers (K-4U) & MineMaarten
 */
public class HCApi {
    private static IHCApi instance;

    public static IHCApi getInstance() {

        return instance;
    }

    public static interface IHCApi {
        IRecipeHandler getRecipeHandler();

        ITrolleyRegistrar getTrolleyRegistrar();

        IBaseClass getBaseClass(TileEntity target, int maxStorage);

        IBaseClass getBaseClass(TMultiPart target, int maxStorage);
    }

    /**
     * For internal use only, don't call it.
     *
     * @param inst
     */
    public static void init(IHCApi inst) {

        if (instance == null && Loader.instance().activeModContainer().getModId().equals(ModInfo.ID)) {
            instance = inst;
        } else {
            throw new IllegalStateException("This method should be called from Hydraulicraft only!");
        }
    }
}
