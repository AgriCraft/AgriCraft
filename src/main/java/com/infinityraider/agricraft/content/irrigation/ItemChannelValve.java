package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemChannelValve extends ItemBase {
    public ItemChannelValve() {
        super(Names.Items.VALVE, new Item.Properties()
                .group(AgriTabs.TAB_AGRICRAFT));
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(context.getPos());
        if(state.getBlock() instanceof BlockIrrigationChannelAbstract) {
            if(!BlockIrrigationChannelAbstract.VALVE.fetch(state).hasValve()) {
                world.setBlockState(pos, BlockIrrigationChannelAbstract.VALVE.apply(state, BlockIrrigationChannelAbstract.Valve.OPEN));
                if(world.isRemote()) {
                    TileEntity tile = world.getTileEntity(pos);
                    if(tile instanceof TileEntityIrrigationChannel) {
                        ((TileEntityIrrigationChannel) tile).setValveState(TileEntityIrrigationChannel.ValveState.OPEN);
                    }
                }
                if(context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    context.getPlayer().getHeldItem(context.getHand()).shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onItemUse(context);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(AgriToolTips.VALVE_L1);
        tooltip.add(AgriToolTips.VALVE_L2);
        tooltip.add(AgriToolTips.VALVE_L3);
    }
}
