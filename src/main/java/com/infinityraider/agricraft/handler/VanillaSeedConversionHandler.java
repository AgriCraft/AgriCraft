package com.infinityraider.agricraft.handler;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Set;

public class VanillaSeedConversionHandler {
    private static final VanillaSeedConversionHandler INSTANCE = new VanillaSeedConversionHandler();

    public static VanillaSeedConversionHandler getInstance() {
        return INSTANCE;
    }

    private final Set<Item> exceptions;

    private VanillaSeedConversionHandler() {
        this.exceptions = Sets.newConcurrentHashSet();
    }

    public void registerException(Item item) {
        this.exceptions.add(item);
    }

    public boolean isException(ItemStack stack) {
        return this.exceptions.contains(stack.getItem());
    }

    public ItemStack attemptConvert(ItemStack stack) {
        // the seed already is an agricraft seed
        if(stack.getItem() instanceof IAgriSeedItem) {
            return stack;
        }
        // the seed is an exception and should not be converted
        if(this.isException(stack)) {
            return stack;
        }
        // try to convert the seed
        return AgriApi.getGenomeAdapterizer().valueOf(stack)
                .map(genome -> genome.toSeedStack(stack.getCount()))
                .orElse(stack);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void vanillaSeedConversion(PlayerInteractEvent.RightClickBlock event) {
        // Pre checks
        if(this.failsPreChecks(event.getWorld(), event.getPos(), event.getItemStack())) {
            return;
        }

        // If sneak clicking a seed analyzer, we need to check to convert the seed as well
        if(this.runSeedAnalyzerConversion(event.getWorld(), event.getPos(), event.getItemStack(), event.getEntityLiving(), event.getHand())) {
            return;
        }

        // Run planting conversion, and if successful, cancel the event
        if(this.runPlantingConversion(
                event.getWorld(), event.getFace() == null ? event.getPos() : event.getPos().offset(event.getFace()),
                event.getItemStack(), event.getPlayer(), event.getHand())
        ) {
            // Cancel the event
            event.setUseItem(Event.Result.DENY);
            event.setCanceled(true);
            // swing player arm
            event.getPlayer().swingArm(event.getHand());
        }
    }

    protected boolean failsPreChecks(World world, BlockPos pos, ItemStack stack) {
        // If overriding is disabled, don't bother.
        if (!AgriCraft.instance.getConfig().overrideVanillaFarming()) {
            return true;
        }
        // If the item is an exception, cancel
        if(this.isException(stack)) {
            return true;
        }
        // If clicking crop tile, the crop will handle the logic
        return AgriApi.getCrop(world, pos).isPresent();
    }

    protected boolean runSeedAnalyzerConversion(World world, BlockPos pos, ItemStack stack, LivingEntity entity, Hand hand) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileEntitySeedAnalyzer) {
            if(entity.isSneaking()) {
                // already an agricraft seed, seed analyzer will cover the logic
                if (stack.getItem() instanceof IAgriSeedItem) {
                    return true;
                }
                // attempt to convert the seed and let the seed analyzer handle the logic
                ItemStack converted = AgriApi.attemptConversionToAgriSeed(stack);
                if (converted.getItem() instanceof IAgriSeedItem) {
                    TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) tile;
                    if(!analyzer.hasSeed()) {
                        entity.setHeldItem(hand, converted);
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected boolean runPlantingConversion(World world, BlockPos pos, ItemStack stack, PlayerEntity player, Hand hand) {
        return !AgriCraft.instance.getConfig().convertSeedsOnlyInAnalyzer() && AgriApi.getGenomeAdapterizer().valueOf(stack).map(seed -> {
            // The player is attempting to plant a seed, convert it to an agricraft crop
            return AgriApi.getSoil(world, pos.down()).map(soil -> {
                // check if there are crop sticks above
                MutableBoolean consumed = new MutableBoolean(false);
                boolean cropSticks = AgriApi.getCrop(world, pos).map(crop -> {
                    if(!crop.hasPlant() && !crop.isCrossCrop() && crop.plantGenome(seed, player)) {
                        if (player == null || !player.isCreative()) {
                            stack.shrink(1);
                            consumed.setValue(true);
                        }
                        if(player != null) {
                            player.swingArm(hand);
                        }
                    }
                    return true;
                }).orElse(false);
                // if there were crop sticks, return the result of the crop sticks action
                if(cropSticks) {
                    return consumed.getValue();
                }
                // no crop sticks, try planting as a plant
                BlockState newState = AgriCraft.instance.getModBlockRegistry().crop_plant.getStateForPlacement(world, pos);
                if (newState != null && world.setBlockState(pos, newState, 11)) {
                    boolean planted = AgriApi.getCrop(world, pos).map(crop -> crop.plantGenome(seed, player)).orElse(false);
                    if (planted) {
                        // reduce stack size
                        if (player == null || !player.isCreative()) {
                            stack.shrink(1);
                        }
                        // return success
                        return true;
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
                return false;
            }).orElse(false);
        }).orElse(false);
    }
}
