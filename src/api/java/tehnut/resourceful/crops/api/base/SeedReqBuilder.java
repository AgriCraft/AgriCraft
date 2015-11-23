package tehnut.resourceful.crops.api.base;

import net.minecraft.world.EnumDifficulty;
import tehnut.resourceful.crops.api.util.BlockStack;

/**
 * Factory for creating SeedReqs.
 * Documentation for each field can be found in {@link SeedReq}
 */
public class SeedReqBuilder {

    private BlockStack growthReq = null;
    private EnumDifficulty difficulty = EnumDifficulty.PEACEFUL;
    private int lightLevelMin = 9;
    private int lightLevelMax = Integer.MAX_VALUE;

    public SeedReqBuilder() {

    }

    public SeedReqBuilder setGrowthReq(BlockStack growthReq) {
        this.growthReq = growthReq;
        return this;
    }

    public SeedReqBuilder setDifficulty(EnumDifficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public SeedReqBuilder setLightLevelMin(int lightLevelMin) {
        this.lightLevelMin = lightLevelMin;
        return this;
    }

    public SeedReqBuilder setLightLevelMax(int lightLevelMax) {
        this.lightLevelMax = lightLevelMax;
        return this;
    }

    public SeedReq build() {
        return new SeedReq(growthReq, difficulty, lightLevelMin, lightLevelMax);
    }
}
