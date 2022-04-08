package com.infinityraider.agricraft.plugins.theoneprobe;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

final class AgriProbeIrrigationBlockInfoProvider implements IProbeInfoProvider {
    private final ResourceLocation id;

    protected AgriProbeIrrigationBlockInfoProvider() {
        this.id = new ResourceLocation(AgriCraft.instance.getModId(), Names.Mods.THE_ONE_PROBE + "_irrigation");
    }

    @Override
    public ResourceLocation getID() {
        return this.id;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player,
                             Level world, BlockState state, IProbeHitData hitData) {
        this.addIrrigationProbeInfo(info, world, hitData.getPos());
    }

    protected void addIrrigationProbeInfo(IProbeInfo info, Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileEntityIrrigationComponent) {
            if(tile instanceof TileEntityIrrigationChannel) {
                TileEntityIrrigationChannel channel = (TileEntityIrrigationChannel) tile;
                if(channel.hasValve()) {
                    if(channel.isOpen()) {
                        info.text(AgriToolTips.VALVE_INFO_OPEN);
                    } else {
                        info.text(AgriToolTips.VALVE_INFO_CLOSED);
                    }
                }
            }
            TileEntityIrrigationComponent component = (TileEntityIrrigationComponent) tile;
            info.progress(component.getContent(), component.getCapacity(), new ProgressStyle()
                    .filledColor(0xff327DCD)
                    .alternateFilledColor(0xff3732CD)
                    .suffix(" mB")
            );
        }
    }
}
