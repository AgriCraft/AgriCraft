package com.infinityraider.agricraft.blocks.tiles;

import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.crop.IAdditionalCropData;
import com.infinityraider.agricraft.api.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.items.IAgriTrowelItem;
import com.infinityraider.agricraft.apiimpl.*;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.items.ItemDebugger;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.block.SoundType;
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
import java.util.*;
import javax.annotation.Nonnull;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.api.misc.IAgriDisplayable;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.reference.AgriNBT;

public class TileEntityCrop extends TileEntityBase implements IAgriCrop, IDebuggable, IAgriDisplayable {

    public static final Class[] ITEM_EXCLUDES = new Class[]{
        IAgriRakeItem.class,
        IAgriClipperItem.class,
        IAgriTrowelItem.class,
        ItemDebugger.class
    };

    private int growthStage;
    private boolean crossCrop = false;

    // Just in case...
    private IAgriStat stats;
    private IAgriPlant plant;
    private IAdditionalCropData data;

    // =========================================================================
    // Crop in world logic
    // =========================================================================
    public void growthTick() {
        if (!this.isRemote()) {
            if (this.plant != null && this.stats != null) {
                if (this.isMature()) {
                    this.spread();
                } else if (this.stats.getGrowth() * this.plant.getGrowthBonus() * AgriCraftConfig.growthMultiplier > this.getRandom().nextDouble() && this.isFertile()) {
                    this.applyGrowthTick();
                }
            } else if (this.isCrossCrop() && AgriCraftConfig.crossOverChance > this.getRandom().nextDouble()) {
                this.crossOver();
            } else {
                this.spawn();
            }
        }
    }

    public boolean plantSeed(EntityPlayer player, ItemStack stack) {
        boolean success = false;
        if (!this.isRemote()) {
            //is the cropEmpty a cross-crop or does it already have a plant
            if (!this.isCrossCrop() && !this.hasPlant()) {
                //the SEED can be planted here
                Optional<AgriSeed> seed = SeedRegistry.getInstance().valueOf(stack);
                success = seed.isPresent()
                        && seed.get().getPlant().getGrowthRequirement().isMet(this.getWorld(), pos)
                        && this.setSeed(seed.get());
            }
        }
        if (success && !player.isCreative()) {
            stack.stackSize--;
        }
        return success;
    }

    public boolean onCropRightClicked(EntityPlayer player, ItemStack heldItem) {
        //only make things happen serverside
        if (!this.isRemote()) {
            if (AgriItems.getInstance().HAND_RAKE.isEnabled() && heldItem == null && this.canWeed()) {
                //if weeds can only be removed by using a hand rake, nothing should happen
                return false;
            } else if (player.isSneaking() || heldItem == null) {
                this.harvest(player);
            } else if (TypeHelper.isAnyType(heldItem.getItem(), ITEM_EXCLUDES)) {
                // Allow the excludes to do their things.
                return false;
            } else if (FertilizerRegistry.getInstance().hasAdapter(heldItem)) {
                Optional<IAgriFertilizer> fert = FertilizerRegistry.getInstance().valueOf(heldItem);
                return fert.isPresent() && fert.get().applyFertilizer(player, this.getWorld(), this.getPos(), this, heldItem, this.getRandom());
            } else if (plantSeed(player, heldItem)) {
                return true;
            } //check to see if the player clicked with crops (crosscrop attempt)
            else if (heldItem.getItem() == AgriItems.getInstance().CROPS) {
                if (!player.isCreative()) {
                    heldItem.stackSize--;
                }
                this.setCrossCrop(true);
            } else {
                //harvest operation
                this.harvest(player);
            }
        }
        //Returning true will prevent other things from happening
        return true;
    }

    public void onCropLeftClicked(EntityPlayer player) {
        if (!this.isRemote()) {
            if (!player.capabilities.isCreativeMode) {
                //drop items if the player is not in creative
                this.spawnAsEntity(this.getDrops());
            }
            if (this.hasPlant()) {
                this.getPlant().ifPresent(p -> p.onRemove(this.getWorld(), pos));
            }
            this.getWorld().removeTileEntity(pos);
            this.getWorld().setBlockToAir(pos);
        }
    }

    public List<ItemStack> getDrops() {
        ArrayList<ItemStack> items = new ArrayList<>();
        if (this.isCrossCrop()) {
            items.add(new ItemStack(AgriItems.getInstance().CROPS, 2));
        } else {
            items.add(new ItemStack(AgriItems.getInstance().CROPS, 1));
        }
        if (this.hasSeed()) {
            this.getSeed().ifPresent(seed -> items.add(seed.toStack()));
            if (this.isMature()) {
                items.addAll(this.getFruits());
            }
        }
        return items;
    }

