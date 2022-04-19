package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.content.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.BlockCrop;
import com.infinityraider.agricraft.content.core.TileEntityCrop;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.InfinityItemProperty;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.render.item.IClientItemProperties;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class ItemTrowel extends ItemBase implements IAgriTrowelItem {
    private static final IAgriPlant NO_PLANT = NoPlant.getInstance();

    public ItemTrowel() {
        super(Names.Items.TROWEL, new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
                .stacksTo(1)
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
        CompoundTag tag = null;
        if(stack.hasTag()) {
            tag = stack.getTag();
        }
        if(tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        CompoundTag genomeTag = new CompoundTag();
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
        CompoundTag tag = this.checkNBT(stack);
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
        CompoundTag tag = this.checkNBT(stack);
        if(tag == null) {
            return Optional.empty();
        }
        return AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.GROWTH));
    }

    @Nullable
    protected CompoundTag checkNBT(ItemStack stack) {
        if(!stack.hasTag()) {
            return null;
        }
        CompoundTag tag = stack.getTag();
        if(tag == null || (!tag.contains(AgriNBT.GENOME)) || (!tag.contains(AgriNBT.GROWTH))) {
            return null;
        }
        return tag;
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        return AgriApi.getCrop(world, pos)
                .map(crop -> this.tryUseOnCrop(crop, stack, player))
                .orElseGet(() -> this.tryPlantOnSoil(world, pos, stack, player));
    }

    protected InteractionResult tryUseOnCrop(IAgriCrop crop, ItemStack stack, @Nullable Player player) {
        if (crop.hasWeeds()) {
            // send message
            if (player != null) {
                player.sendMessage(AgriToolTips.MSG_TROWEL_WEED, player.getUUID());
            }
        } else if(crop.isCrossCrop()) {
            return InteractionResult.FAIL;
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
        return InteractionResult.FAIL;
    }

    protected InteractionResult tryPickUpPlant(IAgriCrop crop, ItemStack stack, @Nullable Player player) {
        if(crop.world() == null || crop.world().isClientSide()) {
            return InteractionResult.PASS;
        }
        if (this.hasPlant(stack)) {
            if (player != null) {
                // send message
                player.sendMessage(AgriToolTips.MSG_TROWEL_PLANT, player.getUUID());
            }
            return InteractionResult.FAIL;
        } else {
            crop.getGenome().ifPresent(genome -> {
                this.setPlant(stack, genome, crop.getGrowthStage());
                crop.removeGenome();
            });
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Post(crop, stack, player));
            return InteractionResult.SUCCESS;
        }
    }

    protected InteractionResult tryPlantOnCropSticks(IAgriCrop crop, ItemStack stack, @Nullable Player player) {
        if(crop.world() == null || crop.world().isClientSide()) {
            return InteractionResult.PASS;
        }
        if(crop.isCrossCrop()) {
            return InteractionResult.FAIL;
        }
        if (this.hasPlant(stack)) {
            if (crop.hasCropSticks()) {
                this.getGenome(stack).ifPresent(genome ->
                        this.getGrowthStage(stack).ifPresent(growth -> {
                            this.removePlant(stack);
                            crop.plantGenome(genome, player);
                            crop.setGrowthStage(growth);
                        }));
            }
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Post(crop, stack, player));
            return InteractionResult.SUCCESS;
        } else {
            if (player != null) {
                // send message
                player.sendMessage(AgriToolTips.MSG_TROWEL_NO_PLANT, player.getUUID());
            }
            return InteractionResult.FAIL;
        }
    }

    protected InteractionResult tryPlantOnSoil(Level world, BlockPos pos, ItemStack stack, @Nullable Player player) {
        if (this.hasPlant(stack)) {
            BlockPos up = pos.above();
            BlockEntity tile = world.getBlockEntity(up);
            if (tile instanceof TileEntityCrop) {
                return this.tryPlantOnCropSticks((TileEntityCrop) tile, stack, player);
            }
            return this.tryNewPlant(world, up, stack, player);
        }
        return InteractionResult.FAIL;
    }

    protected InteractionResult tryNewPlant(Level world, BlockPos pos, ItemStack stack, @Nullable Player player) {
        if (AgriCraft.instance.getConfig().allowPlantingOutsideCropSticks()) {
            BlockCrop cropBlock =  AgriBlockRegistry.getInstance().getCropBlock();
            BlockState newState = cropBlock.adaptStateForPlacement(cropBlock.blockStatePlant(), world, pos);
            if (newState != null && world.setBlock(pos, newState, 11)) {
                boolean success = AgriApi.getCrop(world, pos).map(crop -> {
                    if (MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Pre(crop, stack, player))) {
                        return false;
                    }
                    return this.getGenome(stack).map(genome ->
                            this.getGrowthStage(stack).map(stage -> {
                                boolean result = crop.plantGenome(genome, player) && this.setGrowthStage(crop, stage);
                                if (result) {
                                    this.removePlant(stack);
                                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Post(crop, stack, player));
                                }
                                return result;
                            }).orElse(false))
                            .orElse(false);
                }).orElse(false);
                if (success) {
                    return InteractionResult.SUCCESS;
                } else {
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        return InteractionResult.FAIL;
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
    public Supplier<IClientItemProperties> getClientItemProperties() {
        return () -> new IClientItemProperties() {
            @Nonnull
            @Override
            public Set<InfinityItemProperty> getModelProperties() {
                return ImmutableSet.of(new InfinityItemProperty(new ResourceLocation(AgriCraft.instance.getModId(), Names.Objects.PLANT)) {
                    @Override
                    public float getValue(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
                        return (!stack.isEmpty()
                                && stack.getItem() instanceof IAgriTrowelItem
                                && ((IAgriTrowelItem) stack.getItem()).hasPlant(stack))
                                ? 1
                                : 0;
                    }
                });
            }
        };
    }

}
