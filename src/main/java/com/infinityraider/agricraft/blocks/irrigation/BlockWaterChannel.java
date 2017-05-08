package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.api.util.FuzzyStack;
import com.infinityraider.agricraft.crafting.CustomWoodRecipeHelper;
import com.infinityraider.agricraft.crafting.CustomWoodShapedRecipe;
import com.infinityraider.agricraft.crafting.FullRecipeLayout;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderChannel;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import com.infinityraider.infinitylib.utility.RegisterHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockWaterChannel extends AbstractBlockWaterChannel<TileEntityChannel> implements IRecipeRegister {

    protected static final float MIN = Constants.UNIT * Constants.QUARTER;
    protected static final float MAX = Constants.UNIT * Constants.THREE_QUARTER;

    public BlockWaterChannel() {
        super("normal");
    }

    @Override
    public TileEntityChannel createNewTileEntity(World world, int meta) {
        return new TileEntityChannel();
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add
     * boxes to the list if they intersect the mask.) Parameters: World, pos,
     * mask, list, colliding entity
     */
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
        //central box
        AxisAlignedBB box = new AxisAlignedBB(MIN, MIN, MIN, MAX, MAX, MAX);
        addCollisionBoxToList(pos, mask, list, box);

        //adjacent boxes
        if (AgriProperties.CHANNEL_NORTH.getValue(state)) {
            box = new AxisAlignedBB(MIN, MIN, 0, MAX, MAX, MIN + Constants.UNIT);
            addCollisionBoxToList(pos, mask, list, box);
        }
        if (AgriProperties.CHANNEL_EAST.getValue(state)) {
            box = new AxisAlignedBB(MAX - Constants.UNIT, MIN, MIN, Constants.UNIT * Constants.WHOLE, MAX, MAX);
            addCollisionBoxToList(pos, mask, list, box);
        }
        if (AgriProperties.CHANNEL_SOUTH.getValue(state)) {
            box = new AxisAlignedBB(MIN, MIN, MAX - Constants.UNIT, MAX, MAX, Constants.UNIT * Constants.WHOLE);
            addCollisionBoxToList(pos, mask, list, box);
        }
        if (AgriProperties.CHANNEL_WEST.getValue(state)) {
            box = new AxisAlignedBB(0, MIN, MIN, MIN + Constants.UNIT, MAX, MAX);
            addCollisionBoxToList(pos, mask, list, box);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        final AxisAlignedBB minBB = new AxisAlignedBB(MIN, MIN, MIN, MAX, MAX, MAX);
        if (AgriProperties.CHANNEL_NORTH.getValue(state)) {
            minBB.addCoord(minBB.minX, MIN, 0);
        }
        if (AgriProperties.CHANNEL_EAST.getValue(state)) {
            minBB.addCoord(1, MAX, minBB.maxZ);
        }
        if (AgriProperties.CHANNEL_SOUTH.getValue(state)) {
            minBB.addCoord(minBB.maxX, MAX, 1);
        }
        if (AgriProperties.CHANNEL_WEST.getValue(state)) {
            minBB.addCoord(0, MIN, minBB.minZ);
        }
        return minBB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        return getBoundingBox(state, world, pos).offset(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return getBoundingBox(state, world, pos).offset(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));    //wooden channel
        list.add(new ItemStack(item, 1, 1));    //iron pipe
    }

    //render methods
    //--------------
    @Override
    @SideOnly(Side.CLIENT)
    public RenderChannel getRenderer() {
        return new RenderChannel(this, new TileEntityChannel());
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
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
