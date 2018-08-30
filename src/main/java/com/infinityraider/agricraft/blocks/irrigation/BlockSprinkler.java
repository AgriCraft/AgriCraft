package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.api.v1.misc.IAgriConnectable;
import com.infinityraider.agricraft.items.blocks.ItemBlockAgricraft;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.blocks.RenderSprinkler;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tiles.irrigation.TileEntitySprinkler;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSprinkler extends BlockTileCustomRenderedBase<TileEntitySprinkler> {

    public static final AxisAlignedBB BOUNDS_SPRINKLER = new AxisAlignedBB(
            Constants.UNIT * Constants.QUARTER,
            Constants.UNIT * Constants.HALF,
            Constants.UNIT * Constants.QUARTER,
            Constants.UNIT * Constants.THREE_QUARTER,
            Constants.UNIT * (Constants.WHOLE + Constants.QUARTER),
            Constants.UNIT * Constants.THREE_QUARTER
    );

    private final ItemBlockAgricraft itemBlock;

    public BlockSprinkler() {
        super("sprinkler", Material.IRON);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setHarvestLevel("axe", 0);
        this.itemBlock = new ItemBlockAgricraft(this);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // Call supermethod.
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        // Update neighbor.
        WorldHelper.getTile(world, pos.up(), IAgriConnectable.class)
                .ifPresent(IAgriConnectable::refreshConnections);
    }

    @Override
    public TileEntitySprinkler createNewTileEntity(World world, int meta) {
        return new TileEntitySprinkler();
    }

    @Override
    public final IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Optional<TileEntityChannel> tile = WorldHelper.getTile(world, pos.up(), TileEntityChannel.class);
        return ((IExtendedBlockState) state).withProperty(AgriProperties.CUSTOM_WOOD_TYPE,
                tile.map(TileEntityCustomWood::getMaterial).orElse(CustomWoodTypeRegistry.DEFAULT));
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return false;
    }

    //prevent block from being removed by leaves
    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if ((!world.isRemote) && (!player.isSneaking())) {
            if (!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world, pos, state, 0);
            }
            world.setBlockToAir(pos);
            world.removeTileEntity(pos);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float f, int i) {
        if (!world.isRemote) {
            ItemStack drop = new ItemStack(this, 1);
            spawnAsEntity(world, pos, drop);
        }
    }

    @Override
    public void observedNeighborChange(IBlockState state, World world, BlockPos pos, Block changedBlock, BlockPos changedBlockPos) {
        if (!this.canBlockStay(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.removeTileEntity(pos);
            world.setBlockToAir(pos);
        }
    }

    //see if the block can stay
    public boolean canBlockStay(World world, BlockPos pos) {
        return WorldHelper.getTile(world, pos, TileEntitySprinkler.class)
                .filter(TileEntitySprinkler::isConnected)
                .isPresent();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getBlockModelResourceLocation() {
        return new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + getInternalName());
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockWaterChannel && state.getBlock().getMaterial(state) == Material.AIR;
    }

    @Override
    public Optional<ItemBlockAgricraft> getItemBlock() {
        return Optional.of(this.itemBlock);
    }

    @Override
    public boolean isEnabled() {
        return AgriCraftConfig.enableIrrigation;
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[0];
    }

    @Override
    public final IUnlistedProperty[] getUnlistedPropertyArray() {
        return new IUnlistedProperty[]{AgriProperties.CUSTOM_WOOD_TYPE};
    }

    /*
     * Adds all intersecting collision boxes to a list. (Be sure to only add
     * boxes to the list if they intersect the mask.) Parameters: World, pos,
     * mask, list, colliding entity
     */
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, @Nullable Entity entity, boolean isActualState) {
        // Add central box.
        Block.addCollisionBoxToList(pos, mask, list, BOUNDS_SPRINKLER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BOUNDS_SPRINKLER;
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
    @SideOnly(Side.CLIENT)
    public RenderSprinkler getRenderer() {
        return new RenderSprinkler(this);
    }
    
}
