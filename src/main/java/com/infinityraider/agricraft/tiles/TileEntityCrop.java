package com.infinityraider.agricraft.tiles;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.MathHelper;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.misc.IAgriDisplayable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCrop extends TileEntityBase implements IAgriCrop, IDebuggable, IAgriDisplayable {

    private AgriSeed seed;
    private int growthStage;
    private boolean crossCrop = false;

    // =========================================================================
    // Event Methods
    // <editor-fold>
    // =========================================================================
    @Override
    @Nonnull
    public MethodResult onGrowthTick() {
        // If remote world, pass.
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

        // Otherwise attempt to do something with the event.
        if (this.isCrossCrop() && AgriCraftConfig.crossOverChance > this.getRandom().nextDouble()) {
            this.crossOver();
            return MethodResult.SUCCESS;
        } else if (!this.hasSeed()) {
            this.spawn();
            return MethodResult.SUCCESS;
        } else if (this.isMature()) {
            this.spread();
            return MethodResult.SUCCESS;
        } else if ((this.seed.getPlant().getGrowthChanceBase() + this.seed.getStat().getGrowth() * this.seed.getPlant().getGrowthChanceBonus()) * AgriCraftConfig.growthMultiplier > this.getRandom().nextDouble()) {
            this.applyGrowthTick();
            return MethodResult.SUCCESS;
        }

        // The method has failed.
        return MethodResult.FAIL;
    }

    @Override
    @Nonnull
    public MethodResult onApplySeeds(@Nonnull AgriSeed seed, @Nullable EntityPlayer player) {
        // Ensure seed is valid.
        Preconditions.checkNotNull(seed, "`7Cannot apply a null seed!`r");

        // If on client side do nothing!
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

        // If this is a cross-crop or has a plant, do nothing!
        if (this.isCrossCrop() || this.hasSeed()) {
            return MethodResult.FAIL;
        }

        // If the soil is wrong, report and abort.
        if (!seed.getPlant().getGrowthRequirement().hasValidSoil(this.getWorld(), pos)) {
            MessageUtil.messagePlayer(player, "`7The soil is not valid for this seed. You can't plant it here.`r");
            return MethodResult.FAIL;
        }

        // If the additional conditions are wrong, warn and continue.
        if (!seed.getPlant().getGrowthRequirement().hasValidConditions(this.getWorld(), pos)) {
            MessageUtil.messagePlayer(player, "`7Caution: This plant has additional requirements that are unmet.`r");
        }

        // If the lighting is wrong, warn and continue.
        if (!seed.getPlant().getGrowthRequirement().hasValidLight(this.getWorld(), pos)) {
            MessageUtil.messagePlayer(player, "`7Caution: This plant won't grow with the current light level.`r");
        }

//        // Notify event listeners of a planting event.
//        if (!MinecraftForge.EVENT_BUS.post(new CropPlantedEvent(this, seed.get(), player))) {
//            // The event was aborted! Abort!
//            return false;
//        }
        // Actually plant the seed.
        this.setSeed(seed);

        // The method was a success!
        return MethodResult.SUCCESS;
    }

    @Override
    @Nonnull
    public MethodResult onApplyCrops(@Nullable EntityPlayer player) {
        // If on client side do nothing!
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

        // If this is a cross-crop or has a plant, do nothing!
        if (this.isCrossCrop() || this.hasSeed()) {
            return MethodResult.FAIL;
        }

        // Set the crop to be a cross-crop.
        final boolean wasSet = this.setCrossCrop(true);

        // Notify of success.
        return wasSet ? MethodResult.SUCCESS : MethodResult.FAIL;
    }

    @Override
    @Nonnull
    public MethodResult onBroken(@Nonnull Consumer<ItemStack> consumer, @Nullable EntityPlayer player) {
        // Verify the consumer is not null.
        Preconditions.checkNotNull(consumer);

        // If on client side do nothing!
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

        // Drop drops if should drop.
        if (player == null || !player.isCreative()) {
            this.getDrops(consumer, true, true, true);
        }

        // Remove the block.
        this.getWorld().removeTileEntity(pos);
        this.getWorld().setBlockToAir(pos);

        // The operation was a success.
        return MethodResult.SUCCESS;
    }

    // =========================================================================
    // Event Methods
    // </editor-fold>
    // =========================================================================
    // =========================================================================
    // Misc. Methods
    // <editor-fold>
    // =========================================================================
    public void getDrops(@Nonnull Consumer<ItemStack> consumer, boolean includeCropSticks, boolean includeSeeds, boolean includeProducts) {
        // Check that the consumer is not null.
        Preconditions.checkNotNull(consumer);

        // Perform crop stick drop.
        if (includeCropSticks) {
            consumer.accept(new ItemStack(AgriItems.getInstance().CROPS, this.isCrossCrop() ? 2 : 1));
        }
        if (this.hasSeed()) {
            if (includeSeeds && this.seed.getPlant().getSeedDropChanceBase() + this.growthStage * this.seed.getPlant().getSeedDropChanceBonus() > this.getRandom().nextDouble()) {
                consumer.accept(this.getSeed().toStack());
            }
            if (includeProducts && this.isMature()) {
                for (int trials = (this.seed.getStat().getGain() + 3) / 3; trials > 0; trials--) {
                    this.seed.getPlant().getHarvestProducts(consumer, this, this.seed.getStat(), this.getRandom());
                }
            }
        }
    }

    // =========================================================================
    // Misc. Methods
    // </editor-fold>
    // =========================================================================
    // =========================================================================
    // ISeedProvider Methods
    // <editor-fold>
    // =========================================================================
    @Override
    public boolean hasSeed() {
        return (this.seed != null);
    }

    @Override
    public AgriSeed getSeed() {
        return this.seed;
    }

    @Override
    public boolean acceptsSeed(@Nullable AgriSeed seed) {
        return (!this.crossCrop) && (this.seed == null || seed == null);
    }

    /**
     * This sets the seed for this crop. Using null will remove the seed. If
     * this call changes the value for seed: - It will first reset the growth
     * stage back to zero. - Then it will make sure that markForUpdate gets
     * called, if setGrowthStage didn't do it already. - And then it will return
     * true. Otherwise it will return false, indicating there was no change and
     * no update.
     *
     * @param seed the seed to associate with this instance.
     * @return true if this changed the seed and caused an update.
     */
    @Override
    public boolean setSeed(@Nullable AgriSeed seed) {
        // Check if the new value is already equal to the current seed. (I.e. same plant and same stats.)
        if (Objects.equal(this.seed, seed)) {
            // No change to make.
            return false;
        }

        // Otherwise set the seed to the new value.
        this.seed = seed;

        // Reset the growth stage.
        this.growthStage = 0;

        // Mark the tile for an update.
        this.markForUpdate();

        // Finally report that a change and an update did occur.
        return true;
    }

    // =========================================================================
    // ISeedProvider Methods
    // </editor-fold>
    // =========================================================================
    // =========================================================================
    // IAgriCrop Methods
    // -------------------------------------------------------------------------
    // The following methods were added due to a ForgeGradle obfuscation bug.
    // See: https://github.com/MinecraftForge/ForgeGradle/issues/205
    // <editor-fold>
    // =========================================================================
    @Override
    public BlockPos getCropPos() {
        return this.getPos();
    }

    @Override
    public World getCropWorld() {
        return this.getWorld();
    }

    // =========================================================================
    // IAgriCrop Methods
    // </editor-fold>
    // =========================================================================
    // =========================================================================
    // Misc.
    // =========================================================================
    @Override
    public boolean isCrossCrop() {
        return crossCrop;
    }

    @Override
    public boolean setCrossCrop(boolean status) {
        // If remote, change was failure.
        if (this.isRemote()) {
            return false;
        }

        // If we have a plant, change was a failure.
        if (this.hasSeed()) {
            return false;
        }

        // If the new state does not differ, change was a failure.
        if (this.crossCrop == status) {
            return false;
        }

        // Otherwise perform change.
        this.crossCrop = status;
        SoundType type = Blocks.PLANKS.getSoundType(null, null, null, null);
        this.getWorld().playSound(null, (double) ((float) xCoord() + 0.5F), (double) ((float) yCoord() + 0.5F), (double) ((float) zCoord() + 0.5F), type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
        this.markForUpdate();

        // Operation was a success!
        return true;
    }

    @Override
    public int getGrowthStage() {
        return this.growthStage;
    }

    @Override
    public boolean setGrowthStage(int stage) {
        // If remote, abort! No change to report.
        if (this.isRemote()) {
            return false;
        }

        // Make sure that the new value is valid.
        if (this.hasSeed()) {
            // If there is a plant currently, make sure the value is valid.
            stage = MathHelper.inRange(stage, 0, this.seed.getPlant().getGrowthStages() - 1);
        } else if (stage != 0) {
            // If this has no plant, the growth stage should be zero.
            // And if someone was trying to set it as non-zero, then that's a mistake.
            AgriCore.getCoreLogger().warn("Can't set a non-zero growth stage when a crop has no seed!");
            stage = 0;
        }

        // If the new growth stage is different, perform update.
        if (stage != this.growthStage) {
            this.growthStage = stage;
            this.markForUpdate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFertile(@Nullable IAgriPlant plant) {
        return (plant != null)
                && this.getWorld().isAirBlock(this.pos.up())
                && plant.getGrowthRequirement().isMet(this.getWorld(), pos);
    }

    @SideOnly(Side.CLIENT)
    public float getCropHeight() {
        return this.hasSeed() ? this.getSeed().getPlant().getHeight(getBlockMetadata()) : Constants.UNIT * 13;
    }

    @Override
    public boolean isMature() {
        return this.hasSeed() && (this.getGrowthStage() + 1 >= this.getSeed().getPlant().getGrowthStages());
    }

    @Override
    @Nonnull
    public Optional<IAgriSoil> getSoil() {
        final IBlockState state = this.getWorld().getBlockState(this.pos.down());
        return AgriApi.getSoilRegistry().get(state);
    }

    // =========================================================================
    // IWeedable Methods
    // =========================================================================
    public boolean spawn() {
        // If in remote world, abort!
        if (this.isRemote()) {
            return false;
        }

        // If already have plant, abort!
        if (this.hasSeed()) {
            return false;
        }

        // Attempt to spawn plant.
        for (IAgriPlant p : AgriApi.getPlantRegistry().all()) {
            if (p.getSpawnChance() > this.getRandom().nextDouble() && this.isFertile(p)) {
                this.setCrossCrop(false);
                this.setSeed(new AgriSeed(p, new PlantStats()));
                return true;
            }
        }

        // The operation was a failure.
        return false;
    }

    public boolean spread() {
        // If remote, abort.
        if (this.isRemote()) {
            return false;
        }

        // If don't have plant, abort.
        if (this.seed == null) {
            return false;
        }

        final IAgriPlant plant = this.seed.getPlant();

        // Try to spread in each direction.
        for (IAgriCrop crop : WorldHelper.getTileNeighbors(this.getWorld(), pos, IAgriCrop.class)) {
            // Note: Checking the probability is faster. Also check that this plant can be planted there.
            if (plant.getSpreadChance() > this.getRandom().nextDouble()
                    && plant.getGrowthRequirement().hasValidSoil(this.getWorld(), crop.getCropPos())) {
                final AgriSeed other = crop.getSeed();
                if (other == null) {
                    if (!crop.isCrossCrop()) {
                        crop.setSeed(this.seed);
                        return true;
                    }
                } else if (canOvertake(this.seed, other, this.getRandom())) {
                    crop.setCrossCrop(false);
                    crop.setSeed(this.seed);
                    return true;
                }

            }
        }

        // The spreading failed.
        return false;
    }

    public static boolean canOvertake(@Nonnull AgriSeed overtaker, @Nonnull AgriSeed victim, @Nonnull Random rand) {
        return overtaker.getPlant().isAggressive()
                && victim.getStat().getStrength() < overtaker.getStat().getStrength() * rand.nextDouble();
    }

    // =========================================================================
    // IAgriFertilizable Methods
    // =========================================================================
    @Override
    public boolean acceptsFertilizer(@Nullable IAgriFertilizer fertilizer) {
        // If the fertilizer is null, then well, no.
        if (fertilizer == null) {
            return false;
        } // First check if this is a cross crops. In all other cases this is a regular crop.
        else if (this.crossCrop) {
            // This is a cross crop.
            // + A cross over (e.g. mutation) can occur if both requirements are met.
            // - Otherwise the fertilizer usage is blocked by returning false.
            return AgriCraftConfig.fertilizerMutation && fertilizer.canTriggerMutation();
        } else if (!this.hasSeed()) {
            // This is a regular crop, AND it is empty.
            // + Random plants (such as weeds) have a chance to spawn.
            return true;
        } else {
            // This is a regular crop, AND it has a plant, AND that plant allows boosts from fertilizers.
            // + Immature plants can grow.
            // + Fully grown plants have a chance to spread clones to neighboring crops
            // return true;
            // This is a regular crop, AND it has a plant, AND that plant does NOT allow fertilizers.
            // - Block the consumption and application of the fertilizer by returning false.
            // return false;
            return this.getSeed().getPlant().isFertilizable();
        }
    }

    @Override
    @Nonnull
    public MethodResult onApplyFertilizer(@Nullable IAgriFertilizer fertilizer, @Nonnull Random rand) {
        // Validate
        Preconditions.checkNotNull(rand);

        // Do stuff
        if (this.isRemote()) {
            // The client doesn't need to do anything special.
            return MethodResult.PASS;
        } else if (!this.acceptsFertilizer(fertilizer)) {
            // This method has been called in error. Make a note and block the tick.
            AgriCore.getCoreLogger().warn("onApplyFertilizer should not be called if acceptFertilizer is false!");
            return MethodResult.FAIL;
        } else {
            // The regular growth tick method handles the rest, now that the special cases have been filtered out.
            this.onGrowthTick();
            return MethodResult.SUCCESS;
        }
    }

    // =========================================================================
    // IHarvestable methods.
    // =========================================================================
    @Override
    @Nonnull
    public MethodResult onHarvest(@Nonnull Consumer<ItemStack> consumer, @Nullable EntityPlayer player) {
        // Check that consumer is not null.
        Preconditions.checkNotNull(consumer);

        // Skip harvest if remote.
        if (this.isRemote()) {
            return MethodResult.PASS;
        } else if (this.isCrossCrop()) {
            if (this.setCrossCrop(false)) {
                consumer.accept(new ItemStack(AgriItems.getInstance().CROPS, 1));
            }
            return MethodResult.SUCCESS;
        } else if (this.canBeHarvested()) {
            this.getDrops(consumer, false, AgriCraftConfig.enableHarvestSeedDrops, true);
            this.setGrowthStage(0);
            return MethodResult.SUCCESS;
        } else {
            return MethodResult.PASS;
        }
    }

    // =========================================================================
    // IRakeable methods.
    // =========================================================================
    @Override
    public boolean onRaked(@Nonnull Consumer<ItemStack> consumer, @Nullable EntityPlayer player) {
        // Check that consumer is valid.
        Preconditions.checkNotNull(consumer);
        // Actually do something.
        if (!this.isRemote() && this.canBeRaked()) {
            this.getDrops(consumer, false, AgriCraftConfig.enableRakingSeedDrops, AgriCraftConfig.enableRakingItemDrops);
            this.setSeed(null);
            return true;
        } else {
            return false;
        }
    }

    // =========================================================================
    // Other
    // =========================================================================
    @Override
    public void writeTileNBT(@Nonnull NBTTagCompound tag) {
        Preconditions.checkNotNull(tag);
        tag.setBoolean(AgriNBT.CROSS_CROP, crossCrop);
        tag.setInteger(AgriNBT.META, growthStage);
        if (this.hasSeed()) {
            this.getSeed().getStat().writeToNBT(tag);
            tag.setString(AgriNBT.SEED, this.getSeed().getPlant().getId());
        }
    }

    @Override
    public void readTileNBT(@Nonnull NBTTagCompound tag) {
        Preconditions.checkNotNull(tag);
        final IAgriStat stat = AgriApi.getStatRegistry().valueOf(tag).orElse(null);
        final IAgriPlant plant = AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.SEED)).orElse(null);
        if (stat != null && plant != null) {
            this.seed = new AgriSeed(plant, stat);
        } else {
            this.seed = null;
        }
        this.growthStage = tag.getInteger(AgriNBT.META);
        this.crossCrop = tag.getBoolean(AgriNBT.CROSS_CROP);
    }

    /**
     * Apply a GROWTH increment
     */
    public void applyGrowthTick() {
        // If remote, abort.
        if (this.isRemote()) {
            return;
        }

        // If have no plant, abort.
        if (!this.hasSeed()) {
            return;
        }

        // If mature, abort.
        if (this.isMature()) {
            return;
        }

        // If not fertile, abort.
        if (!this.isFertile()) {
            return;
        }

        // Apply the growth tick.
        setGrowthStage(this.growthStage + 1);

        // The operation was a success.
        // return;
    }

    /**
     * the code that makes the crop cross with neighboring crops
     */
    public void crossOver() {
        AgriApi.getMutationEngine().attemptCross(this, this.getWorld().rand);
    }

    @Override
    public void addServerDebugInfo(@Nonnull Consumer<String> consumer) {
        Preconditions.checkNotNull(consumer);
        consumer.accept("CROP:");
        if (this.crossCrop) {
            consumer.accept(" - This is a crosscrop");
        } else if (this.hasSeed()) {
            final IAgriPlant plant = this.getSeed().getPlant();
            final IAgriStat stat = this.getSeed().getStat();
            if (plant.isWeed()) {
                consumer.accept(" - This crop has weeds");
            } else {
                consumer.accept(" - This crop has a plant");
            }
            consumer.accept(" - Plant: " + plant.getPlantName());
            consumer.accept(" - Id: " + plant.getId());
            consumer.accept(" - Stage: " + this.getGrowthStage());
            consumer.accept(" - Stages: " + plant.getGrowthStages());
            consumer.accept(" - Meta: " + this.getGrowthStage());
            consumer.accept(" - Growth: " + stat.getGrowth());
            consumer.accept(" - Gain: " + stat.getGain());
            consumer.accept(" - Strength: " + stat.getStrength());
            consumer.accept(" - Fertile: " + this.isFertile());
            consumer.accept(" - Mature: " + this.isMature());
            consumer.accept(" - AgriSoil: " + plant.getGrowthRequirement().getSoils().stream()
                    .findFirst().map(IAgriSoil::getId).orElse("None")
            );
        } else {
            consumer.accept(" - This crop has no plant");
        }
    }

    @Override
    public void addClientDebugInfo(@Nonnull Consumer<String> consumer) {
        Preconditions.checkNotNull(consumer);
        if (this.hasSeed()) {
            consumer.accept(" - Texture: " + this.getSeed().getPlant().getPrimaryPlantTexture(this.getGrowthStage()).toString());
        }
    }

    @Override
    public void addDisplayInfo(@Nonnull Consumer<String> information) {
        // Validate
        Preconditions.checkNotNull(information);

        // Add Soil Information
        information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.soil")+": " + this.getSoil().map(IAgriSoil::getName).orElse("Unknown"));

        if (this.hasSeed()) {
            // Fetch the plant.
            final IAgriPlant plant = this.getSeed().getPlant();
            // Fetch the stat.
            final IAgriStat stat = this.getSeed().getStat();
            information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.plant") + ": " + plant.getPlantName());
            //Add the SEED name.
            information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.seed") + ": " + plant.getSeedName());
            //Add the GROWTH.
            if (this.isMature()) {
                information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.growth") + ": " + AgriCore.getTranslator().translate("agricraft_tooltip.mature"));
            } else {
                information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.growth") + ": " + (int) (100.0 * (this.getGrowthStage() + 1) / plant.getGrowthStages()) + "%");
            }
            //Add the ANALYZED data.
            if (stat.isAnalyzed()) {
                stat.addStats(information);
            } else {
                information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.analyzed"));
            }
            //Add the fertility information.
            information.accept(AgriCore.getTranslator().translate(this.isFertile() ? "agricraft_tooltip.fertile" : "agricraft_tooltip.notFertile"));
        } else {
            information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.empty"));
        }

    }
}
