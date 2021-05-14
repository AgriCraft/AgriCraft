package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.TileEntityCropSticks;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.InfinityItemProperty;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class ItemTrowel extends ItemBase implements IAgriTrowelItem {
    private static final IAgriPlant NO_PLANT = NoPlant.getInstance();

    public ItemTrowel() {
        super(Names.Items.TROWEL, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    @Override
    public boolean hasPlant(ItemStack stack) {
        return this.getGenome(stack).isPresent();
    }

    @Override
    public boolean setPlant(ItemStack stack, IAgriGenome genome, IAgriGrowthStage stage) {
        if(this.hasPlant(stack)) {
            return false;
        }
        CompoundNBT tag = null;
        if(stack.hasTag()) {
            tag = stack.getTag();
        }
        if(tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        CompoundNBT genomeTag = new CompoundNBT();
        if(!genome.writeToNBT(genomeTag)) {
            return false;
        }
        tag.put(AgriNBT.GENOME, genomeTag);
        tag.putString(AgriNBT.GROWTH, stage.getId());
        return true;
    }

    @Override
    public boolean removePlant(ItemStack stack) {
        if(this.hasPlant(stack)) {
            stack.setTag(null);
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public Optional<IAgriGenome> getGenome(ItemStack stack) {
        CompoundNBT tag = this.checkNBT(stack);
        if(tag == null) {
            return Optional.empty();
        }
        IAgriGenome genome = AgriApi.getAgriGenomeBuilder(NO_PLANT).build();
        if(!genome.readFromNBT(tag.getCompound(AgriNBT.GENOME))) {
            return Optional.empty();
        }
        return genome.getPlant().isPlant() ? Optional.of(genome) : Optional.empty();
    }

    @Override
    public Optional<IAgriGrowthStage> getGrowthStage(ItemStack stack) {
        CompoundNBT tag = this.checkNBT(stack);
        if(tag == null) {
            return Optional.empty();
        }
        return AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.GROWTH));
    }

    @Nullable
    protected CompoundNBT checkNBT(ItemStack stack) {
        if(!stack.hasTag()) {
            return null;
        }
        CompoundNBT tag = stack.getTag();
        if(tag == null || (!tag.contains(AgriNBT.GENOME)) || (!tag.contains(AgriNBT.GROWTH))) {
            return null;
        }
        return tag;
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        return AgriApi.getCrop(world, pos)
                .map(crop -> this.tryUseOnCrop(crop, stack, player))
                .orElseGet(() -> this.tryPlantOnSoil(world, pos, stack, player));
    }

    protected ActionResultType tryUseOnCrop(IAgriCrop crop, ItemStack stack, @Nullable PlayerEntity player) {
        if (crop.hasWeeds()) {
            // send message
            if (player != null) {
                player.sendMessage(AgriToolTips.MSG_TROWEL_WEED, player.getUniqueID());
            }
        } else if(crop.isCrossCrop()) {
            return ActionResultType.FAIL;
        } else {
            if (!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Pre(crop, stack, player))) {
                if (crop.hasPlant()) {
                    // Try picking up a plant onto the trowel
                    return this.tryPickUpPlant(crop, stack, player);
                } else {
                    // Try planting a plant on crop sticks
                    return this.tryPlantOnCropSticks(crop, stack, player);
                }
            }
        }
        return ActionResultType.FAIL;
    }

    protected ActionResultType tryPickUpPlant(IAgriCrop crop, ItemStack stack, @Nullable PlayerEntity player) {
        if(crop.world() == null || crop.world().isRemote()) {
            return ActionResultType.PASS;
        }
        if (this.hasPlant(stack)) {
            if (player != null) {
                // send message
                player.sendMessage(AgriToolTips.MSG_TROWEL_PLANT, player.getUniqueID());
            }
            return ActionResultType.FAIL;
        } else {
            crop.getGenome().ifPresent(genome -> {
                this.setPlant(stack, genome, crop.getGrowthStage());
                crop.removeGenome();
            });
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Post(crop, stack, player));
            return ActionResultType.SUCCESS;
        }
    }

    protected ActionResultType tryPlantOnCropSticks(IAgriCrop crop, ItemStack stack, @Nullable PlayerEntity player) {
        if(crop.world() == null || crop.world().isRemote()) {
            return ActionResultType.PASS;
        }
        if(crop.isCrossCrop()) {
            return ActionResultType.FAIL;
        }
        if (this.hasPlant(stack)) {
            if (crop.hasCropSticks()) {
                this.getGenome(stack).ifPresent(genome ->
                        this.getGrowthStage(stack).ifPresent(growth -> {
                            this.removePlant(stack);
                            crop.spawnGenome(genome);
                            crop.setGrowthStage(growth);
                        }));
            }
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Post(crop, stack, player));
            return ActionResultType.SUCCESS;
        } else {
            if (player != null) {
                // send message
                player.sendMessage(AgriToolTips.MSG_TROWEL_NO_PLANT, player.getUniqueID());
            }
            return ActionResultType.FAIL;
        }
    }

    protected ActionResultType tryPlantOnSoil(World world, BlockPos pos, ItemStack stack, @Nullable PlayerEntity player) {
        if (this.hasPlant(stack)) {
            BlockPos up = pos.up();
            TileEntity tile = world.getTileEntity(up);
            if (tile instanceof TileEntityCropSticks) {
                return this.tryPlantOnCropSticks((TileEntityCropSticks) tile, stack, player);
            }
            return this.tryNewPlant(world, up, stack, player);
        }
        return ActionResultType.FAIL;
    }

    protected ActionResultType tryNewPlant(World world, BlockPos pos, ItemStack stack, @Nullable PlayerEntity player) {
        if (AgriCraft.instance.getConfig().allowPlantingOutsideCropSticks()) {
            BlockState newState = AgriCraft.instance.getModBlockRegistry().crop_plant.getStateForPlacement(world, pos);
            if (newState != null && world.setBlockState(pos, newState, 11)) {
                boolean success = AgriApi.getCrop(world, pos).map(crop -> {
                    if (MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Pre(crop, stack, player))) {
                        return false;
                    }
                    return this.getGenome(stack).map(genome ->
                            this.getGrowthStage(stack).map(stage -> {
                                boolean result = crop.spawnGenome(genome) && this.setGrowthStage(crop, stage);
                                if (result) {
                                    this.removePlant(stack);
                                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Post(crop, stack, player));
                                }
                                return result;
                            }).orElse(false))
                            .orElse(false);
                }).orElse(false);
                if (success) {
                    return ActionResultType.SUCCESS;
                } else {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
        return ActionResultType.FAIL;
    }

    protected boolean setGrowthStage(IAgriCrop crop, IAgriGrowthStage stage) {
        if(crop.getGrowthStage().equals(stage)) {
            return true;
        } else {
            return crop.setGrowthStage(stage);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Set<InfinityItemProperty> getModelProperties() {
        return ImmutableSet.of(new InfinityItemProperty(new ResourceLocation(AgriCraft.instance.getModId(), Names.Objects.PLANT)) {
            @Override
            public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
                return (!stack.isEmpty()
                        && stack.getItem() instanceof IAgriTrowelItem
                        && ((IAgriTrowelItem) stack.getItem()).hasPlant(stack))
                        ? 1
                        : 0;
            }
        });
    }

}
