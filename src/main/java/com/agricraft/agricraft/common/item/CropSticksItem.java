package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.CropStickVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CropSticksItem extends BlockItem {

	private final CropStickVariant variant;

	public CropSticksItem(Block block, CropStickVariant variant) {
		super(block, (variant == CropStickVariant.IRON || variant == CropStickVariant.OBSIDIAN) ? new Item.Properties().fireResistant() : new Item.Properties());
		this.variant = variant;
	}

	public CropStickVariant getVariant() {
		return this.variant;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		if(world.isClientSide()) {
			return InteractionResult.PASS;
		}
		BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof CropBlock) {
			// if the above block is already a crop, apply crop sticks
			return this.applyToExisting(world, pos, state, context.getPlayer(), context.getHand());
		}
		// Delegate to default logic for placement on soil
		return super.useOn(context);
	}

	protected InteractionResult applyToExisting(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand) {
		ItemInteractionResult result = CropBlock.applyCropSticks(world, pos, state, this.getVariant());
		if(result == ItemInteractionResult.SUCCESS) {
			if(player != null) {
				ItemStack stack = player.getItemInHand(hand);
				if(!player.isCreative()) {
					stack.shrink(1);
				}
			}
		}
		return result.result();
	}

	@Override
	public String getDescriptionId() {
		return "item.agricraft." + variant.getSerializedName() + "_crop_sticks";
	}

	@Override
	public boolean canBeHurtBy(ItemStack stack, DamageSource source) {
		return !stack.has(DataComponents.FIRE_RESISTANT) || !source.is(DamageTypeTags.IS_FIRE);
	}

}
