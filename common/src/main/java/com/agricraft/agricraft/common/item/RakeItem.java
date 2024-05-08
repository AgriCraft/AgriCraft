package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.common.block.CropBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RakeItem extends Item {

	public static final RakeLogic WOOD_LOGIC = (crop, rakeItem, player) -> {
		AgriGrowthStage current = crop.getWeedGrowthStage();
		AgriGrowthStage previous = current.getPrevious(crop, crop.getLevel().getRandom());
		if (current.equals(previous)) {
			crop.removeWeeds();
		} else {
			crop.setWeedGrowthStage(previous);
		}
	};

	public static final RakeLogic IRON_LOGIC = (crop, rakeItem, player) -> crop.removeWeeds();

	private final RakeLogic rakeLogic;

	public RakeItem(Properties properties, RakeLogic rakeLogic) {
		super(properties);
		this.rakeLogic = rakeLogic;
	}

	@NotNull
	@Override
	public InteractionResult useOn(@NotNull UseOnContext context) {
		return AgriApi.getCrop(context.getLevel(), context.getClickedPos()).map(crop -> {
			if (crop.getLevel() == null) {
				return InteractionResult.PASS;
			}
			if (crop.hasWeeds()) {
				AgriWeed weed = crop.getWeed();
				AgriGrowthStage stage = crop.getWeedGrowthStage();
				this.rakeLogic.apply(crop, context.getItemInHand(), context.getPlayer());
				ArrayList<ItemStack> drops = new ArrayList<>();
				weed.onRake(stage, drops::add, crop.getLevel().getRandom(), context.getPlayer());
				drops.forEach(stack -> CropBlock.spawnItem(crop.getLevel(), crop.getBlockPos(), stack));
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.FAIL;
		}).orElse(InteractionResult.FAIL);
	}

	@FunctionalInterface
	public interface RakeLogic {

		void apply(AgriCrop crop, ItemStack rakeItem, @Nullable Player player);

	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		tooltipComponents.add(Component.translatable("agricraft.tooltip.rake").withStyle(ChatFormatting.DARK_GRAY));
	}

}
