package com.infinityraider.agricraft.api.v1.plant;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IAgriWeedSpawnable {

    boolean hasWeeds();

    Optional<IAgriWeed> getWeeds();

    Optional<IAgriGrowthStage> getWeedGrowthStage();

    boolean setWeed(@Nullable IAgriWeed weed, @Nullable IAgriGrowthStage stage);
}
