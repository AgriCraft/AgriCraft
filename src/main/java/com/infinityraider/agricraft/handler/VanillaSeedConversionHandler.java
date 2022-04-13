package com.infinityraider.agricraft.handler;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
                event.getWorld(), event.getFace() == null ? event.getPos() : event.getPos().relative(event.getFace()),
                event.getItemStack(), event.getPlayer(), event.getHand())
        ) {
            // Cancel the event
            event.setUseItem(Event.Result.DENY);
            event.setCanceled(true);
            // swing player arm
            event.getPlayer().swing(event.getHand());
        }
    }

    protected boolean failsPreChecks(Level world, BlockPos pos, ItemStack stack) {
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

    protected boolean runSeedAnalyzerConversion(Level world, BlockPos pos, ItemStack stack, LivingEntity entity, InteractionHand hand) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileEntitySeedAnalyzer) {
            if(entity.isDiscrete()) {
                // already an agricraft seed, seed analyzer will cover the logic
                if (stack.getItem() instanceof IAgriSeedItem) {
                    return true;
                }
                // attempt to convert the seed and let the seed analyzer handle the logic
                ItemStack converted = AgriApi.attemptConversionToAgriSeed(stack);
                if (converted.getItem() instanceof IAgriSeedItem) {
                    TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) tile;
                    if(!analyzer.hasSeed()) {
                        entity.setItemInHand(hand, converted);
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected boolean runPlantingConversion(Level world, BlockPos pos, ItemStack stack, Player player, InteractionHand hand) {
        return !AgriCraft.instance.getConfig().convertSeedsOnlyInAnalyzer() && AgriApi.getGenomeAdapterizer().valueOf(stack).map(seed -> {
            // The player is attempting to plant a seed, convert it to an agricraft crop
            return AgriApi.getSoil(world, pos.below()).map(soil -> {
                // check if there are crop sticks above
                MutableBoolean consumed = new MutableBoolean(false);
                boolean cropSticks = AgriApi.getCrop(world, pos).map(crop -> {
                    if(!crop.hasPlant() && !crop.isCrossCrop() && crop.plantGenome(seed, player)) {
                        if (player == null || !player.isCreative()) {
                            stack.shrink(1);
                            consumed.setValue(true);
                        }
                        if(player != null) {
                            player.swing(hand);
                        }
                    }
                    return true;
                }).orElse(false);
                // if there were crop sticks, return the result of the crop sticks action
                if(cropSticks) {
                    return consumed.getValue();
                }
                // no crop sticks, try planting as a plant
                BlockState newState = AgriBlockRegistry.getInstance().crop_plant.get().getStateForPlacement(world, pos);
                if (newState != null && world.setBlock(pos, newState, 11)) {
                    boolean planted = AgriApi.getCrop(world, pos).map(crop -> crop.plantGenome(seed, player)).orElse(false);
                    if (planted) {
                        // reduce stack size
                        if (player == null || !player.isCreative()) {
                            stack.shrink(1);
                        }
                        // return success
                        return true;
                    } else {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
                return false;
            }).orElse(false);
        }).orElse(false);
    }
}
