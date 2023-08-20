package com.agricraft.agricraft.api.genetic;

public interface AgriAllele<T> {

	T trait();

	boolean isDominant(AgriAllele<T> other);

	AgriGene<T> gene();

}
