package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.genetic.AgriGenomeProviderItem;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.CropState;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.util.LangUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SeedBagItem extends Item {

	public static final List<BagSorter> SORTERS = new ArrayList<>();
	public static final BagSorter DEFAULT_SORTER = new BagSorter() {
		@Override
		public ResourceLocation getId() {
			return new ResourceLocation("agricraft", "default");
		}

		@Override
		public int compare(BagEntry entry1, BagEntry entry2) {
			int s1 = AgriStatRegistry.getInstance().stream().mapToInt(stat -> entry1.genome.getStatGene(stat).getTrait()).sum();
			int s2 = AgriStatRegistry.getInstance().stream().mapToInt(stat -> entry2.genome.getStatGene(stat).getTrait()).sum();
			if (s1 != s2) {
				return s2 - s1;
			}
			return AgriStatRegistry.getInstance().stream().mapToInt(stat -> {
				Integer d1 = entry1.genome.getStatGene(stat).getDominant().trait();
				Integer r1 = entry1.genome.getStatGene(stat).getRecessive().trait();
				Integer d2 = entry2.genome.getStatGene(stat).getDominant().trait();
				Integer r2 = entry2.genome.getStatGene(stat).getRecessive().trait();
				return (d2 + r2) - (d1 + r1);
			}).sum();
		}
	};

	static {
		SORTERS.add(DEFAULT_SORTER);
	}

	public SeedBagItem(Properties properties) {
		super(properties);
	}

	public static void addStatSorter(AgriStat stat) {
		SORTERS.add(new StatSorter(stat));
	}

	private static boolean plantOnCrop(AgriCrop crop, ItemStack seed) {
		if (crop.hasPlant() || crop.isCrossCropSticks()) {
			return false;
		}
		crop.plantGenome(AgriGenome.fromNBT(seed.getTag()));
		return true;
	}

	public static int add(ItemStack seedBag, ItemStack insertedStack) {
		if (insertedStack.isEmpty() || !(insertedStack.getItem() instanceof AgriGenomeProviderItem seed)) {
			return 0;
		}
		CompoundTag tag = seedBag.getOrCreateTag();
		Optional<AgriGenome> opt = seed.getGenome(insertedStack);
		if (opt.isEmpty()) {
			return 0;
		}
		AgriGenome genome = opt.get();
		if (tag.contains("species")) {
			// bag already has seeds, we can add seeds only if they have the same species
			if (!genome.getSpeciesGene().getTrait().equals(tag.getString("species"))) {
				return 0;
			}
		}
		// at this point, either there are no seeds in the bag, or the seed to be inserted has the same species as the ones inside
		if (!tag.contains("seeds")) {
			tag.put("seeds", new ListTag());
			tag.putString("species", genome.getSpeciesGene().getTrait());
		}
		ListTag seeds = tag.getList("seeds", Tag.TAG_COMPOUND);
		int size = size(seedBag);
		if (size >= CoreConfig.seedBagCapacity) {
			return 0;
		}
		// we should try to merge the genome to another one by increasing its count if they are the same
		CompoundTag seedTag = new CompoundTag();
		int insertedCount = Math.min(CoreConfig.seedBagCapacity - size, insertedStack.getCount());
		seedTag.putInt("count", insertedCount);
		genome.writeToNBT(seedTag);
		seeds.add(seedTag);
		sort(seedBag);
		return insertedCount;
	}

	public static ItemStack extractFirstStack(ItemStack seedBag) {
		ListTag seeds = seedBag.getTag().getList("seeds", Tag.TAG_COMPOUND);
		BagEntry entry = BagEntry.fromNBT(seeds.getCompound(0));
		ItemStack seed = AgriSeedItem.toStack(entry.genome);
		seed.setCount(entry.count);
		seeds.remove(0);
		if (seeds.isEmpty()) {
			seedBag.getTag().remove("seeds");
			seedBag.getTag().remove("species");
		}
		return seed;
	}

	public static ItemStack extractFirstItem(ItemStack seedBag, boolean simulate) {
		ListTag seeds = seedBag.getTag().getList("seeds", Tag.TAG_COMPOUND);
		CompoundTag seedTag = seeds.getCompound(0);
		AgriGenome genome = AgriGenome.fromNBT(seedTag);
		ItemStack seed = AgriSeedItem.toStack(genome);
		if (!simulate) {
			int count = seedTag.getInt("count") - 1;
			seedTag.putInt("count", count);
			if (count <= 0) {
				seeds.remove(0);
			}
			if (seeds.isEmpty()) {
				seedBag.getTag().remove("seeds");
				seedBag.getTag().remove("species");
			}
		}
		return seed;
	}

	public static void changeSorter(ItemStack seedBag, int delta) {
		if (delta == 0) {
			return;
		}
		CompoundTag tag = seedBag.getOrCreateTag();
		int sorterIndex = 0;
		if (tag.contains("sorter")) {
			sorterIndex = tag.getInt("sorter");
		}
		sorterIndex += delta;
		if (sorterIndex < 0) {
			sorterIndex += SORTERS.size();
		}
		sorterIndex %= SORTERS.size();
		tag.putInt("sorter", sorterIndex);
		sort(seedBag);
	}

	private static void sort(ItemStack seedBag) {
		CompoundTag tag = seedBag.getOrCreateTag();
		int sorterIndex = 0;
		if (tag.contains("sorter")) {
			sorterIndex = tag.getInt("sorter");
		}
		ListTag listTag = tag.getList("seeds", Tag.TAG_COMPOUND);
		List<BagEntry> entries = new ArrayList<>();
		for (int i = 0; i < listTag.size(); i++) {
			entries.add(BagEntry.fromNBT(listTag.getCompound(i)));
		}
		BagSorter sorter = SORTERS.get(sorterIndex);
		entries.sort(sorter);
		listTag.clear();
		for (BagEntry entry : entries) {
			CompoundTag t = new CompoundTag();
			entry.writeToNBT(t);
			listTag.add(t);
		}
	}

	public static int size(ItemStack seedBag) {
		CompoundTag tag = seedBag.getTag();
		if (tag == null || !tag.contains("seeds")) {
			return 0;
		}
		ListTag seeds = tag.getList("seeds", Tag.TAG_COMPOUND);
		int count = 0;
		for (int i = 0; i < seeds.size(); i++) {
			count += seeds.getCompound(i).getInt("count");
		}
		return count;
	}

	public static boolean isEmpty(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag == null || !tag.contains("species");
	}

	public static boolean isFilled(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag != null && tag.contains("seeds") && size(stack) == CoreConfig.seedBagCapacity;
	}

	private static void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getHand() == InteractionHand.OFF_HAND) {
			return super.useOn(context);
		}
		ItemStack seedBag = context.getItemInHand();
		if (isEmpty(seedBag)) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		ItemStack seed = extractFirstItem(seedBag, true);
		BlockPos pos = context.getClickedPos();
		Optional<AgriCrop> optional = AgriApi.getCrop(level, pos);
		// if we clicked on a crop stick
		if (optional.isPresent()) {
			if (plantOnCrop(optional.get(), seed)) {
				extractFirstItem(seedBag, false);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}
		// if we clicked on a soil
		optional = AgriApi.getCrop(level, pos.above());
		if (optional.isPresent()) {
			// if there's a crop above
			if (plantOnCrop(optional.get(), seed)) {
				extractFirstItem(seedBag, false);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		} else if (CoreConfig.plantOffCropSticks) {
			// if there is nothing above
			if (AgriApi.getSoil(level, pos).isPresent() && level.getBlockState(pos.above()).isAir()) {
				level.setBlock(pos.above(), ModBlocks.CROP.get().defaultBlockState().setValue(CropBlock.CROP_STATE, CropState.PLANT), Block.UPDATE_ALL_IMMEDIATE);
				optional = AgriApi.getCrop(level, pos.above());
				if (optional.isPresent()) {
					optional.get().plantGenome(AgriGenome.fromNBT(seed.getTag()), context.getPlayer());
					extractFirstItem(seedBag, false);
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.PASS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack seedBag, Slot slot, ClickAction action, Player player) {
		// when you right-click on another stack with the bag
		if (seedBag.getCount() == 1 && action == ClickAction.SECONDARY) {
			ItemStack itemStack = slot.getItem();
			if (itemStack.isEmpty()) {
				if (!isEmpty(seedBag)) {
					playRemoveOneSound(player);
					ItemStack seed = extractFirstStack(seedBag);
					slot.safeInsert(seed);
				}
			} else if (itemStack.getItem().canFitInsideContainerItems()) {
				int inserted = add(seedBag, itemStack);
				slot.safeTake(itemStack.getCount(), inserted, player);
				if (inserted > 0) {
					playInsertSound(player);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack seedBag, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		// when you right-click on the bag with another stack
		if (seedBag.getCount() == 1 && action == ClickAction.SECONDARY && slot.allowModification(player)) {
			if (other.isEmpty()) {
				if (!isEmpty(seedBag)) {
					playRemoveOneSound(player);
					ItemStack seed = extractFirstStack(seedBag);
					access.set(seed);
				}
			} else if (other.getItem().canFitInsideContainerItems()) {
				int inserted = add(seedBag, other);
				if (inserted > 0) {
					playInsertSound(player);
					other.shrink(inserted);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		if (isEmpty(stack)) {
			tooltipComponents.add(Component.translatable("agricraft.tooltip.bag.empty").withStyle(ChatFormatting.DARK_GRAY));
		} else {
			tooltipComponents.add(Component.translatable("agricraft.tooltip.bag.content", size(stack)).append(LangUtils.seedName(stack.getTag().getString("species"))).withStyle(ChatFormatting.DARK_GRAY));
		}
		int i = stack.getOrCreateTag().getInt("sorter");
		String id = SORTERS.get(i).getId().toString().replace(":", ".");
		tooltipComponents.add(Component.translatable("agricraft.tooltip.bag.sorter")
				.append(Component.translatable("agricraft.tooltip.bag.sorter." + id))
				.withStyle(ChatFormatting.DARK_GRAY));
	}

	/**
	 * Sorts its entries by having the best in first, and the worst in last
	 */
	public interface BagSorter extends Comparator<BagEntry> {

		ResourceLocation getId();

	}

	public record BagEntry(int count, AgriGenome genome) {

		public static BagEntry fromNBT(CompoundTag tag) {
			return new BagEntry(tag.getInt("count"), AgriGenome.fromNBT(tag));
		}

		public void writeToNBT(CompoundTag tag) {
			tag.putInt("count", this.count);
			this.genome.writeToNBT(tag);
		}

	}

	public static class StatSorter implements BagSorter {

		private final AgriStat stat;
		private final ResourceLocation id;

		public StatSorter(AgriStat stat) {
			this.stat = stat;
			this.id = new ResourceLocation("agricraft", stat.getId());
		}

		@Override
		public ResourceLocation getId() {
			return this.id;
		}

		@Override
		public int compare(BagEntry entry1, BagEntry entry2) {
			int s1 = entry1.genome.getStatGene(this.stat).getTrait();
			int s2 = entry2.genome.getStatGene(this.stat).getTrait();
			if (s1 == s2) {
				return DEFAULT_SORTER.compare(entry1, entry2);
			}
			return s2 - s1;
		}

	}

}
