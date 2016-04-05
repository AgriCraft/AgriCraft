package com.infinityraider.agricraft.compatibility;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModHelper {
	
    private final String modId;
    private final boolean enabled;

    protected ModHelper(String modId) {
        this.modId = modId;
        this.enabled = ConfigurationHandler.enableModCompatibility(modId);
    }

    /**
     * @return The mod id of the mod for this helper
     */
    protected final String getModId() {
        return modId;
    }

    /**
     * @return if compatibility is enabled
     */
    protected final boolean isEnabled() {
        return enabled;
    }

    /**
     * @return a list containing all CropPlant objects for this mod
     */
    protected List<CropPlant> getCropPlants() {
        return new ArrayList<>();
    }

    /**
     * @return a list containing all items from this mod which have custom behaviour when used on a crop
     */
    protected List<Item> getCropTools(){
        return new ArrayList<>();
    }

    /**
     * Called when a tool from this mod with custom behaviour is used on a crop
     * @param world the world object
     * @param pos the position in the world
     * @param block the Block implementation of the crop
     * @param crop the TileEntity implementation of the crop
     * @param player the player using the tool (can be null)
     * @param stack the ItemStack holding the item
     * @return true to consume the right click
     */
    protected boolean handleRightClick(World world, BlockPos pos, BlockCrop block, TileEntityCrop crop, EntityPlayer player, ItemStack stack) {
        return false;
    }

    /**
     * @return true if this helper should have growth tick methods called
     */
    protected boolean handleGrowthTick() {
        return false;
    }

    /**
     * Called after a growth tick has been applied
     * @param world the world object
     * @param pos the position in the world
     * @param state the block state
     */
    protected void announceGrowthTick(World world, BlockPos pos, IBlockState state) {}

    /**
     * Is called when a crop is about to grow
     * @param world the world object
     * @param pos the position in the world
     * @param block the Block implementation of the crop
     * @param crop the TileEntity implementation of the crop
     * @param rnd a Random object
     * @return true to allow the growth tick, or false to prevent it
     */
    protected boolean allowGrowthTick(World world, BlockPos pos, BlockCrop block, TileEntityCrop crop, Random rnd) {
        return true;
    }

    /**
     * Called during ForgeModLoader's pre-init phase
     */
    protected void preInit() {}

    /**
     * Called during ForgeModLoader's init phase
     */
    protected void init() {}

    /**
     * Called during ForgeModLoader's post-init phase
     */
    protected void postInit() {}
}
