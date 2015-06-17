package com.InfinityRaider.AgriCraft.compatibility.bloodmagic;

import WayofTime.alchemicalWizardry.api.harvest.IHarvestHandler;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.world.World;

@Optional.Interface(modid = Names.Mods.bloodMagic, iface = "WayofTime.alchemicalWizardry.api.harvest.IHarvestHandler")
public class HarvestHandler implements IHarvestHandler {
    @Override
    public boolean harvestAndPlant(World world, int x, int y, int z, Block block, int meta) {
        if(!(block instanceof BlockCrop)) {
            return false;
        }
        if(meta<7) {
            return false;
        }
        ((BlockCrop) block).harvest(world, x, y, z, null);
        return true;
    }
}
