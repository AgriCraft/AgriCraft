package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.api.v1.content.items.IAgriCropStickItem;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.infinitylib.item.IInfinityItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemCropSticks extends BlockItem implements IInfinityItem, IAgriCropStickItem {
    private final CropStickVariant variant;

    public ItemCropSticks(CropStickVariant variant) {
        super(AgriBlockRegistry.getInstance().getCropBlock(), new Properties().tab(AgriTabs.TAB_AGRICRAFT));
        this.variant = variant;
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return this.getVariant().getId();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public CropStickVariant getVariant() {
        return this.variant;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        // Fetch target world, pos, tile, and state
        Level world = context.getLevel();
        if(world.isClientSide()) {
            return InteractionResult.PASS;
        }
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockState state = world.getBlockState(pos);

        // Check clicked position
        if(state.getBlock() instanceof BlockCrop) {
            // if the clicked block is already a crop, apply crop sticks
            return this.applyToExisting(world, pos, state, context.getPlayer(), context.getHand());
        }
        // Check position above
        pos = pos.relative(context.getClickedFace());
        state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockCrop) {
            // if the above block is already a crop, apply crop sticks
            return this.applyToExisting(world, pos, state, context.getPlayer(), context.getHand());
        }
        // Delegate to default logic for placement on soil
        return super.useOn(context);
    }

    protected InteractionResult applyToExisting(Level world, BlockPos pos, BlockState state, @Nullable Player player, InteractionHand hand) {
        InteractionResult result = ((BlockCrop) state.getBlock()).applyCropSticks(world, pos, state, this.getVariant());
        if(result == InteractionResult.SUCCESS) {
            this.consumeItem(player, hand);
        }
        return result;
    }

    protected void consumeItem(@Nullable Player player, InteractionHand hand) {
        if(player != null) {
            ItemStack stack = player.getItemInHand(hand);
            if(!player.isCreative()) {
                stack.shrink(1);
            }
        }
    }

    // Override to prevent forwarding to the block
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    // Override to prevent forwarding to the block
    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            items.add(new ItemStack(this));
        }
    }
}
