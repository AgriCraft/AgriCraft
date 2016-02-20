package com.infinityraider.agricraft.api.v1;

import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface IAgriCraftSeed extends IAgriCraftRenderable, IPlantable {
    /** Gets a list of mutations with this seed as result, can be empty but should never be null */
    List<IMutation> getMutations();

    /** Gets the plant for this seed */
    IAgriCraftPlant getPlant();

    /** Gets the tier for this seed */
    int tier();

    /** Gets the information for the journal for this seed */
    @SideOnly(Side.CLIENT)
    String getInformation();

}
