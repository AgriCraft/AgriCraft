package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.config.AgriCraftConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.tools.seedbag.BagEntry;
import com.agricraft.agricraft.api.tools.seedbag.BagSorter;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.CropState;
import com.agricraft.agricraft.common.registry.AgriBlocks;
import com.agricraft.agricraft.common.registry.AgriDataComponents;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SeedBagItem extends Item {

	public static final List<BagSorter> SORTERS = new ArrayList<>();
	public static final BagSorter DEFAULT_SORTER = new BagSorter() {
		@Override
		public ResourceLocation getId() {
			return ResourceLocation.fromNamespaceAndPath("agricraft", "default");
		}

		@Override
		public int compare(BagEntry entry1, BagEntry entry2) {
			int s1 = AgriApi.get().getStatRegistry().stream().mapToInt(stat -> entry1.genome().getStatChromosome(stat).trait()).sum();
			int s2 = AgriApi.get().getStatRegistry().stream().mapToInt(stat -> entry2.genome().getStatChromosome(stat).trait()).sum();
			if (s1 != s2) {
				return s2 - s1;
			}
			return AgriApi.get().getStatRegistry().stream().mapToInt(stat -> {
				Integer d1 = entry1.genome().getStatChromosome(stat).dominant();
				Integer r1 = entry1.genome().getStatChromosome(stat).recessive();
				Integer d2 = entry2.genome().getStatChromosome(stat).dominant();
				Integer r2 = entry2.genome().getStatChromosome(stat).recessive();
				return (d2 + r2) - (d1 + r1);
			}).sum();
		}
	};

	public SeedBagItem(Properties properties) {
		super(properties);
	}

	private static boolean plantOnCrop(AgriCrop crop, ItemStack seed) {
		if (crop.hasPlant() || crop.isCrossCropSticks()) {
			return false;
		}
		AgriGenome genome = seed.get(AgriDataComponents.GENOME);
		if (genome == null) {
			return false;
		}
		crop.plantGenome(genome);
		return true;
	}

	public static int add(ItemStack seedBag, ItemStack insertedStack) {
		AgriGenome genome = insertedStack.get(AgriDataComponents.GENOME);
		Data data = seedBag.get(AgriDataComponents.SEED_BAG_DATA);
		if (insertedStack.isEmpty() || genome == null || data == null) {
			return 0;
		}
		if (!data.plants().isEmpty() && !data.plants().getFirst().genome().species().trait().equals(genome.species().trait())) {
			// bag already has seeds, we can add seeds only if they have the same species
			return 0;
		}
		// at this point, either there are no seeds in the bag, or the seed to be inserted has the same species as the ones inside
		int size = size(seedBag);
		if (size >= AgriCraftConfig.SEED_BAG_CAPACITY.get()) {
			return 0;
		}
		int insertedCount = Math.min(AgriCraftConfig.SEED_BAG_CAPACITY.get() - size, insertedStack.getCount());
		ImmutableList.Builder<BagEntry> builder = ImmutableList.builder();
		Stream.concat(data.plants().stream(), Stream.of(new BagEntry(insertedCount, genome))).sorted(SORTERS.get(data.sorter())).forEach(builder::add);
		Data newData = new Data(builder.build(), data.sorter());

		seedBag.set(AgriDataComponents.SEED_BAG_DATA, newData);
		return insertedCount;
	}

	public static ItemStack extractFirstStack(ItemStack seedBag) {
		Data data = seedBag.get(AgriDataComponents.SEED_BAG_DATA);
		if (data == null || data.plants().isEmpty()) {
			return ItemStack.EMPTY;
		}
		BagEntry entry = data.plants().getFirst();
		ItemStack seed = AgriSeedItem.toStack(entry.genome());
		seed.setCount(entry.count());
		seedBag.set(AgriDataComponents.SEED_BAG_DATA, new Data(data.plants().stream().skip(1).collect(ImmutableList.toImmutableList()), data.sorter()));
		return seed;
	}

	public static ItemStack extractFirstItem(ItemStack seedBag, boolean simulate) {
		Data data = seedBag.get(AgriDataComponents.SEED_BAG_DATA);
		if (data == null || data.plants().isEmpty()) {
			return ItemStack.EMPTY;
		}
		BagEntry entry = data.plants().getFirst();
		ItemStack seed = AgriSeedItem.toStack(entry.genome());
		if (!simulate) {
			ImmutableList.Builder<BagEntry> builder = ImmutableList.builder();
			if (entry.count() <= 1) {
				data.plants().stream().skip(1).forEach(builder::add);
			} else {
				BagEntry newEntry = new BagEntry(entry.count() - 1, entry.genome());
				builder.add(newEntry);
			}
			data.plants().stream().skip(1).forEach(builder::add);
			seedBag.set(AgriDataComponents.SEED_BAG_DATA, new Data(builder.build(), data.sorter()));
		}
		return seed;
	}

	public static void changeSorter(ItemStack seedBag, int delta) {
		if (delta == 0) {
			return;
		}
		Data data = seedBag.get(AgriDataComponents.SEED_BAG_DATA);
		int sorterIndex = data.sorter + delta;
		if (sorterIndex < 0) {
			sorterIndex += SORTERS.size();
		}
		sorterIndex %= SORTERS.size();
		seedBag.set(AgriDataComponents.SEED_BAG_DATA, data.sortedBy(sorterIndex));
	}

	public static int size(ItemStack seedBag) {
		Data data = seedBag.get(AgriDataComponents.SEED_BAG_DATA);
		return data == null ? 0 : data.size();
	}

	public static boolean isEmpty(ItemStack stack) {
		Data data = stack.get(AgriDataComponents.SEED_BAG_DATA);
		return data == null || data.plants().isEmpty();
	}

	public static boolean isFilled(ItemStack stack) {
		Data data = stack.get(AgriDataComponents.SEED_BAG_DATA);
		return data != null && data.size() == AgriCraftConfig.SEED_BAG_CAPACITY.get();
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
		Optional<AgriCrop> optional = AgriApi.get().getCrop(level, pos);
		// if we clicked on a crop stick
		if (optional.isPresent()) {
			if (plantOnCrop(optional.get(), seed)) {
				extractFirstItem(seedBag, false);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}
		// if we clicked on a soil
		optional = AgriApi.get().getCrop(level, pos.above());
		if (optional.isPresent()) {
			// if there's a crop above
			if (plantOnCrop(optional.get(), seed)) {
				extractFirstItem(seedBag, false);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		} else if (AgriCraftConfig.PLANT_OFF_CROP_STICKS.get()) {
			// if there is nothing above
			if (AgriApi.get().getSoil(level, pos).isPresent() && level.getBlockState(pos.above()).isAir()) {
				level.setBlock(pos.above(), AgriBlocks.CROP.get().defaultBlockState().setValue(CropBlock.CROP_STATE, CropState.PLANT), Block.UPDATE_ALL_IMMEDIATE);
				optional = AgriApi.get().getCrop(level, pos.above());
				if (optional.isPresent()) {
					AgriGenome genome = seed.get(AgriDataComponents.GENOME);
					if (genome != null) {
						optional.get().plantGenome(genome, context.getPlayer());
						extractFirstItem(seedBag, false);
						return InteractionResult.SUCCESS;
					}
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
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		Data data = stack.get(AgriDataComponents.SEED_BAG_DATA);
		if (isEmpty(stack) || data == null) {
			tooltipComponents.add(Component.translatable("agricraft.tooltip.bag.empty").withStyle(ChatFormatting.DARK_GRAY));
		} else {
			tooltipComponents.add(Component.translatable("agricraft.tooltip.bag.content", size(stack)).append(LangUtils.seedName(data.plants().getFirst().genome().species().trait())).withStyle(ChatFormatting.DARK_GRAY));
		}
		String id = SORTERS.get(data.sorter).getId().toString().replace(":", ".");
		tooltipComponents.add(Component.translatable("agricraft.tooltip.bag.sorter")
				.append(Component.translatable("agricraft.tooltip.bag.sorter." + id))
				.withStyle(ChatFormatting.DARK_GRAY));
	}

	public record Data(ImmutableList<BagEntry> plants, int sorter) {

		public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				BagEntry.CODEC.listOf().fieldOf("plants").forGetter(data -> data.plants),
				Codec.INT.fieldOf("sorter").forGetter(data -> data.sorter)
		).apply(instance, Data::new));
		public static StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				BagEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), data -> data.plants,
				ByteBufCodecs.INT, data -> data.sorter,
				Data::new
		);

		public Data() {
			this(ImmutableList.of(), 0);
		}

		private Data(List<BagEntry> entries, int sorterId) {
			this(ImmutableList.copyOf(entries), sorterId);
		}

		public Data sorted() {
			return new Data(ImmutableList.sortedCopyOf(SORTERS.get(this.sorter), this.plants), this.sorter);
		}

		public Data sortedBy(int sorter) {
			return new Data(ImmutableList.sortedCopyOf(SORTERS.get(sorter), this.plants), sorter);
		}

		public int size() {
			return plants.stream().mapToInt(BagEntry::count).sum();
		}

	}

	public static class StatSorter implements BagSorter {

		private final Supplier<AgriStat> stat;
		private final ResourceLocation id;

		public StatSorter(Supplier<AgriStat> stat, ResourceLocation id) {
			this.stat = stat;
			this.id = id;
		}

		@Override
		public ResourceLocation getId() {
			return this.id;
		}

		@Override
		public int compare(BagEntry entry1, BagEntry entry2) {
			int s1 = entry1.genome().getStatChromosome(this.stat.get()).trait();
			int s2 = entry2.genome().getStatChromosome(this.stat.get()).trait();
			if (s1 == s2) {
				return DEFAULT_SORTER.compare(entry1, entry2);
			}
			return s2 - s1;
		}

	}

}
