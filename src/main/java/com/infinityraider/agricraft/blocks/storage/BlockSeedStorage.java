package com.infinityraider.agricraft.blocks.storage;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.renderers.blocks.RenderSeedStorage;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorage;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSeedStorage extends BlockCustomWood<TileEntitySeedStorage> {

    public BlockSeedStorage() {
        super("seed_storage");
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[]{AgriProperties.FACING};
    }

    @Override
    public TileEntitySeedStorage createNewTileEntity(World world, int meta) {
        return new TileEntitySeedStorage();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.SEED_STORAGE_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        final List<ItemStack> items = super.getDrops(world, pos, state, fortune);
        WorldHelper
                .getTile(world, pos, TileEntitySeedStorage.class)
                .map(t -> (List<ItemStack>) t.getInventory())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(StackHelper::fitToMaxSize)
                .forEach(items::addAll);
        return items;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Optional<TileEntitySeedStorage> tile = WorldHelper.getTile(worldIn, pos, TileEntitySeedStorage.class);
        if (tile.isPresent()) {
            TileEntitySeedStorage storage = tile.get();
            state = AgriProperties.FACING.applyToBlockState(state, storage.getOrientation());
        }
        return state;
    }

    //render methods
    //--------------
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderSeedStorage getRenderer() {
        return new RenderSeedStorage(this);
    }

    @Override
    public boolean isEnabled() {
        return !AgriCraftConfig.disableSeedStorage;
    }

    /*if (!AgriCraftConfig.disableSeedStorage) {
			CustomWoodRecipeHelper.registerCustomWoodRecipe(AgriBlocks.getInstance().SEED_STORAGE, 1, true, "wiw", "wcw", "wcw", 'w', CustomWoodRecipeHelper.MATERIAL_PARAMETER, 'i', Items.IRON_INGOT, 'c', Blocks.CHEST);
		}*/
}
