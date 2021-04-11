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
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GeneAnimalAttractant implements IAgriGene<Boolean> {
    private static final int RANGE = 5;
    private static final int COOLDOWN = 60*20;
    private static final int PRIORITY = 3;

    private final String id;

    private final Class<? extends MobEntity> clazz;
    private final List<String> defaultPlantIds;

    private final IAllele<Boolean> alleleTrue;
    private final IAllele<Boolean> alleleFalse;

    private final Set<IAllele<Boolean>> alleles;

    private final TranslationTextComponent description;
    private final Vector3f colorDom;
    private final Vector3f colorRec;

    public GeneAnimalAttractant(String id, Class<? extends MobEntity> clazz, List<String> defaultPlantIds, Vector3f colorDom, Vector3f colorRec) {
        this.id = id;
        this.clazz = clazz;
        this.defaultPlantIds = defaultPlantIds;
        this.alleleTrue = new Allele(this, true);
        this.alleleFalse = new Allele(this, false);
        this.alleles = ImmutableSet.of(this.alleleTrue, this.alleleFalse);
        this.description = new TranslationTextComponent(AgriCraft.instance.getModId() + ".gene.animal_attractant." + this.getId());
        this.colorDom = colorDom;
        this.colorRec = colorRec;
        AgriCraft.instance.proxy().registerEventHandler(this);
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IAllele<Boolean> defaultAllele(IAgriPlant plant) {
        return this.getAllele(defaultPlantIds.contains(plant.getId()));
    }

    @Nonnull
    @Override
    public IAllele<Boolean> getAllele(Boolean value) {
        return value ? this.alleleTrue : this.alleleFalse;
    }

    @Nonnull
    @Override
    public IAllele<Boolean> readAlleleFromNBT(@Nonnull CompoundNBT tag) {
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
        return new AgriGenePair<>(this, first, second);
    }

    @Nonnull
    @Override
    public IFormattableTextComponent getDescription() {
        return this.description;
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
    public boolean isHidden() {
        return true;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEntitySpawned(LivingSpawnEvent event) {
        if(!event.getWorld().isRemote() && this.clazz.isInstance(event.getEntityLiving())) {
            MobEntity entity = this.clazz.cast(event.getEntityLiving());
            EatCropGoal goal = new EatCropGoal(entity, entity.getAIMoveSpeed(), COOLDOWN, this.defaultPlantIds);
            if(EntityHelper.injectGoal(entity, goal, PRIORITY)) {
                CapabilityEatCropGoal.getInstance().setCropEatGoal(entity, goal);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onCropGrowthTick(AgriCropEvent.Grow.General.Post event) {
        if(event.getCrop().getWorld() != null && event.getCrop().hasPlant() && event.getCrop().isMature()) {
            BlockPos min = event.getCrop().getPosition().add(-RANGE, -RANGE, -RANGE);
            BlockPos max = event.getCrop().getPosition().add(RANGE, RANGE, RANGE);
            AxisAlignedBB range = new AxisAlignedBB(min, max);
            event.getCrop().getWorld().getEntitiesWithinAABB(this.clazz, range, (entity) -> true).stream()
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
        public IFormattableTextComponent getTooltip() {
            return null;
        }

        @Nonnull
        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean(AgriNBT.KEY, this.trait());
            return tag;
        }
    }

    private static class Mutator implements IMutator<Boolean> {
        private static final Mutator INSTANCE = new Mutator();

        private static final double BASE_PROBABILITY = 0.5;

        private Mutator() {}

        @Override
        public IAgriGenePair<Boolean> pickOrMutate(IAgriGene<Boolean> gene, IAllele<Boolean> first, IAllele<Boolean> second,
                                                   Tuple<IAgriGenome, IAgriGenome> parents, Random random) {
            return gene.generateGenePair(
                    this.handle(gene, first, parents.getA(), random),
                    this.handle(gene, second, parents.getB(), random)
            );
        }

        protected IAllele<Boolean> handle(IAgriGene<Boolean> gene, IAllele<Boolean> allele, IAgriGenome genome, Random random) {
            if(allele.trait()) {
                // true alleles will mostly mutate to false (90% at mutativity 1, 50% at mutativity 10)
                double probability = this.mutationChance(genome);
                double roll = random.nextDouble();
                if (roll < probability) {
                    return allele;
                } else {
                    return gene.getAllele(false);
                }
            } else {
                // false alleles will never mutate to true
                return allele;
            }
        }

        protected double mutationChance(IAgriGenome genome) {
            int mutativity = genome.getStats().getMutativity();
            int max = AgriApi.getStatRegistry().mutativityStat().getMax();
            return BASE_PROBABILITY * (mutativity + 0.0) / (max + 0.0);
        }
    }

    public static class EatCropGoal extends Goal {
        private static final double DIST_SQ = 0.75 * 0.75;

        protected final MobEntity entity;
        private final double speed;
        private final int cooldown;
        private final List<String> plantIds;

        private final Set<IAgriCrop> potentialTargets;
        private IAgriCrop target;

        private double targetX;
        private double targetY;
        private double targetZ;

        private int cooldownCounter;

        private EatCropGoal(MobEntity entity, double speed, int cooldown, List<String> plantIds) {
            this.entity = entity;
            this.speed = speed;
            this.cooldown = cooldown;
            this.plantIds = plantIds;
            if (!(entity.getNavigator() instanceof GroundPathNavigator) && !(entity.getNavigator() instanceof FlyingPathNavigator)) {
                throw new IllegalArgumentException("Unsupported mob type for EatCropGoal");
            }
            this.potentialTargets = Sets.newIdentityHashSet();
        }

        protected MobEntity getEntity() {
            return this.entity;
        }

        @Nullable
        protected IAgriCrop getTarget() {
            return this.target;
        }

        @Override
        public boolean shouldExecute() {
            if (this.cooldownCounter > 0) {
                --this.cooldownCounter;
                return false;
            } else {
                this.target = this.findSuitableTarget();
                if (this.getTarget() == null) {
                    this.resetTask();
                    return false;
                } else {
                    this.potentialTargets.remove(this.target);
                    return true;
                }
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.isSuitableTarget(this.getTarget());
        }

        @Override
        public void startExecuting() {
            if(this.getTarget() != null) {
                this.targetX = this.getTarget().getPosition().getX() + 0.5;
                this.targetY = this.getTarget().getPosition().getY() + 0.5;
                this.targetZ = this.getTarget().getPosition().getZ() + 0.5;
            }
        }

        @Override
        public void resetTask() {
            this.target = null;
            this.getEntity().getNavigator().clearPath();
            this.setCooldown();
        }

        @Override
        public void tick() {
            if(this.getTarget() == null) {
                this.resetTask();
                return;
            }
            this.getEntity().getLookController().setLookPosition(
                    this.targetX, this.targetY, this.targetZ,
                    (float)(this.getEntity().getHorizontalFaceSpeed() + 20),
                    (float)this.getEntity().getVerticalFaceSpeed());
            if (this.getEntity().getDistanceSq(this.targetX, this.targetY, this.targetZ) < DIST_SQ) {
                this.getEntity().getNavigator().clearPath();
                this.target.setGrowthStage(this.getTarget().getPlant().getInitialGrowthStage());
                if(this.getEntity() instanceof AnimalEntity) {
                   ((AnimalEntity) this.getEntity()).setInLove(100);
                }
                this.resetTask();
            } else {
                this.getEntity().getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
            }

        }

        protected void setCooldown() {
            this.cooldownCounter = this.cooldown;
        }

        @Nullable
        protected IAgriCrop findSuitableTarget() {
            this.potentialTargets.removeIf(crop -> !this.isSuitableTarget(crop));
            return this.potentialTargets.stream().min((a, b) -> {
                        double dA = 100 * this.getEntity().getDistanceSq(a.getPosition().getX() + 0.5, a.getPosition().getY() + 0.5, a.getPosition().getZ() + 0.5);
                        double dB = 100 * this.getEntity().getDistanceSq(b.getPosition().getX() + 0.5, b.getPosition().getY() + 0.5, b.getPosition().getZ() + 0.5);
                        return (int) (dA - dB);
                    }).orElse(null);
        }

        protected boolean isSuitableTarget(IAgriCrop crop) {
            return crop != null
                    && crop.isValid()
                    && crop.hasPlant()
                    && crop.isMature()
                    && this.isSuitablePlant(crop.getPlant());
        }

        protected boolean isSuitablePlant(IAgriPlant plant) {
            return plant.isPlant() && this.plantIds.contains(plant.getId());
        }

        public void addPotentialTarget(IAgriCrop crop) {
            this.potentialTargets.add(crop);
        }
    }
}
