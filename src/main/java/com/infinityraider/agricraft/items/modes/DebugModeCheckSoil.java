/*
 */
package com.infinityraider.agricraft.items.modes;

import com.infinityraider.agricraft.api.util.FuzzyStack;
import com.infinityraider.agricraft.apiimpl.SoilRegistry;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 *
 * @author RlonRyan
 */
public class DebugModeCheckSoil extends DebugMode {

    @Override
    public String debugName() {
        return "check soil";
    }

    @Override
    public void debugAction(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        FuzzyStack soil = new FuzzyStack(world.getBlockState(pos));
        String type = SoilRegistry.getInstance().getSoils().stream()
                .filter(s -> s.isVarient(soil))
                .map(s -> s.getName())
                .findFirst()
                .orElse("Unknown Soil");
        player.addChatComponentMessage(new TextComponentString("Soil type: \'" + type + "\'"));
    }

}
