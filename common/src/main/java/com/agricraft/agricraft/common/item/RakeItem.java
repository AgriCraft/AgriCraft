package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.IAgriCrop;
import com.agricraft.agricraft.api.crop.IAgriGrowthStage;
import com.agricraft.agricraft.api.plant.IAgriWeed;
import com.google.common.collect.Lists;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RakeItem extends Item {

	public static RakeLogic WOOD_LOGIC = new RakeLogic("rake_wood") {
		@Override
		public boolean doRakeAction(IAgriCrop crop, ItemStack stack, @Nullable Player player) {
			if (crop.world() == null || crop.world().isClientSide()) {
				return false;
			}
			if (!crop.hasWeeds()) {
				return false;
			}
			IAgriWeed weeds = crop.getWeeds();
			IAgriGrowthStage current = crop.getWeedGrowthStage();
			IAgriGrowthStage previous = current.getPreviousStage(crop, crop.world().getRandom());
			if (current.equals(previous)) {
				return crop.removeWeed();
			} else {
				return crop.setWeed(weeds, previous);
			}
		}
	};

	public static RakeLogic IRON_LOGIC = new RakeLogic("rake_iron") {
		@Override
		public boolean doRakeAction(IAgriCrop crop, ItemStack stack, @Nullable Player player) {
			if (crop.world() == null || crop.world().isClientSide()) {
				return false;
			}
			if (!crop.hasWeeds()) {
				return false;
			}
			return crop.removeWeed();
		}
	};

	private final RakeLogic logic;

	public RakeItem(Properties properties, RakeLogic rakeLogic) {
		super(properties);
		this.logic = rakeLogic;
	}

	protected RakeLogic getRakeLogic() {
		return this.logic;
	}

	@NotNull
	@Override
	public InteractionResult useOn(@NotNull UseOnContext context) {
		return AgriApi.getCrop(context.getLevel(), context.getClickedPos())
				.map(crop -> this.getRakeLogic().applyLogic(crop, context.getItemInHand(), context.getPlayer()))
				.orElse(InteractionResult.FAIL);
	}

	public static abstract class RakeLogic {

		private final String name;

		public RakeLogic(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public final InteractionResult applyLogic(IAgriCrop crop, ItemStack stack, @Nullable Player player) {
			if (crop.world() == null || crop.world().isClientSide()) {
				return InteractionResult.PASS;
			}
// FIXME: update events
//			if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Rake.Pre(crop, stack, player))) {
			IAgriWeed weeds = crop.getWeeds();
			IAgriGrowthStage stage = crop.getWeedGrowthStage();
			if (this.doRakeAction(crop, stack, player)) {
				List<ItemStack> drops = Lists.newArrayList();
				weeds.onRake(stage, drops::add, crop.world().getRandom(), player);
//					AgriCropEvent.Rake.Post event = new AgriCropEvent.Rake.Post(crop, stack, drops, player);
				drops.forEach(crop::dropItem);
//					MinecraftForge.EVENT_BUS.post(event);
//					event.getDrops().forEach(crop::dropItem);
				return InteractionResult.SUCCESS;
			}
//			}
			return InteractionResult.FAIL;
		}

		protected abstract boolean doRakeAction(IAgriCrop crop, ItemStack stack, @Nullable Player player);

	}

}
