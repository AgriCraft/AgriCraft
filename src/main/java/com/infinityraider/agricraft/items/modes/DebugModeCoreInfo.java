/*
 */
package com.infinityraider.agricraft.items.modes;

import com.agricraft.agricore.core.AgriCore;
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
public class DebugModeCoreInfo extends DebugMode {

    @Override
    public String debugName() {
        return "core info";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        MessageUtil.messagePlayer(player, "{0} Info:", FMLCommonHandler.instance().getSide());
        MessageUtil.messagePlayer(player, "========================================");
        MessageUtil.messagePlayer(player, "AgriPlants Hash: {0}", AgriCore.getPlants().hashCode());
        MessageUtil.messagePlayer(player, " - Plant Count: {0}", AgriCore.getPlants().getAll().size());
        MessageUtil.messagePlayer(player, "AgriMutations Hash: {0}", AgriCore.getMutations().hashCode());
        MessageUtil.messagePlayer(player, " - Mutation Count: {0}", AgriCore.getMutations().getAll().size());
        MessageUtil.messagePlayer(player, "AgriSoils Hash: {0}", AgriCore.getSoils().hashCode());
        MessageUtil.messagePlayer(player, " - Soil Count: {0}", AgriCore.getSoils().getAll().size());
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
