package tehnut.resourceful.crops.api.base;

import net.minecraft.world.EnumDifficulty;
import tehnut.resourceful.crops.api.util.BlockStack;

public class SeedReq {

    private BlockStack growthReq;
    private EnumDifficulty difficulty;
    private int lightLevelMin;
    private int lightLevelMax;

    /**
     * To create a seed requirement, use {@link SeedReqBuilder}
     *
     * @param growthReq     - BlockStack needed under the soil for the crop to grow.
     * @param difficulty    - Minimum difficulty needed for the crop to grow.
     * @param lightLevelMin - Minimum light level required for a crop to grow.
     * @param lightLevelMax - Maximum light level the crop can grow at.
     */
    protected SeedReq(BlockStack growthReq, EnumDifficulty difficulty, int lightLevelMin, int lightLevelMax) {
        this.growthReq = growthReq;
        this.difficulty = difficulty;
        this.lightLevelMin = lightLevelMin;
        this.lightLevelMax = lightLevelMax;
    }

    public BlockStack getGrowthReq() {
        return growthReq;
    }

    public EnumDifficulty getDifficulty() {
        return difficulty;
    }

    public int getLightLevelMin() {
        return lightLevelMin;
    }

    public int getLightLevelMax() {
        return lightLevelMax;
    }

    @Override
    public String toString() {
        return "SeedReq{" +
                "growthReq=" + growthReq +
                "difficulty=" + difficulty +
                ", lightLevelMin=" + lightLevelMin +
                ", lightLevelMax=" + lightLevelMax +
                '}';
    }
}
