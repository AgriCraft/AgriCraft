package com.infinityraider.agricraft.plugins.theoneprobe;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.core.BlockCropPlant;
import com.infinityraider.agricraft.content.core.TileEntityCropPlant;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class AgriProbeBlockDisplayOverride implements IBlockDisplayOverride {
    private static final ITextComponent MOD_ID = new StringTextComponent("{=m=}Agricraft"); // TOP uses {=m=} to format the text

    protected AgriProbeBlockDisplayOverride() {
        super();
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, PlayerEntity playerEntity, World world,
                                        BlockState state, IProbeHitData hitData) {
        if(state.getBlock() instanceof BlockCropPlant) {
            TileEntity tile = world.getTileEntity(hitData.getPos());
            if(tile instanceof TileEntityCropPlant) {
                TileEntityCropPlant crop = (TileEntityCropPlant) tile;
                IAgriPlant plant = crop.getPlant();
                if(plant.isPlant()) {
                    this.addData(info, plant);
                    return true;
                }
            }
        }
        return false;
    }

    protected void addData(IProbeInfo info, IAgriPlant plant) {
        ItemStack seed = plant.toItemStack();
            info.horizontal()
                    .item(seed)
                    .vertical()
                    .itemLabel(seed)
                    .text(MOD_ID);
    }
}

