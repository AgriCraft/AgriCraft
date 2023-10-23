package com.agricraft.agricraft.api.codecs;

import java.util.List;

public interface AgriSoilValue {

	String name();

	int ordinal();

	boolean isValid();

	List<String> synonyms();

	default boolean isSynonym(String string) {
		return this.synonyms().stream().anyMatch(string::equalsIgnoreCase);
	}

}
