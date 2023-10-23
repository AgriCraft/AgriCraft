package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPage;
import com.agricraft.agricraft.client.ClientUtil;
import com.agricraft.agricraft.common.item.journal.EmptyPage;
import com.agricraft.agricraft.common.item.journal.FrontPage;
import com.agricraft.agricraft.common.item.journal.GeneticsPage;
import com.agricraft.agricraft.common.item.journal.GrowthReqsPage;
import com.agricraft.agricraft.common.item.journal.IntroductionPage;
import com.agricraft.agricraft.common.item.journal.MutationsPage;
import com.agricraft.agricraft.common.item.journal.PlantPage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
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

	public static class Data implements JournalData {

		private final List<AgriPlant> plants;
		private final List<JournalPage> pages;

		public Data(ItemStack journalStack) {
			this.plants = new ArrayList<>();
			this.pages = new ArrayList<>();
			// TODO: @Ketheroth use the researched plants from the journal
			AgriApi.getPlantRegistry().ifPresent(reg -> reg.stream().forEach(this.plants::add));
			this.initializePages();
		}

		public void initializePages() {
			this.pages.clear();
			this.pages.add(new FrontPage());
			this.pages.add(new IntroductionPage());
			this.pages.add(new GeneticsPage());
			this.pages.add(new GrowthReqsPage());
			for (AgriPlant plant : this.plants) {
				PlantPage plantPage = new PlantPage(plant, this.plants.stream().map(plant1 -> AgriApi.getPlantId(plant1).orElse(AgriPlant.UNKNOWN)).toList());
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
		public List<AgriPlant> getDiscoveredSeeds() {
			return this.plants;
		}

	}

}
