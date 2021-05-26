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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class AgriProbeIrrigationBlockInfoProvider implements IProbeInfoProvider {
    private final String id;

    protected AgriProbeIrrigationBlockInfoProvider() {
        this.id = AgriCraft.instance.getModId() + ":" + Names.Mods.THE_ONE_PROBE + "_irrigation";
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, PlayerEntity player,
                             World world, BlockState state, IProbeHitData hitData) {
        this.addIrrigationProbeInfo(info, world, hitData.getPos());
    }

    protected void addIrrigationProbeInfo(IProbeInfo info, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
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
