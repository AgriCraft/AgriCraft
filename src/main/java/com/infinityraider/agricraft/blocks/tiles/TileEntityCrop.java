package com.infinityraider.agricraft.blocks.tiles;

import com.infinityraider.agricraft.api.crop.IAdditionalCropData;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.apiimpl.MutationEngine;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.AgriWorldHelper;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.agricraft.agricore.util.MathHelper;
import com.infinityraider.agricraft.api.misc.IAgriDisplayable;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.apiimpl.SoilRegistry;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.AgriProperties;
import java.util.Optional;

public class TileEntityCrop extends TileEntityBase implements IAgriCrop, IDebuggable, IAgriDisplayable {

    public static final String NAME = "crops";

    private boolean crossCrop = false;

    // Just in case...
    @Nullable
    private IAgriStat stats;
    private IAgriPlant plant;
    private IAdditionalCropData data;

    // =========================================================================
    // IPlantProvider Methods
    // =========================================================================
    @Override
    public final boolean hasPlant() {
        return this.plant != null;
    }

    @Nonnull
    @Override
    public final Optional<IAgriPlant> getPlant() {
        return Optional.ofNullable(this.plant);
    }

    // =========================================================================
    // IStatProvider Methods
    // =========================================================================
    @Override
    public final boolean hasStat() {
        return this.stats != null;
    }

    @Nonnull
    @Override
    public final Optional<IAgriStat> getStat() {
        return Optional.ofNullable(this.stats);
    }

    // =========================================================================
    // Misc.
    // =========================================================================
    @Override
    public boolean isCrossCrop() {
        return crossCrop;
    }

