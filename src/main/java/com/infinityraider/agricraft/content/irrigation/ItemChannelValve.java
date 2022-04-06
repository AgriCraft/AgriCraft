package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemChannelValve extends ItemBase {
    public ItemChannelValve() {
        super(Names.Items.VALVE, new Item.Properties()
                .tab(AgriTabs.TAB_AGRICRAFT));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(context.getClickedPos());
        if(state.getBlock() instanceof BlockIrrigationChannelAbstract) {
            if(!BlockIrrigationChannelAbstract.VALVE.fetch(state).hasValve()) {
                world.setBlock(pos, BlockIrrigationChannelAbstract.VALVE.apply(state, BlockIrrigationChannelAbstract.Valve.OPEN), 3);
                if(world.isClientSide()) {
                    BlockEntity tile = world.getBlockEntity(pos);
                    if(tile instanceof TileEntityIrrigationChannel) {
                        ((TileEntityIrrigationChannel) tile).setValveState(TileEntityIrrigationChannel.ValveState.OPEN);
                    }
                }
                if(context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    context.getPlayer().getItemInHand(context.getHand()).shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(AgriToolTips.VALVE_L1);
        tooltip.add(AgriToolTips.VALVE_L2);
        tooltip.add(AgriToolTips.VALVE_L3);
    }
}
