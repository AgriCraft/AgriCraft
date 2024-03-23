package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPage;
import com.agricraft.agricraft.client.ClientUtil;
import com.agricraft.agricraft.common.block.entity.SeedAnalyzerBlockEntity;
import com.agricraft.agricraft.common.item.journal.EmptyPage;
import com.agricraft.agricraft.common.item.journal.FrontPage;
import com.agricraft.agricraft.common.item.journal.GeneticsPage;
import com.agricraft.agricraft.common.item.journal.GrowthReqsPage;
import com.agricraft.agricraft.common.item.journal.IntroductionPage;
import com.agricraft.agricraft.common.item.journal.MutationsPage;
import com.agricraft.agricraft.common.item.journal.PlantPage;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JournalItem extends Item {

	public JournalItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (player.isDiscrete()) {
			return InteractionResultHolder.pass(stack);
		}
		if (level.isClientSide) {
			ClientUtil.openJournalScreen(player, usedHand);
			return InteractionResultHolder.consume(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (level.isClientSide) {
			return InteractionResult.PASS;
		}
		ItemStack heldItem = context.getItemInHand();
		// if a seed analyzer was clicked, insert the journal inside
		if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof SeedAnalyzerBlockEntity seedAnalyzer) {
			if (seedAnalyzer.hasJournal()) {
				return InteractionResult.PASS;
			}
			ItemStack remaining = seedAnalyzer.insertJournal(heldItem);
			heldItem.setCount(remaining.getCount());
			return InteractionResult.CONSUME;
		}
		return super.useOn(context);
	}

	@Override
	public ItemStack getDefaultInstance() {
		ItemStack stack = new ItemStack(this);
//		researchPlant(stack, new ResourceLocation("minecraft:wheat"));
		return stack;
	}

	public static void researchPlant(ItemStack journal, ResourceLocation plantId) {
		CompoundTag tag = journal.getOrCreateTag();
		StringTag idTag = StringTag.valueOf(plantId.toString());
		if (tag.contains("plants")) {
			ListTag plants = tag.getList("plants", Tag.TAG_STRING);
			if (!plants.contains(idTag)) {
				plants.add(idTag);
			}
		} else {
			ListTag plants = new ListTag();
			plants.add(idTag);
			tag.put("plants", plants);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		tooltipComponents.add(Component.translatable("agricraft.tooltip.journal", getResearchedPlants(stack)).withStyle(ChatFormatting.GRAY));
	}

	public static JournalData getJournalData(ItemStack journal) {
		return new Data(journal);
	}

	public static int getResearchedPlants(ItemStack journal) {
		CompoundTag tag = journal.getTag();
		if (tag == null || !tag.contains("plants")) {
			return 0;
		}
		return tag.getList("plants", Tag.TAG_STRING).size();
	}

	public static class Data implements JournalData {

		private final List<ResourceLocation> plants;
		private final List<JournalPage> pages;

		public Data(ItemStack journalStack) {
			this.plants = new ArrayList<>();
			this.pages = new ArrayList<>();
			CompoundTag tag = journalStack.getTag();
			if (tag != null && tag.contains("plants")) {
				ListTag list = tag.getList("plants", Tag.TAG_STRING);
				for (Tag plantTag : list) {
					ResourceLocation plantId = new ResourceLocation(plantTag.getAsString());
					if (AgriApi.getPlant(plantId).isPresent()) {
						plants.add(plantId);
					}
				}
			}
			this.plants.sort(Comparator.comparing(ResourceLocation::toString));
			this.initializePages();
		}

		public void initializePages() {
			this.pages.clear();
			this.pages.add(new FrontPage());
			this.pages.add(new IntroductionPage());
			this.pages.add(new GeneticsPage());
			this.pages.add(new GrowthReqsPage());
			for (ResourceLocation plant : this.plants) {
				PlantPage plantPage = new PlantPage(plant, plants);
				this.pages.add(plantPage);
				List<List<ResourceLocation>> mutations = plantPage.getMutationsOffPage();
				int size = mutations.size();
				if (size > 0) {
					int remaining = size;
					int from = 0;
					int to = Math.min(remaining, MutationsPage.LIMIT);
					while (remaining > 0) {
						pages.add(new MutationsPage(mutations.subList(from, to)));
						remaining -= (to - from);
						from = to;
						to = from + Math.min(remaining, MutationsPage.LIMIT);
					}
				}
			}
		}

		@Override
		public JournalPage getPage(int index) {
			if (0 <= index && index < this.pages.size()) {
				return this.pages.get(index);
			}
			return new EmptyPage();
		}

		@Override
		public int size() {
			return this.pages.size();
		}

		@Override
		public List<ResourceLocation> getDiscoveredSeeds() {
			return this.plants;
		}

	}

}
