package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AgriSeedItem extends BlockItem {

	public AgriSeedItem(Properties properties) {
		super(ModBlocks.CROP.get(), properties);
	}
	public static ItemStack toStack(ResourceLocation plantId) {
		return toStack(plantId, 1);
	}

	public static ItemStack toStack(ResourceLocation plantId, int amount) {
		ItemStack stack = new ItemStack(ModItems.SEED.get(), amount);
		CompoundTag tag = new CompoundTag();
		tag.putString("plant", plantId.toString());
		stack.setTag(tag);
		return stack;
	}

	@Override
	public Component getName(ItemStack stack) {
		if (stack.getTag()==null) {
			return Component.translatable("seed.agricraft.agricraft.unknown");
		}
		return Component.translatable("seed.agricraft." + stack.getTag().getString("plant").replace(":", "."));
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		InteractionResult result = super.place(context);
		Level level = context.getLevel();
		System.out.println(level.isClientSide + " " + result);
		if (result.consumesAction() && !level.isClientSide) {
			System.out.println(level.getBlockState(context.getClickedPos()));
			BlockEntity be = level.getBlockEntity(context.getClickedPos());
			if (be instanceof CropBlockEntity cbe) {
				CompoundTag tag = context.getItemInHand().getTag();
				if (tag != null) {
					String plant = tag.getString("plant");
					cbe.setPlant(plant, level.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(plant)));
				}
			}
		}
		return result;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		if (isAdvanced.isAdvanced()) {
			CompoundTag tag = stack.getTag();
			if (tag != null) {
				tooltipComponents.add(Component.literal("id: " + tag.getString("plant")));
			}
		}
	}

	@Override
	public Component getDescription() {
		return super.getDescription();
	}

}
