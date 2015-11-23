package tehnut.resourceful.crops.api.base;

import java.awt.*;

/**
 * Factory for creating Seeds/Crops/Shards.
 * Documentation for each field can be found in {@link Seed}
 */
public class SeedBuilder {

    private String name;
    private int tier;
    private int amount;
    private String input;
    private String output;
    private String secondOutput = null;
    private String thirdOutput = null;
    private Color color;
    private SeedReq seedReq = new SeedReqBuilder().build();
    private Chance chance = new ChanceBuilder().build();
    private boolean compatSeed = false;
    private Compat compat = new CompatBuilder().build();

    public SeedBuilder() {

    }

    public SeedBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SeedBuilder setTier(int tier) {
        if (tier > 4)
            this.tier = 4;
        else if (tier < 1)
            this.tier = 1;
        else
            this.tier = tier;
        return this;
    }

    public SeedBuilder setAmount(int amount) {
        if (amount > 64)
            this.amount = 64;
        else
            this.amount = amount;
        return this;
    }

    public SeedBuilder setInput(String input) {
        this.input = input;
        return this;
    }

    public SeedBuilder setOutput(String output) {
        this.output = output;
        return this;
    }

    public SeedBuilder setSecondOutput(String secondOutput) {
        this.secondOutput = secondOutput;
        return this;
    }

    public SeedBuilder setThirdOutput(String thirdOutput) {
        this.thirdOutput = thirdOutput;
        return this;
    }

    public SeedBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public SeedBuilder setSeedReq(SeedReq seedReq) {
        this.seedReq = seedReq;
        return this;
    }

    public SeedBuilder setChance(Chance chance) {
        this.chance = chance;
        return this;
    }

    public SeedBuilder setCompatSeed(boolean compatSeed) {
        this.compatSeed = compatSeed;
        return this;
    }

    public SeedBuilder setCompat(Compat compat) {
        this.compat = compat;
        return this;
    }

    public Seed build() {
        return new Seed(name, tier, amount, input, output, secondOutput, thirdOutput, color, seedReq, chance, compatSeed, compat);
    }
}
