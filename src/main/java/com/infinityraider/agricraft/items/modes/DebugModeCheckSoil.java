/*
 */
package com.infinityraider.agricraft.items.modes;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 *
 *
 */
public class DebugModeCheckSoil extends DebugMode {

    @Override
    public String debugName() {
        return "check soil";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        FuzzyStack soil = FuzzyStack.from(world.getBlockState(pos)).orElse(null);
        String type = AgriApi.getSoilRegistry().all().stream()
                .filter(s -> s.isVariant(soil))
                .map(s -> s.getName())
                .findFirst()
                .orElse("Unknown Soil");
        MessageUtil.messagePlayer(player, "{0} Soil Info:", FMLCommonHandler.instance().getSide());
        MessageUtil.messagePlayer(player, " - Soil Type: \"{0}\"", type);
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        // NOP
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        // NOP
    }

}
