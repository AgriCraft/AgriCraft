package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.crafting.CustomWoodRecipeHelper;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.renderers.blocks.RenderChannelFull;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannelFull;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import java.util.Optional;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWaterChannelFull extends AbstractBlockWaterChannel<TileEntityChannelFull> implements IRecipeRegister {

    private final ItemBlockCustomWood itemBlock;
    
    public BlockWaterChannelFull() {
        super("full");
        this.itemBlock = new ItemBlockCustomWood(this);
    }
    
    @Override
    public Optional<ItemBlockCustomWood> getItemBlock() {
        return Optional.of(this.itemBlock);
    }

    @Override
    public TileEntityChannelFull createNewTileEntity(World world, int meta) {
        return new TileEntityChannelFull();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderChannelFull getRenderer() {
        return new RenderChannelFull(this);
    }

    @Override
    public void registerRecipes() {
        // Creation Recipe
        CustomWoodRecipeHelper.registerCustomWoodRecipe(AgriBlocks.getInstance().CHANNEL_FULL, 1, false, AgriBlocks.getInstance().CHANNEL, AgriBlocks.getInstance().CHANNEL, AgriBlocks.getInstance().CHANNEL, AgriBlocks.getInstance().CHANNEL);
        // Deconstruction Recipe
        CustomWoodRecipeHelper.registerCustomWoodRecipe(AgriBlocks.getInstance().CHANNEL, 4, false, AgriBlocks.getInstance().CHANNEL_FULL);
    }

}
