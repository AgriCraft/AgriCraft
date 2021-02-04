package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.InfinityItemProperty;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
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

    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        World world = context.getWorld();
        if(world.isRemote()) {
            return ActionResultType.PASS;
        }
        BlockPos pos = context.getPos();
        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        return AgriApi.getCrop(world, pos)
                .map(crop -> {
                    if (crop.hasWeeds()) {
                        // send message
                        if(player != null) {
                            player.sendMessage(AgriToolTips.MSG_TROWEL_WEED, Util.DUMMY_UUID);
                        }
                    } else {
                        if (!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Pre(crop, stack, player))) {
                            if (crop.hasPlant()) {
                                if (this.hasPlant(stack)) {
                                    // send message
                                    if (player != null) {
                                        player.sendMessage(AgriToolTips.MSG_TROWEL_PLANT, Util.DUMMY_UUID);
                                    }
                                } else {
                                    crop.getGenome().ifPresent(genome -> {
                                        this.setPlant(stack, genome, crop.getGrowthStage());
                                        crop.removeSeed();
                                    });
                                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Post(crop, stack, player));
                                    return ActionResultType.SUCCESS;
                                }
                            } else {
                                if (this.hasPlant(stack)) {
                                    this.getGenome(stack).ifPresent(genome -> {
                                        this.getGrowthStage(stack).ifPresent(growth -> {
                                            this.removePlant(stack);
                                            crop.setGenome(genome);
                                            crop.setGrowthStage(growth);
                                        });
                                    });
                                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Trowel.Post(crop, stack, player));
                                    return ActionResultType.SUCCESS;
                                } else {
                                    // send message
                                    if (player != null) {
                                        player.sendMessage(AgriToolTips.MSG_TROWEL_NO_PLANT, Util.DUMMY_UUID);
                                    }
                                }
                            }
                        }
                    }
                    return ActionResultType.FAIL;
                }).orElse(ActionResultType.FAIL);
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
