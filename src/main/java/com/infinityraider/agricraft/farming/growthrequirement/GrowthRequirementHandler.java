package com.infinityraider.agricraft.farming.growthrequirement;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriStack;
import com.infinityraider.agricraft.api.AgriApi;
import com.infinityraider.agricraft.api.requirement.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Holds all the default soils and soil. Also holds all GrowthRequirements.
 */
public class GrowthRequirementHandler {

    public static IGrowthReqBuilder getNewBuilder() {
        return new GrowthReqBuilder();
    }

    /**
     * This list contains soils which pose as a default soil, meaning any
     * CropPlant which doesn't require a specific soil will be able to grown on
     * these This list can be modified with MineTweaker
     */
    public static List<FuzzyStack> defaultSoils = new ArrayList<>();

    //Methods for fertile soils
    //-------------------------
    public static boolean isSoilValid(IBlockAccess world, BlockPos pos) {
        return FuzzyStack.fromBlockState(world.getBlockState(pos))
                .filter(soil -> AgriApi.SoilRegistry().get().isSoil(soil) || defaultSoils.contains(soil))
                .isPresent();
    }

    public static void init() {
        registerSoils();
        registerCustomEntries();
    }

    private static void registerSoils() {
        addDefaultSoil(new FuzzyStack(new ItemStack(Blocks.FARMLAND)));
    }

    private static void registerCustomEntries() {
        //reads custom entries
        AgriCore.getLogger("agricraft").info("Registering soils to whitelist:");
        // TODO Decide if to replace!
        String[] data = new String[]{"minecraft:dirt"};
        String total = " of " + data.length + ".";
        for (String line : data) {
            // TODO: THIS IS REALLY BAD. POSSIBLE SOURCE OF CRASHES.
            AgriCore.getLogger("agricraft").debug("  Parsing " + line + total);
            ItemStack stack = ((FuzzyStack) AgriStack.fromString(line).toStack()).toStack();
            Block block = (stack != null && stack.getItem() instanceof ItemBlock) ? ((ItemBlock) stack.getItem()).block : null;

            if (block != null) {
                addDefaultSoil(new FuzzyStack(new ItemStack(block, stack.getItemDamage())));
            } else {
                AgriCore.getLogger("agricraft").info(" Error when adding block to soil whitelist: Invalid block (line: " + line + ")");
            }
        }
        AgriCore.getLogger("agricraft").info("Completed soil whitelist:");
    }

    public static void addAllToSoilWhitelist(Collection<? extends FuzzyStack> list) {
        for (FuzzyStack block : list) {
            addDefaultSoil(block);
        }
    }

    public static void removeAllFromSoilWhitelist(Collection<? extends FuzzyStack> list) {
        defaultSoils.removeAll(list);
    }

    public static boolean addDefaultSoil(FuzzyStack block) {
        if (!defaultSoils.contains(block)) {
            defaultSoils.add(block);
            return true;
        }
        return false;
    }

}
