package com.agricraft.agricraft.common.item.journal;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.tools.journal.JournalPage;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MutationsPage implements JournalPage {

	public static final ResourceLocation ID = new ResourceLocation(AgriApi.MOD_ID, "mutation_page");
	public static final int LIMIT = 18;

	private final List<List<ResourceLocation>> mutationsLeft;
	private final List<List<ResourceLocation>> mutationsRight;

	public MutationsPage(List<List<ResourceLocation>> mutations) {
		int count = mutations.size();
		if (count <= LIMIT / 2) {
			this.mutationsLeft = mutations;
			this.mutationsRight = List.of();
		} else {
			this.mutationsLeft = mutations.subList(0, LIMIT / 2 - 1);
			this.mutationsRight = mutations.subList(LIMIT / 2, count - 1);
		}
	}

	@Override
	public ResourceLocation getDrawerId() {
		return ID;
	}

	public List<List<ResourceLocation>> getMutationsLeft() {
		return this.mutationsLeft;
	}

	public List<List<ResourceLocation>> getMutationsRight() {
		return this.mutationsRight;
	}

}
