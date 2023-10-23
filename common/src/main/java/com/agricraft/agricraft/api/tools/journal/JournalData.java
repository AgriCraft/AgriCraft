package com.agricraft.agricraft.api.tools.journal;

import com.agricraft.agricraft.api.codecs.AgriPlant;

import java.util.List;

public interface JournalData {

	JournalPage getPage(int index);

	int size();

	List<AgriPlant> getDiscoveredSeeds();

}
