package com.infinityraider.agricraft.plugins.theoneprobe;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.world.BlockGreenHouseMonitor;
import com.infinityraider.agricraft.reference.Names;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

final class AgriProbeGreenHouseMonitorInfoProvider implements IProbeInfoProvider {
    private final ResourceLocation id;

    protected AgriProbeGreenHouseMonitorInfoProvider() {
        this.id = new ResourceLocation(AgriCraft.instance.getModId(), Names.Mods.THE_ONE_PROBE + "_greenhouse");
    }

    @Override
    public ResourceLocation getID() {
        return this.id;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player,
                             Level world, BlockState state, IProbeHitData hitData) {
        this.addGreenHouseMonitorProbeInfo(info, world, hitData.getPos());
    }

    protected void addGreenHouseMonitorProbeInfo(IProbeInfo info, Level world, BlockPos pos) {
        BlockGreenHouseMonitor monitor = AgriCraft.instance.getModBlockRegistry().getGreenHouseMonitorBlock();
        BlockState state = world.getBlockState(pos);
        if(state.getBlock() == monitor) {
            info.text(monitor.getFeedbackMessage(state));
        }
    }
}
