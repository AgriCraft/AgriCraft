package com.infinityraider.agricraft.plugins.theoneprobe;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.core.BlockCrop;
import com.infinityraider.agricraft.content.core.TileEntityCrop;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AgriProbeCropBlockDisplayOverride implements IBlockDisplayOverride {
    private static final TextComponent MOD_ID = new TextComponent("{=m=}Agricraft"); // TOP uses {=m=} to format the text

    protected AgriProbeCropBlockDisplayOverride() {
        super();
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, Player playerEntity, Level world,
                                        BlockState state, IProbeHitData hitData) {
        if(state.getBlock() instanceof BlockCrop) {
            BlockEntity tile = world.getBlockEntity(hitData.getPos());
            if(tile instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) tile;
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

