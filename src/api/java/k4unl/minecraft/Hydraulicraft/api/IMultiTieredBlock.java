package k4unl.minecraft.Hydraulicraft.api;

import net.minecraft.world.IBlockAccess;

public interface IMultiTieredBlock {

    PressureTier getTier(int metadata);

    PressureTier getTier(IBlockAccess world, int x, int y, int z);
}
