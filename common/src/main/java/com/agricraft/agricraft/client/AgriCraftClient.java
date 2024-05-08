package com.agricraft.agricraft.client;

import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.client.tools.journal.drawers.FrontPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.GeneticsPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.GrowthReqsPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.IntroductionPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.MutationPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.PlantPageDrawer;
import com.agricraft.agricraft.common.item.SeedBagItem;
import com.agricraft.agricraft.common.item.TrowelItem;
import com.agricraft.agricraft.common.item.journal.FrontPage;
import com.agricraft.agricraft.common.item.journal.GeneticsPage;
import com.agricraft.agricraft.common.item.journal.GrowthReqsPage;
import com.agricraft.agricraft.common.item.journal.IntroductionPage;
import com.agricraft.agricraft.common.item.journal.MutationsPage;
import com.agricraft.agricraft.common.item.journal.PlantPage;
import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class AgriCraftClient {

	public static void init() {
		AgriClientApi.registerPageDrawer(FrontPage.ID, new FrontPageDrawer());
		AgriClientApi.registerPageDrawer(IntroductionPage.ID, new IntroductionPageDrawer());
		AgriClientApi.registerPageDrawer(GrowthReqsPage.ID, new GrowthReqsPageDrawer());
		AgriClientApi.registerPageDrawer(GeneticsPage.ID, new GeneticsPageDrawer());
		AgriClientApi.registerPageDrawer(PlantPage.ID, new PlantPageDrawer());
		AgriClientApi.registerPageDrawer(MutationsPage.ID, new MutationPageDrawer());
		ItemProperties.register(ModItems.TROWEL.get(), new ResourceLocation("agricraft:plant"), (itemStack, clientLevel, livingEntity, i) -> {
			if (!itemStack.isEmpty() && itemStack.getItem() instanceof TrowelItem trowel && trowel.hasPlant(itemStack)) {
				return 1;
			} else {
				return 0;
			}
		});
		ItemProperties.register(ModItems.SEED_BAG.get(), new ResourceLocation("agricraft:seed_bag"), (itemStack, clientLevel, livingEntity, i) -> {
			if (!itemStack.isEmpty() && itemStack.getItem() instanceof SeedBagItem) {
				if (SeedBagItem.isFilled(itemStack)) {
					return 1;
				} else if (SeedBagItem.isEmpty(itemStack)) {
					return 0.5F;
				}
				return 0;
			} else {
				return 0.5F;
			}
		});
	}

}
