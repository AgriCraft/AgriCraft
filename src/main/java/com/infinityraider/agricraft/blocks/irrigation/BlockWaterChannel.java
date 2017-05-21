package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.crafting.CustomWoodRecipeHelper;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderChannel;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import com.infinityraider.infinitylib.utility.RegisterHelper;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockWaterChannel extends AbstractBlockWaterChannel<TileEntityChannel> implements IRecipeRegister {

    protected static final float MIN = Constants.UNIT * Constants.QUARTER;
    protected static final float MAX = Constants.UNIT * Constants.THREE_QUARTER;

    protected static final double EXPANSION = 1 / 64d;

    public static final AxisAlignedBB CENTER_BOX = new AxisAlignedBB(MIN, MIN, MIN, MAX, MAX, MAX).expandXyz(EXPANSION);
    public static final AxisAlignedBB NORTH_BOX = new AxisAlignedBB(MIN, MIN, 0, MAX, MAX, MIN + Constants.UNIT).expandXyz(EXPANSION);
    public static final AxisAlignedBB EAST_BOX = new AxisAlignedBB(MAX - Constants.UNIT, MIN, MIN, Constants.UNIT * Constants.WHOLE, MAX, MAX).expandXyz(EXPANSION);
    public static final AxisAlignedBB SOUTH_BOX = new AxisAlignedBB(MIN, MIN, MAX - Constants.UNIT, MAX, MAX, Constants.UNIT * Constants.WHOLE).expandXyz(EXPANSION);
    public static final AxisAlignedBB WEST_BOX = new AxisAlignedBB(0, MIN, MIN, MIN + Constants.UNIT, MAX, MAX).expandXyz(EXPANSION);

    public BlockWaterChannel() {
        super("normal");
    }

    @Override
    public TileEntityChannel createNewTileEntity(World world, int meta) {
        return new TileEntityChannel();
    }

    /*
     * Adds all intersecting collision boxes to a list. (Be sure to only add
     * boxes to the list if they intersect the mask.) Parameters: World, pos,
     * mask, list, colliding entity
     */
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
        // Fetch Tile Entity
        final Optional<TileEntityChannel> tile = WorldHelper.getTile(world, pos, TileEntityChannel.class);

        // Add central box.
        addCollisionBoxToList(pos, mask, list, CENTER_BOX);

        //adjacent boxes
        if (tile.isPresent()) {
            if (tile.get().hasNeighbor(EnumFacing.NORTH)) {
                Block.addCollisionBoxToList(pos, mask, list, NORTH_BOX);
            }
            if (tile.get().hasNeighbor(EnumFacing.EAST)) {
                Block.addCollisionBoxToList(pos, mask, list, EAST_BOX);
            }
            if (tile.get().hasNeighbor(EnumFacing.SOUTH)) {
                Block.addCollisionBoxToList(pos, mask, list, SOUTH_BOX);
            }
            if (tile.get().hasNeighbor(EnumFacing.WEST)) {
                Block.addCollisionBoxToList(pos, mask, list, WEST_BOX);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        // Fetch Tile Entity
        final Optional<TileEntityChannel> tile = WorldHelper.getTile(world, pos, TileEntityChannel.class);

        // Define Core Bounding Box
        AxisAlignedBB selection = CENTER_BOX;

        // Expand Bounding Box
        if (tile.isPresent()) {
            if (tile.get().hasNeighbor(EnumFacing.NORTH)) {
                selection = selection.union(NORTH_BOX);
            }
            if (tile.get().hasNeighbor(EnumFacing.EAST)) {
                selection = selection.union(EAST_BOX);
            }
            if (tile.get().hasNeighbor(EnumFacing.SOUTH)) {
                selection = selection.union(SOUTH_BOX);
            }
            if (tile.get().hasNeighbor(EnumFacing.WEST)) {
                selection = selection.union(WEST_BOX);
            }
        }

        return selection;
    }
    
    

    //render methods
    //--------------
    @Override
    @SideOnly(Side.CLIENT)
    public RenderChannel getRenderer() {
        return new RenderChannel(this, new TileEntityChannel());
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void registerRecipes() {
        // "Correct" wooden bowl recipe, so that may register channel recipe.
        RegisterHelper.removeRecipe(new ItemStack(Items.BOWL));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.BOWL, 4), "w w", " w ", 'w', "slabWood"));

        // Register channel recipe.
        CustomWoodRecipeHelper.registerCustomWoodRecipe(this, 6, true, "w w", " w ", 'w', CustomWoodRecipeHelper.MATERIAL_PARAMETER);
    }

}
