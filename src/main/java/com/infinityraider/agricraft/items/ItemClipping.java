package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.ISeedStats;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

public class ItemClipping extends ItemBase {

	@SideOnly(Side.CLIENT)
	public static final class ItemData {

		private ItemData() {
		}

		public static final String BASE_LOCATION = "agricraftitem:agricraft/items/clipping$";

		public static final ModelResourceLocation DEFAULT_MODEL = new ModelResourceLocation(BASE_LOCATION + "agricraft/items/debugger$", "inventory");
		public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("agricraft", "clipping"), "inventory");
	}

	@SideOnly(Side.CLIENT)
	private Map<IAgriCraftPlant, ModelResourceLocation> textures;

	public ItemClipping() {
		super("clipping", false);
		this.setCreativeTab(null);
		if (FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT)) {
			this.initTextures();
		}
	}

	@SideOnly(Side.CLIENT)
	private void initTextures() {
		this.textures = new HashMap<>();
	}

	public final void addPlant(IAgriCraftPlant crop, String texture) {
		if (FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT)) {
			this.textures.put(crop, getModel(texture));
			this.textures.put(null, ItemData.DEFAULT_MODEL);
		}
	}

	@SideOnly(Side.CLIENT)
	public static ModelResourceLocation getModel(final String texture) {
		if (texture == null || texture.isEmpty()) {
			return ItemData.DEFAULT_MODEL;
		}

		return new ModelResourceLocation(ItemData.BASE_LOCATION + texture.replaceFirst("_stem[0-9]", "_stem").replaceAll(":", "/") + "~4~4~12~12$", "inventory");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerItemRenderer() {
		/*
		AgriCore.getLogger("AgriCraft").debug("Registering Clipping Renderers...");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, this::getModel);
		ModelBakery.registerItemVariants(this, textures.values().toArray(new ModelResourceLocation[textures.values().size()]));
		AgriCore.getLogger("AgriCraft").debug("Clipping Renderers Registered!");
		 */
	}

	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(ItemStack stack) {
		return textures.getOrDefault(toCrop(stack), ItemData.DEFAULT_MODEL);
	}

	@Override
	public boolean canItemEditBlocks() {
		return true;
	}

	//this is called when you right click with this item in hand
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (world.isRemote) {
			return EnumActionResult.PASS;
		}
		if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
			return EnumActionResult.PASS;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (!(block instanceof BlockCrop)) {
			return EnumActionResult.PASS;
		}
		TileEntity te = world.getTileEntity(pos);
		if (te == null || !(te instanceof TileEntityCrop)) {
			return EnumActionResult.SUCCESS;
		}
		TileEntityCrop crop = (TileEntityCrop) te;
		BlockCrop blockCrop = (BlockCrop) block;
		if (crop.isCrossCrop()) {
			blockCrop.harvest(world, pos, state, player, crop);
		}
		if (!crop.canPlant()) {
			return EnumActionResult.FAIL;
		}
		ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
		ISeedStats stats = PlantStats.getStatsFromStack(seed);
		if (stats == null) {
			return EnumActionResult.PASS;
		}
		if (world.rand.nextInt(10) <= stats.getStrength()) {
			blockCrop.plantSeed(seed, world, pos);
		}
		stack.stackSize = stack.stackSize - 1;
		return EnumActionResult.SUCCESS;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String text = I18n.translateToLocal("item.agricraft:clipping.name");
		IAgriCraftPlant plant = toCrop(stack);
		if (plant == null || plant.getAllFruits() == null || plant.getAllFruits().isEmpty()) {
			return text;
		}
		ItemStack fruit = plant.getAllFruits().get(0);
		return fruit.getDisplayName() + " " + text;
	}

	private static IAgriCraftPlant toCrop(ItemStack stack) {
		try {
			ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
			return CropPlantHandler.getPlantFromStack(seed);
		} catch (Exception e) {
			return null;
		}
	}

}
