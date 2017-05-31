package com.infinityraider.agricraft.tiles;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.AgriApi;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.misc.IAgriDisplayable;
import com.infinityraider.agricraft.api.misc.IAgriHarvestProduct;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import com.infinityraider.agricraft.api.util.MethodResult;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
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
    public MethodResult onGrowthTick() {
        // If remote world, pass.
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

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
    public MethodResult onApplySeeds(EntityPlayer player, AgriSeed seed) {
        // If on client side do nothing!
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

        // If this is a cross-crop or has a plant, do nothing!
        if (this.isCrossCrop() || this.hasSeed()) {
            return MethodResult.FAIL;
        }

        // If the growth requirement is not met, abort!
        if (!seed.getPlant().getGrowthRequirement().isMet(worldObj, pos)) {
            return MethodResult.FAIL;
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
    public MethodResult onApplyCrops(EntityPlayer player) {
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
    public MethodResult onBroken(EntityPlayer player) {
        // If on client side do nothing!
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

        // Drop drops if should drop.
        if (player == null || !player.isCreative()) {
            this.getDrops(drop -> WorldHelper.spawnItemInWorld(this.worldObj, this.pos, drop), true);
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
    public void getDrops(Consumer<ItemStack> consumer, boolean includeCropSticks) {
        // Perform crop stick drop.
        if (includeCropSticks) {
            consumer.accept(new ItemStack(AgriItems.getInstance().CROPS, this.isCrossCrop() ? 2 : 1));
        }
        if (this.hasSeed()) {
            if (this.seed.getPlant().getSeedDropChanceBase() + this.growthStage * this.seed.getPlant().getSeedDropChanceBonus() > this.getRandom().nextDouble()) {
                consumer.accept(this.getSeed().toStack());
            }
            if (this.isMature()) {
                for (int trials = (this.seed.getStat().getGain() + 3) / 3; trials > 0; trials--) {
                    for (IAgriHarvestProduct product : this.seed.getPlant().getProducts()) {
                        if (this.getRandom().nextDouble() <= product.getChance()) {
                            consumer.accept(product.toStack(this.getRandom()));
                        }
                    }
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
    public boolean acceptsSeed(AgriSeed seed) {
        return (!this.crossCrop) && (this.seed == null || seed == null);
    }

    @Override
    public boolean setSeed(AgriSeed seed) {
        final AgriSeed oldSeed = this.seed;
        this.seed = seed;
        this.markForUpdate();
        return this.seed != oldSeed;
    }

    // =========================================================================
    // ISeedProvider Methods
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
        worldObj.playSound(null, (double) ((float) xCoord() + 0.5F), (double) ((float) yCoord() + 0.5F), (double) ((float) zCoord() + 0.5F), type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
        this.markForUpdate();

        // Operation was a success!
        return true;
    }

    @Override
    public int getGrowthStage() {
        return this.growthStage;
    }

    @Override
    public void setGrowthStage(int stage) {
        // If remote, abort!
        if (this.isRemote()) {
            return;
        }

        // If no seed, abort!
        if (!this.hasSeed()) {
            return;
        }

        // Bring growth stage into bounds.
        if (stage < 0) {
            stage = 0;
        } else if (stage >= this.seed.getPlant().getGrowthStages()) {
            stage = this.seed.getPlant().getGrowthStages() - 1;
        }

        // If the new growth stage is different, perform update.
        if (stage != this.growthStage) {
            this.growthStage = stage;
            this.markForUpdate();
        }
    }

    @Override
    public boolean isFertile(IAgriPlant plant) {
        return worldObj.isAirBlock(this.pos.up())
                && plant.getGrowthRequirement().isMet(this.worldObj, pos);
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
    public Optional<IAgriSoil> getSoil() {
        return FuzzyStack
                .from(this.worldObj.getBlockState(this.pos.down()))
                .flatMap(stack -> AgriApi.SoilRegistry().get().stream().filter(soil -> soil.isVarient(stack)).findFirst());
    }

    // =========================================================================
    // IWeedable Methods
    // =========================================================================
    @Override
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
        for (IAgriPlant p : AgriApi.PlantRegistry().get().all()) {
            if (p.getSpawnChance() > this.getRandom().nextDouble() && this.isFertile(p)) {
                this.setCrossCrop(false);
                this.setSeed(new AgriSeed(p, new PlantStats()));
                return true;
            }
        }

        // The operation was a failure.
        return false;
    }

    @Override
    public boolean spread() {
        // If remote, abort.
        if (this.isRemote()) {
            return false;
        }

        // Fetch the seed;
        final AgriSeed seed = this.getSeed();

        // If don't have plant, abort.
        if (seed == null) {
            return false;
        }

        final IAgriPlant plant = seed.getPlant();

        // If don't roll a spread event, abort.
        if (plant.getSpreadChance() <= this.getRandom().nextDouble()) {
            for (IAgriCrop crop : WorldHelper.getTileNeighbors(worldObj, pos, IAgriCrop.class)) {
                final AgriSeed other = crop.getSeed();
                if (other == null) {
                    if (!crop.isCrossCrop()) {
                        crop.setSeed(seed);
                        return true;
                    }
                } else if (canOvertake(seed, other, this.getRandom())) {
                    crop.setCrossCrop(false);
                    crop.setSeed(seed);
                    return true;
                }

            }
        }

        // The spreading failed.
        return false;
    }

    public static boolean canOvertake(AgriSeed overtaker, AgriSeed victim, Random rand) {
        return overtaker.getPlant().isAggressive()
                && victim.getStat().getStrength() < overtaker.getStat().getStrength() * rand.nextDouble();
    }

    // =========================================================================
    // IFertilizable Methods
    // =========================================================================
    @Override
    public boolean acceptsFertilizer(IAgriFertilizer fertilizer) {
        if (this.crossCrop) {
            return AgriCraftConfig.fertilizerMutation && fertilizer.canTriggerMutation();
        }
        return this.hasSeed() && this.getSeed().getPlant().isFertilizable();
    }

    @Override
    public MethodResult onApplyFertilizer(IAgriFertilizer fertilizer, Random rand) {
        // If in remote world, pass.
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

        // Attempt to fertilize plant.
        if (this.hasSeed() && this.getSeed().getPlant().isFertilizable() && this.getGrowthStage() < Constants.MATURE) {
            ((BlockCrop) AgriBlocks.getInstance().CROP).grow(getWorld(), rand, getPos(), getWorld().getBlockState(getPos()));
            return MethodResult.SUCCESS;
        }

        // Attempt to perform mutation.
        if (this.isCrossCrop() && AgriCraftConfig.fertilizerMutation && fertilizer.canTriggerMutation()) {
            this.crossOver();
            return MethodResult.SUCCESS;
        }

        // The action was a failure.
        return MethodResult.FAIL;
    }

    // =========================================================================
    // IHarvestable methods.
    // =========================================================================
    @Override
    public boolean onHarvest(@Nullable EntityPlayer player) {
        if (!this.isRemote()) {
            if (this.isCrossCrop()) {
                this.setCrossCrop(false);
                WorldHelper.spawnItemInWorld(this.worldObj, this.pos, new ItemStack(AgriItems.getInstance().CROPS, 1));
                return false;
            } else if (this.canBeHarvested()) {
                this.getDrops(stack -> WorldHelper.spawnItemInWorld(this.worldObj, this.pos, stack), false);
                this.setGrowthStage(0);
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // IRakeable methods.
    // =========================================================================
    @Override
    public boolean onRaked(@Nullable EntityPlayer player) {
        if (!this.isRemote() && this.canBeRaked()) {
            this.getDrops(stack -> WorldHelper.spawnItemInWorld(this.worldObj, this.pos, stack), false);
            this.setGrowthStage(0);
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
    public void writeTileNBT(NBTTagCompound tag) {
        tag.setBoolean(AgriNBT.CROSS_CROP, crossCrop);
        tag.setInteger(AgriNBT.META, growthStage);
        if (this.hasSeed()) {
            this.getSeed().getStat().writeToNBT(tag);
            tag.setString(AgriNBT.SEED, this.getSeed().getPlant().getId());
        }
    }

    @Override
    public void readTileNBT(NBTTagCompound tag) {
        final IAgriStat stat = AgriApi.StatRegistry().get().valueOf(tag).orElse(null);
        final IAgriPlant plant = AgriApi.PlantRegistry().get().get(tag.getString(AgriNBT.SEED)).orElse(null);
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
        return;
    }

    /**
     * the code that makes the crop cross with neighboring crops
     */
    public void crossOver() {
        AgriApi.MutationEngine().get().attemptCross(this, this.worldObj.rand);
    }

    @Override
    public void addServerDebugInfo(Consumer<String> consumer) {
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
    public void addClientDebugInfo(Consumer<String> consumer) {
        if (this.hasSeed()) {
            consumer.accept(" - Texture: " + this.getSeed().getPlant().getPrimaryPlantTexture(this.getGrowthStage()).toString());
        }
    }

    @Override
    public void addDisplayInfo(Consumer<String> information) {

        // Add Soil Information
        information.accept("Soil: " + this.getSoil().map(IAgriSoil::getName).orElse("Unknown"));

        if (this.hasSeed()) {
            // Fetch the plant.
            final IAgriPlant plant = this.getSeed().getPlant();
            // Fetch the stat.
            final IAgriStat stat = this.getSeed().getStat();

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
