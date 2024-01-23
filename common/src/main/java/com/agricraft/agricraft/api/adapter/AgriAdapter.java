package com.agricraft.agricraft.api.adapter;

import java.util.Optional;

public interface AgriAdapter<T> {

	boolean accepts(Object obj);

	Optional<T> valueOf(Object obj);

}