    @Override
    public void setCrossCrop(boolean status) {
        if (status != this.crossCrop) {
            this.crossCrop = status;
            SoundType type = Blocks.PLANKS.getSoundType();
            worldObj.playSound(null, (double) ((float) xCoord() + 0.5F), (double) ((float) yCoord() + 0.5F), (double) ((float) zCoord() + 0.5F), type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
            this.worldObj.setBlockState(pos, AgriProperties.CROSSCROP.applyToBlockState(this.getState(), status), 2);
            this.markForUpdate();
        }
    }

    public int getGrowthRate() {
        // TODO: update!
        return AgriCraftConfig.weedGrowthRate;
    }

    @Override
    public int getGrowthStage() {
        return this.getBlockMetadata();
    }

    @Override
    public void setGrowthStage(int stage) {
        if (this.hasPlant() && this.stats != null) {
            stage = MathHelper.inRange(stage, 0, Constants.MATURE);
            this.stats = this.stats.withMeta(stage);
            this.worldObj.setBlockState(pos, AgriProperties.GROWTHSTAGE.applyToBlockState(this.getState(), stage), 2);
            this.markForUpdate();
        }
    }

    @Override
    public boolean acceptsPlant(IAgriPlant plant) {
        return plant != null && !this.hasPlant() && !this.isCrossCrop();
    }

    @Override
    public boolean setPlant(IAgriPlant plant) {
        if ((!this.crossCrop) && (!this.hasPlant()) && (plant != null)) {
            this.plant = plant;
            plant.onSeedPlanted(worldObj, pos);
            IAdditionalCropData new_data = plant.getInitialCropData(worldObj, getPos(), this);
            if (new_data != null) {
                this.data = new_data;
            }
            this.markForUpdate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<IAgriPlant> removePlant() {
        final IAgriPlant oldPlant = this.plant;
        this.plant = null;
        this.data = null;
        if (oldPlant != null) {
            oldPlant.onPlantRemoved(worldObj, pos);
        }
        this.markForUpdate();
        return Optional.ofNullable(oldPlant);
    }

    @Override
    public boolean acceptsStat(IAgriStat stat) {
        return true;
    }

    @Override
    public boolean setStat(IAgriStat stat) {
        this.stats = stat;
        if (stat != null) {
            this.setGrowthStage(stat.getMeta());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<IAgriStat> removeStat() {
        final IAgriStat old = this.stats;
        this.stats = new PlantStats();
        this.markForUpdate();
        return Optional.ofNullable(old);
    }

    /*
	 * check if the crop is fertile
     */
    @Override
    public boolean isFertile(AgriSeed seed) {
        return worldObj.isAirBlock(this.getPos().add(0, 1, 0))
                && seed.getPlant().getGrowthRequirement().isMet(this.worldObj, pos);
    }

    /*
	 * gets the height of the crop
     */
    @SideOnly(Side.CLIENT)
    public float getCropHeight() {
        return hasPlant() ? plant.getHeight(getBlockMetadata()) : Constants.UNIT * 13;
    }

    /**
     * check the block if the plant is mature
     */
    @Override
    public boolean isMature() {
        return getGrowthStage() >= Constants.MATURE;
    }

    @Override
    public Optional<IAgriSoil> getSoil() {
        return SoilRegistry.getInstance().getSoil(this.worldObj.getBlockState(this.pos.add(0, -1, 0)));
    }

    // =========================================================================
    // IWeedable Methods
    // =========================================================================
    @Override
    public boolean spawn(Random rand) {
        if (!hasPlant()) {
            for (IAgriPlant p : PlantRegistry.getInstance().getPlants()) {
                if (p.getSpawnChance() > rand.nextDouble()) {
                    this.setCrossCrop(false);
                    this.setStat(new PlantStats());
                    this.setPlant(p);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean spread(Random rand) {
        if (this.hasPlant() && this.plant.getSpreadChance() > rand.nextDouble()) {
            for (IAgriCrop crop : this.getNeighbours()) {
                if (!this.getPlant().equals(crop.getPlant())) {
                    if (!crop.hasPlant() && !crop.isCrossCrop()) {
                        crop.setPlant(plant);
                        return true;
                    } else if (this.plant.isAgressive() && this.stats != null && this.stats.getStrength() >= rand.nextDouble() * this.stats.getStrength()) {
                        crop.setCrossCrop(false);
                        crop.setPlant(plant);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean clearWeed() {
        if (this.plant != null && this.plant.isWeedable()) {
            this.removePlant();
            return true;
        } else {
            return false;
        }
    }

    // =========================================================================
    // IFertilizable Methods
    // =========================================================================
    @Override
    public boolean acceptsFertilizer(IAgriFertilizer fertilizer) {
        if (this.crossCrop) {
            return AgriCraftConfig.bonemealMutation && fertilizer.canTriggerMutation();
        }
        if (this.hasPlant()) {
            return fertilizer.isFertilizerAllowed(plant.getTier());
        }
        return false;
    }

    //when fertilizer is applied
    @Override
    public boolean onApplyFertilizer(IAgriFertilizer fertilizer, Random rand) {
        if (this.hasPlant() && this.getGrowthStage() < Constants.MATURE) {
            ((BlockCrop) AgriBlocks.getInstance().CROP).grow(getWorld(), rand, getPos(), getWorld().getBlockState(getPos()));
            return true;
        } else if (this.isCrossCrop() && AgriCraftConfig.bonemealMutation && fertilizer.canTriggerMutation()) {
            this.crossOver();
            return true;
        }
        return false;
    }

    // =========================================================================
    // IHarvestable methods.
    // =========================================================================
    @Override
    public boolean canHarvest() {
        return hasPlant() && isMature();
    }

    @Override
    public boolean harvest(@Nullable EntityPlayer player) {
        // TODO: Correct horrible hack.
        // This is a terrible, aweful method of doing this.
        return ((BlockCrop) getWorld().getBlockState(pos).getBlock()).harvest(getWorld(), getPos(), getWorld().getBlockState(getPos()), player, this);
    }

    @Override
    public List<ItemStack> getFruits() {
        return plant.getFruitsOnHarvest(stats.getGain(), worldObj.rand);
    }

    // =========================================================================
    // Other
    // =========================================================================
    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public IAdditionalCropData getAdditionalCropData() {
        return this.data;
    }

    @Override
    public void validate() {
        super.validate();
        if (this.hasPlant()) {
            plant.onValidate(worldObj, pos, this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.hasPlant()) {
            plant.onInvalidate(worldObj, pos, this);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (this.hasPlant()) {
            plant.onChunkUnload(worldObj, pos, this);
        }
    }

    @Override
    public void writeTileNBT(NBTTagCompound tag) {
        if (this.stats != null) {
            this.stats.writeToNBT(tag);
            tag.setBoolean(AgriNBT.CROSS_CROP, crossCrop);
            if (plant != null) {
                tag.setString(AgriNBT.SEED, plant.getId());
            }
            if (getAdditionalCropData() != null) {
                tag.setTag(AgriNBT.INVENTORY, getAdditionalCropData().writeToNBT());
            }
        }
        //AgriCore.getLogger("Plant-Tag").debug("Write Tag: {0}", tag);
    }

    @Override
    public void readTileNBT(NBTTagCompound tag) {
        this.stats = StatRegistry.getInstance().valueOf(tag).orElse(null);
        this.crossCrop = tag.getBoolean(AgriNBT.CROSS_CROP);
        this.plant = PlantRegistry.getInstance().getPlant(tag.getString(AgriNBT.SEED));
        if (tag.hasKey(AgriNBT.INVENTORY) && this.plant != null) {
            this.data = plant.readCropDataFromNBT(tag.getCompoundTag(AgriNBT.INVENTORY));
        }
        //AgriCore.getLogger("Plant-Tag").debug("Read Tag: {0}", tag);
    }

    /**
     * Apply a GROWTH increment
     */
    public void applyGrowthTick() {
        int meta = getGrowthStage();
        if (hasPlant()) {
            plant.onAllowedGrowthTick(worldObj, pos, meta);
        }
        IBlockState state = getWorld().getBlockState(getPos());
        if (canWeed() || !plant.isMature(getWorld(), pos, state)) {
            setGrowthStage(meta + 1);
            /* TODO: Announce Growth Tick Via API! */
        }
    }

    /**
     * the code that makes the crop cross with neighboring crops
     */
    public void crossOver() {
        MutationEngine.getInstance().attemptCross(this, this.worldObj.rand);
    }

    /**
     * @return a list with all neighbours of type <code>TileEntityCrop</code> in
     * the NORTH, SOUTH, EAST and WEST DIRECTION
     */
    @Override
    public List<IAgriCrop> getNeighbours() {
        return AgriWorldHelper.getTileNeighbors(worldObj, pos, IAgriCrop.class);
    }

    /**
     * @return a list with only mature neighbours of type
     * <code>TileEntityCrop</code>
     */
    @Override
    public List<IAgriCrop> getMatureNeighbours() {
        List<IAgriCrop> neighbours = getNeighbours();
        neighbours.removeIf((p) -> !(p.hasPlant() && p.isMature()));
        return neighbours;
    }

    @Override
    public void addServerDebugInfo(List<String> list) {
        list.add("CROP:");
        if (this.crossCrop) {
            list.add(" - This is a crosscrop");
        } else if (this.hasSeed()) {
            if (this.plant.isWeedable()) {
                list.add(" - This crop has weeds");
            } else {
                list.add(" - This crop has a plant");
            }
            list.add(" - Plant: " + this.plant.getPlantName());
            list.add(" - Id: " + this.plant.getId());
            list.add(" - Tier: " + plant.getTier());
            list.add(" - Stage: " + this.getGrowthStage());
            list.add(" - Meta: " + this.getBlockMetadata());
            list.add(" - Growth: " + this.stats.getGrowth());
            list.add(" - Gain: " + this.stats.getGain());
            list.add(" - Strength: " + this.stats.getStrength());
            list.add(" - Fertile: " + this.isFertile());
            list.add(" - Mature: " + this.isMature());
            list.add(" - AgriSoil: " + this.plant.getGrowthRequirement().getSoils().stream()
                    .findFirst().map(s -> s.getId()).orElse("Any")
            );
        } else {
            list.add(" - This crop has no plant");
        }
    }

    @Override
    public void addClientDebugInfo(List<String> lines) {
        lines.add(" - Texture: " + this.plant.getPrimaryPlantTexture(this.getGrowthStage()).toString());
    }

    @Override
    public void addDisplayInfo(List information) {

        // Add Soil Information
        information.add("Soil: " + this.getSoil().map(s -> s.getName()).orElse("Unknown"));

        if (this.hasPlant()) {
            //Add the SEED name.
            information.add(AgriCore.getTranslator().translate("agricraft_tooltip.seed") + ": " + this.plant.getSeedName());
            //Add the GROWTH.
            if (this.isMature()) {
                information.add(AgriCore.getTranslator().translate("agricraft_tooltip.growthStage") + ": " + AgriCore.getTranslator().translate("agricraft_tooltip.mature"));
            } else {
                information.add(AgriCore.getTranslator().translate("agricraft_tooltip.growthStage") + ": " + ((int) (100.0 * this.getBlockMetadata() / Constants.MATURE) + "%"));
            }
            //Add the ANALYZED data.
            if (this.stats.isAnalyzed()) {
                this.stats.addStats(information);
            } else {
                information.add(AgriCore.getTranslator().translate("agricraft_tooltip.analyzed"));
            }
            //Add the fertility information.
            information.add(AgriCore.getTranslator().translate(this.isFertile() ? "agricraft_tooltip.fertile" : "agricraft_tooltip.notFertile"));
        } else if (this.canWeed()) {
            information.add(AgriCore.getTranslator().translate("agricraft_tooltip.weeds"));
        } else {
            information.add(AgriCore.getTranslator().translate("agricraft_tooltip.empty"));
        }

    }
}
