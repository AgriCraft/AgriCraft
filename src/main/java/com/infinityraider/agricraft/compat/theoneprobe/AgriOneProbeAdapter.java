/*
 */
package com.infinityraider.agricraft.compat.theoneprobe;

import com.infinityraider.agricraft.api.v1.misc.IAgriDisplayable;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.ArrayList;
import java.util.List;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 *
 * @author Ryan
 */
public class AgriOneProbeAdapter implements IProbeInfoProvider {

    @Override
    public String getID() {
        return "agricraft";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        final List<String> lines = new ArrayList<>();
        WorldHelper.getBlock(world, data.getPos(), IAgriDisplayable.class).ifPresent(e -> e.addDisplayInfo(lines::add));
        WorldHelper.getTile(world, data.getPos(), IAgriDisplayable.class).ifPresent(e -> e.addDisplayInfo(lines::add));
        lines.stream().forEach(probeInfo::text);
    }

}
