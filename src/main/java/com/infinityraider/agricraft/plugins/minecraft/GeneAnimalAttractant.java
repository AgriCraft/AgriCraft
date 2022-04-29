package com.infinityraider.agricraft.plugins.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.capability.CapabilityEatCropGoal;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGenePair;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.infinitylib.utility.EntityHelper;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Demo class to showcase the flexibility and potential of the genome system.
 *
 * These genes cause animals to be attracted to specific plants with the right genes.
 * These genes can only be activated by mutating on certain default plants,
 * from where they can be propagated through the mutation tree with some difficulty.
 *
 */
public class GeneAnimalAttractant implements IAgriGene<Boolean> {
    private static final int RANGE = 5;
    private static final int COOLDOWN = 60*20;
    private static final double SPEED = 1.0;
    private static final int PRIORITY = 4;

    private final String id;

    private final Class<? extends Mob> clazz;
    private final List<String> defaultPlantIds;

    private final IAllele<Boolean> alleleTrue;
    private final IAllele<Boolean> alleleFalse;

    private final Set<IAllele<Boolean>> alleles;

    private final TranslatableComponent geneDescription;
    private final TranslatableComponent genePairDescription;
    private final Vector3f colorDom;
    private final Vector3f colorRec;

    public GeneAnimalAttractant(String id, Class<? extends Mob> clazz, List<String> defaultPlantIds, Vector3f colorDom, Vector3f colorRec) {
        this.id = id;
        this.clazz = clazz;
        this.defaultPlantIds = defaultPlantIds;
        this.alleleTrue = new Allele(this, true);
        this.alleleFalse = new Allele(this, false);
        this.alleles = ImmutableSet.of(this.alleleTrue, this.alleleFalse);
        this.geneDescription = new TranslatableComponent(AgriCraft.instance.getModId() + ".gene.animal_attractant." + this.getId());
        this.genePairDescription = new TranslatableComponent(AgriCraft.instance.getModId() + ".tooltip.gene.animal_attractant." + this.getId());
        this.colorDom = colorDom;
        this.colorRec = colorRec;
        AgriCraft.instance.proxy().registerEventHandler(this);
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    public boolean isDefaultPlant(IAgriPlant plant) {
        return this.defaultPlantIds.contains(plant.getId());
    }

    @Nonnull
    @Override
    public IAllele<Boolean> defaultAllele(IAgriPlant plant) {
        return this.getAllele(false);
    }

    @Nonnull
    @Override
    public IAllele<Boolean> getAllele(Boolean value) {
        return value ? this.alleleTrue : this.alleleFalse;
    }

    @Nonnull
    @Override
    public IAllele<Boolean> readAlleleFromNBT(@Nonnull CompoundTag tag) {
        return this.getAllele(tag.contains(AgriNBT.KEY) && tag.getBoolean(AgriNBT.KEY));
    }

    @Nonnull
    @Override
    public Set<IAllele<Boolean>> allAlleles() {
        return this.alleles;
    }

    @Nonnull
    @Override
    public IMutator<Boolean> mutator() {
        return Mutator.INSTANCE;
    }

    @Nonnull
    @Override
    public IAgriGenePair<Boolean> generateGenePair(IAllele<Boolean> first, IAllele<Boolean> second) {
        return new GenePair(this, first, second);
    }

    @Nonnull
    @Override
    public TranslatableComponent getGeneDescription() {
        return this.geneDescription;
    }

    @Nonnull
    public TranslatableComponent getGenePairDescription() {
        return this.genePairDescription;
    }


    @Nonnull
    @Override
    public Vector3f getDominantColor() {
        return this.colorDom;
    }

    @Nonnull
    @Override
    public Vector3f getRecessiveColor() {
        return this.colorRec;
    }

    @Override
    public int getComparatorWeight() {
        return 1;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEntitySpawned(EntityJoinWorldEvent event) {
        if(!event.getWorld().isClientSide() && this.clazz.isInstance(event.getEntity())) {
            Mob entity = this.clazz.cast(event.getEntity());
            EatCropGoal goal = new EatCropGoal(this, entity, SPEED, COOLDOWN, this.defaultPlantIds);
            if(EntityHelper.injectGoal(entity, goal, PRIORITY)) {
                CapabilityEatCropGoal.getInstance().setCropEatGoal(entity, goal);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onCropGrowthTick(AgriCropEvent.Grow.General.Post event) {
        if(event.getCrop().world() != null && event.getCrop().hasPlant() && event.getCrop().isMature()) {
            BlockPos min = event.getCrop().getPosition().offset(-RANGE, -RANGE, -RANGE);
            BlockPos max = event.getCrop().getPosition().offset(RANGE, RANGE, RANGE);
            AABB range = new AABB(min, max);
            event.getCrop().world().getEntitiesOfClass(this.clazz, range, (entity) -> true).stream()
                    .map(entity -> CapabilityEatCropGoal.getInstance().getCropEatGoal(entity))
                    .filter(Objects::nonNull)
                    .filter(goal -> goal.isSuitableTarget(event.getCrop()))
                    .forEach(goal -> goal.addPotentialTarget(event.getCrop()));
        }
    }

    private static class Allele implements IAllele<Boolean> {
        private final IAgriGene<Boolean> gene;
        private final boolean trait;

        public Allele(IAgriGene<Boolean> gene, boolean trait) {
            this.gene = gene;
            this.trait = trait;
        }

        @Override
        public IAgriGene<Boolean> gene() {
            return this.gene;
        }

        @Override
        public Boolean trait() {
            return this.trait;
        }

        @Override
        public boolean isDominant(IAllele<Boolean> other) {
            return !this.trait();
        }

        @Override
        public TranslatableComponent getTooltip() {
            return null;
        }

        @Override
        public int comparatorValue() {
            return this.trait() ? 1 : 0;
        }

        @Nonnull
        @Override
        public CompoundTag writeToNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(AgriNBT.KEY, this.trait());
            return tag;
        }
    }

    private static class GenePair extends AgriGenePair<Boolean> {
        public GenePair(GeneAnimalAttractant gene, IAllele<Boolean> first, IAllele<Boolean> second) {
            super(gene, first, second);
            }


        @Override
        public final GeneAnimalAttractant getGene() {
            return (GeneAnimalAttractant) super.getGene();
        }


        @Override
        public void addTooltipDescription(Consumer<Component> consumer) {
            if(this.getTrait()) {
                consumer.accept(this.getGene().getGenePairDescription());
            }
        }
    }

    private static class Mutator implements IMutator<Boolean> {
        private static final Mutator INSTANCE = new Mutator();

        private static final double BASE_PROBABILITY = 0.5;

        private Mutator() {}

        @Override
        public IAgriGenePair<Boolean> pickOrMutate(IAgriCrop crop, IAgriGene<Boolean> gene, IAllele<Boolean> first, IAllele<Boolean> second,
                                                   Tuple<IAgriGenome, IAgriGenome> parents, Random random) {
            return gene.generateGenePair(
                    this.mutateAllele(gene, first, parents.getA(), random),
                    this.mutateAllele(gene, second, parents.getB(), random)
            );
        }

        protected IAllele<Boolean> mutateAllele(IAgriGene<Boolean> gene, IAllele<Boolean> allele, IAgriGenome genome, Random random) {
            if(gene instanceof GeneAnimalAttractant && ((GeneAnimalAttractant) gene).isDefaultPlant(genome.getPlant())) {
                //If the plant is a default plant, give a chance to mutate from false to true
                if(allele.trait()) {
                    // Never mutate to false on a default plant
                    return allele;
                } else {
                    // On a default 55% to mutate to true at mutativity 1, 100% at mutativity 10
                    double probability = (1 - BASE_PROBABILITY) + this.mutationChance(genome);
                    double roll = random.nextDouble();
                    if (roll < probability) {
                        return gene.getAllele(true);
                    } else {
                        return allele;
                    }
                }
            } else {
                // If the plant is not a default plant, it will never mutate from false to true,
                // and mostly mutate from true to false
                if (allele.trait()) {
                    // 95% to mutate to false at mutativity 1, 50% at mutativity 10
                    double probability = this.mutationChance(genome);
                    double roll = random.nextDouble();
                    if (roll < probability) {
                        return allele;
                    } else {
                        return gene.getAllele(false);
                    }
                } else {
                    // Never mutate to true
                    return allele;
                }
            }
        }

        protected double mutationChance(IAgriGenome genome) {
            int mutativity = genome.getStats().getMutativity();
            int max = AgriApi.getStatRegistry().mutativityStat().getMax();
            return BASE_PROBABILITY * (mutativity + 0.0) / (max + 0.0);
        }
    }

    public static class EatCropGoal extends Goal {
        private static final double DIST_SQ = 1.25 * 1.25;

        private final GeneAnimalAttractant gene;
        private final Mob entity;
        private final double speed;
        private final int cooldown;
        private final List<String> plantIds;

        private final Set<IAgriCrop> potentialTargets;
        private IAgriCrop target;

        private double targetX;
        private double targetY;
        private double targetZ;

        private int cooldownCounter;

        private EatCropGoal(GeneAnimalAttractant gene, Mob entity, double speed, int cooldown, List<String> plantIds) {
            this.gene = gene;
            this.entity = entity;
            this.speed = speed;
            this.cooldown = cooldown;
            this.plantIds = plantIds;
            this.potentialTargets = Sets.newIdentityHashSet();
        }

        protected GeneAnimalAttractant getGene() {
            return this.gene;
        }

        protected Mob getEntity() {
            return this.entity;
        }

        @Nullable
        protected IAgriCrop getTarget() {
            return this.target;
        }

        @Override
        public boolean canUse() {
            if (this.cooldownCounter > 0) {
                --this.cooldownCounter;
                return false;
            } else {
                this.target = this.findSuitableTarget();
                if (this.getTarget() == null) {
                    return false;
                } else {
                    this.potentialTargets.remove(this.target);
                    return true;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.isSuitableTarget(this.getTarget());
        }

        @Override
        public void start() {
            if(this.getTarget() != null) {
                this.targetX = this.getTarget().getPosition().getX() + 0.5;
                this.targetY = this.getTarget().getPosition().getY() + 0.5;
                this.targetZ = this.getTarget().getPosition().getZ() + 0.5;
            }
        }

        @Override
        public void stop() {
            this.target = null;
            this.getEntity().getNavigation().stop();
            this.setCooldown();
        }

        @Override
        public void tick() {
            if(this.getTarget() == null) {
                this.stop();
                return;
            }
            this.getEntity().getLookControl().setLookAt(
                    this.targetX, this.targetY, this.targetZ,
                    (float)(this.getEntity().getHeadRotSpeed() + 20),
                    (float)this.getEntity().getMaxHeadXRot());
            double distSq = this.getEntity().distanceToSqr(this.targetX, this.targetY, this.targetZ);
            double criterion = Math.max(DIST_SQ, this.getEntity().getBbWidth()*this.getEntity().getBbWidth());
            if (distSq <= criterion) {
                this.getEntity().getNavigation().stop();
                this.target.setGrowthStage(this.getTarget().getPlant().getInitialGrowthStage());
                if(this.getEntity() instanceof Animal) {
                   ((Animal) this.getEntity()).setInLoveTime(100);
                }
                this.stop();
            } else {
                this.getEntity().getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
            }

        }

        protected void setCooldown() {
            this.cooldownCounter = this.cooldown;
        }

        @Nullable
        protected IAgriCrop findSuitableTarget() {
            this.potentialTargets.removeIf(crop -> !this.isSuitableTarget(crop));
            return this.potentialTargets.stream().min((a, b) -> {
                        double dA = 100 * this.getEntity().distanceToSqr(a.getPosition().getX() + 0.5, a.getPosition().getY() + 0.5, a.getPosition().getZ() + 0.5);
                        double dB = 100 * this.getEntity().distanceToSqr(b.getPosition().getX() + 0.5, b.getPosition().getY() + 0.5, b.getPosition().getZ() + 0.5);
                        return (int) (dA - dB);
                    }).orElse(null);
        }

        protected boolean isSuitableTarget(IAgriCrop crop) {
            return crop != null
                    && crop.isValid()
                    && crop.hasPlant()
                    && crop.isMature()
                    && this.isSuitablePlant(crop.getPlant())
                    && crop.getGenome().map(this::hasSuitableGene).orElse(false);
        }

        protected boolean isSuitablePlant(IAgriPlant plant) {
            return plant.isPlant() && this.plantIds.contains(plant.getId());
        }

        protected boolean hasSuitableGene(IAgriGenome genome) {
            return genome.getGenePair(this.getGene()).getTrait();
        }

        public void addPotentialTarget(IAgriCrop crop) {
            this.potentialTargets.add(crop);
        }
    }
}