    public void applyBoneMeal() {
        if (!this.isRemote()) {
            if (this.hasPlant() || this.canWeed()) {
                this.setGrowthStage(this.growthStage + 2 + this.getRandom().nextInt(3));
            } else if (this.isCrossCrop() && AgriCraftConfig.fertilizerMutation) {
                this.crossOver();
            }
        }
    }

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
        if (!this.isRemote() && status != this.crossCrop) {
            this.crossCrop = status;
            SoundType type = Blocks.PLANKS.getSoundType(null, null, null, null);
            worldObj.playSound(null, (double) ((float) xCoord() + 0.5F), (double) ((float) yCoord() + 0.5F), (double) ((float) zCoord() + 0.5F), type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
            this.markForUpdate();
        }
    }

    @Override
    public int getGrowthStage() {
        return this.growthStage;
    }

    @Override
    public void setGrowthStage(int stage) {
        if (!this.isRemote() && this.hasPlant() && this.stats != null) {
            if (stage < 0) {
                stage = 0;
            } else if (stage >= this.plant.getGrowthStages()) {
                stage = this.plant.getGrowthStages() - 1;
            }
            if (stage != this.growthStage) {
                this.growthStage = stage;
                this.markForUpdate();
            }
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
            plant.onPlanted(worldObj, pos);
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
            oldPlant.onRemove(worldObj, pos);
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
        if (!this.isRemote()) {
            this.stats = stat;
            return stat != null;
        }
        return false;
    }

    @Override
    public Optional<IAgriStat> removeStat() {
        if (!this.isRemote()) {
            final IAgriStat old = this.stats;
            this.stats = new PlantStats();
            this.markForUpdate();
            return Optional.ofNullable(old);
        }
        return Optional.empty();
    }

    @Override
    public boolean isFertile(IAgriPlant plant) {
        return worldObj.isAirBlock(this.getPos().add(0, 1, 0))
                && plant.getGrowthRequirement().isMet(this.worldObj, pos);
    }

    @SideOnly(Side.CLIENT)
    public float getCropHeight() {
        return hasPlant() ? plant.getHeight(getBlockMetadata()) : Constants.UNIT * 13;
    }

    @Override
    public boolean isMature() {
        return getPlant().map(plant -> this.growthStage + 1 >= plant.getGrowthStages()).orElse(false);
    }

    @Override
    public Optional<IAgriSoil> getSoil() {
        return SoilRegistry.getInstance().getSoil(this.worldObj.getBlockState(this.pos.add(0, -1, 0)));
    }

    // =========================================================================
    // IWeedable Methods
    // =========================================================================
    @Override
    public boolean spawn() {
        if (!hasPlant()) {
            for (IAgriPlant p : PlantRegistry.getInstance().getPlants()) {
                if (p.getSpawnChance() > this.getRandom().nextDouble() && this.isFertile(p)) {
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
    public boolean spread() {
        if (this.hasPlant() && this.plant.getSpreadChance() > this.getRandom().nextDouble()) {
            for (IAgriCrop crop : this.getNeighbours()) {
                if (!this.getPlant().equals(crop.getPlant())) {
                    if (!crop.hasPlant() && !crop.isCrossCrop()) {
                        crop.setPlant(plant);
                        return true;
                    } else if (this.plant.isAggressive() && crop.getStat().map(s -> s.getStrength()).orElse((byte) 0) > this.getStat().map(s -> s.getStrength()).orElse((byte) 0) * this.getRandom().nextDouble()) {
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
        if (this.plant != null && this.plant.isWeed()) {
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
            return AgriCraftConfig.fertilizerMutation && fertilizer.canTriggerMutation();
        }
        return this.hasPlant() && this.plant.isFertilizable() && fertilizer.isFertilizerAllowed(plant.getTier());
    }

    @Override
    public boolean onApplyFertilizer(IAgriFertilizer fertilizer, Random rand) {
        if (this.hasPlant() && this.plant.isFertilizable() && this.getGrowthStage() < Constants.MATURE) {
            ((BlockCrop) AgriBlocks.getInstance().CROP).grow(getWorld(), rand, getPos(), getWorld().getBlockState(getPos()));
            return true;
        } else if (this.isCrossCrop() && AgriCraftConfig.fertilizerMutation && fertilizer.canTriggerMutation()) {
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
        if (!this.isRemote()) {
            if (this.canWeed()) {
                this.clearWeed();
                return false;
            } else if (this.isCrossCrop()) {
                this.setCrossCrop(false);
                this.spawnAsEntity(new ItemStack(AgriItems.getInstance().CROPS, 1));
                return false;
            } else if (this.canHarvest()) {
                this.setGrowthStage(2);
                List<ItemStack> drops = this.getFruits();
                drops.forEach(this::spawnAsEntity);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ItemStack> getFruits() {
        return this.getStat()
                .map(stat
                        -> this.getPlant()
                        .map(plant -> plant.getFruitsOnHarvest(stat, this.getRandom()))
                        .orElse(Collections.emptyList()))
                .orElse(Collections.emptyList());
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
            tag.setInteger(AgriNBT.META, growthStage);
        }
        tag.setBoolean(AgriNBT.CROSS_CROP, crossCrop);
        if (plant != null) {
            tag.setString(AgriNBT.SEED, plant.getId());
        }
        if (getAdditionalCropData() != null) {
            tag.setTag(AgriNBT.INVENTORY, getAdditionalCropData().writeToNBT());
        }
        //AgriCore.getLogger("Plant-Tag").debug("Write Tag: {0}", tag);
    }

    @Override
    public void readTileNBT(NBTTagCompound tag) {
        this.stats = StatRegistry.getInstance().valueOf(tag).orElse(null);
        if (tag.hasKey(AgriNBT.META)) {
            this.growthStage = tag.getInteger(AgriNBT.META);
        }
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
        if (hasPlant() && growthStage < plant.getGrowthStages()) {
            plant.onAllowedGrowthTick(worldObj, pos, this, meta);
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
        return WorldHelper.getTileNeighbors(worldObj, pos, IAgriCrop.class);
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
            if (this.plant.isWeed()) {
                list.add(" - This crop has weeds");
            } else {
                list.add(" - This crop has a plant");
            }
            Optional<IAgriStat> stats = this.getStat();
            list.add(" - Plant: " + this.plant.getPlantName());
            list.add(" - Id: " + this.plant.getId());
            list.add(" - Tier: " + plant.getTier());
            list.add(" - Stage: " + this.getGrowthStage());
            list.add(" - Stages: " + this.plant.getGrowthStages());
            list.add(" - Meta: " + this.getGrowthStage());
            list.add(" - Growth: " + stats.map(IAgriStat::getGrowth).orElse((byte) 1));
            list.add(" - Gain: " + stats.map(IAgriStat::getGain).orElse((byte) 1));
            list.add(" - Strength: " + stats.map(IAgriStat::getStrength).orElse((byte) 1));
            list.add(" - Fertile: " + this.isFertile());
            list.add(" - Mature: " + this.isMature());
            list.add(" - AgriSoil: " + this.plant.getGrowthRequirement().getSoils().stream()
                    .findFirst().map(IAgriSoil::getId).orElse("None")
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
    public void addDisplayInfo(List<String> information) {

        // Add Soil Information
        information.add("Soil: " + this.getSoil().map(IAgriSoil::getName).orElse("Unknown"));

        if (this.hasPlant()) {
            //Add the SEED name.
            information.add(AgriCore.getTranslator().translate("agricraft_tooltip.seed") + ": " + this.plant.getSeedName());
            //Add the GROWTH.
            if (this.isMature()) {
                information.add(AgriCore.getTranslator().translate("agricraft_tooltip.growthStage") + ": " + AgriCore.getTranslator().translate("agricraft_tooltip.mature"));
            } else {
                information.add(AgriCore.getTranslator().translate("agricraft_tooltip.growthStage") + ": " + ((int) 100 * (this.getGrowthStage() + 1.0) / this.plant.getGrowthStages()) + "%");
            }
            //Add the ANALYZED data.
            this.getStat().map(stat -> {
                if (stat.isAnalyzed()) {
                    stat.addStats(information);
                } else {
                    AgriCore.getTranslator().translate("agricraft_tooltip.analyzed");
                }
                return false;
            }).orElse(information.add(AgriCore.getTranslator().translate("agricraft_tooltip.analyzed")));
            //Add the fertility information.
            information.add(AgriCore.getTranslator().translate(this.isFertile() ? "agricraft_tooltip.fertile" : "agricraft_tooltip.notFertile"));
        } else if (this.canWeed()) {
            information.add(AgriCore.getTranslator().translate("agricraft_tooltip.weeds"));
        } else {
            information.add(AgriCore.getTranslator().translate("agricraft_tooltip.empty"));
        }

    }
}
