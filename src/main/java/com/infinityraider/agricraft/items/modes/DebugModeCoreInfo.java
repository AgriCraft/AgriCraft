/*
 */
package com.infinityraider.agricraft.items.modes;

import static com.infinityraider.infinitylib.utility.MessageUtil.*;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.infinityraider.infinitylib.utility.debug.DebugMode;

import com.agricraft.agricore.core.AgriCore;

/**
 *
 *
 */
public class DebugModeCoreInfo extends DebugMode {

    @Override
    public String debugName() {
        return "core info";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        messagePlayer(player, "{0} Info:", FMLCommonHandler.instance().getSide());
        messagePlayer(player, "========================================");
        messagePlayer(player, "AgriPlants Hash: {0}", AgriCore.getPlants().hashCode());
        messagePlayer(player, " - Plant Count: {0}", AgriCore.getPlants().getAll().size());
        messagePlayer(player, "AgriMutations Hash: {0}", AgriCore.getMutations().hashCode());
        messagePlayer(player, " - Mutation Count: {0}", AgriCore.getMutations().getAll().size());
        messagePlayer(player, "AgriSoils Hash: {0}", AgriCore.getSoils().hashCode());
        messagePlayer(player, " - Soil Count: {0}", AgriCore.getSoils().getAll().size());
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
