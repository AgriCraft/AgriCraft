package tehnut.resourceful.crops.api.base;

public class ChanceBuilder {

    private double extraSeed = 0.0;
    private double essenceDrop = 0.0;

    public ChanceBuilder() {

    }

    public ChanceBuilder setExtraSeed(double extraSeed) {
        this.extraSeed = extraSeed;
        return this;
    }

    public ChanceBuilder setEssenceDrop(double essenceDrop) {
        this.essenceDrop = essenceDrop;
        return this;
    }

    public Chance build() {
        return new Chance(extraSeed, essenceDrop);
    }
}
