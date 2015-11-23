package tehnut.resourceful.crops.api.base;

import tehnut.resourceful.crops.api.util.BlockStack;

public class CompatBuilder {

    private Compat.CompatExNihilio compatExNihilio = new CompatExNihilioBuilder().build();

    public CompatBuilder() {

    }

    public CompatBuilder setCompatExNihilio(Compat.CompatExNihilio compatExNihilio) {
        this.compatExNihilio = compatExNihilio;
        return this;
    }

    public Compat build() {
        return new Compat(compatExNihilio);
    }

    public static class CompatExNihilioBuilder {

        private BlockStack sourceBlock = null;
        private int sieveChance;

        public CompatExNihilioBuilder() {

        }

        public CompatExNihilioBuilder setSourceBlock(BlockStack sourceBlock) {
            this.sourceBlock = sourceBlock;
            return this;
        }

        public CompatExNihilioBuilder setSieveChance(int sieveChance) {
            this.sieveChance = sieveChance;
            return this;
        }

        public Compat.CompatExNihilio build() {
            return new Compat.CompatExNihilio(sourceBlock, sieveChance);
        }
    }
}
