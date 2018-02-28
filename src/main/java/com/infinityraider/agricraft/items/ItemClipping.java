package com.infinityraider.agricraft.items;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.item.IAutoRenderedItem;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.render.item.ItemModelTexture;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Class representing clipping items.
 *
 * @author The AgriCraft Team
 */
public class ItemClipping extends ItemBase implements IAutoRenderedItem {

    //
    // The following line has been commented out, since apparently Forge ASM is
    // *special*, and cannot handle simple static variable declarations such as
    // this.
    //
    //@SideOnly(Side.CLIENT)
    public static final ResourceLocation DEFAULT_PLANT_ICON = new ResourceLocation("agricraft:items/debugger");

    public ItemClipping() {
        super("clipping");
        this.setCreativeTab(null);
    }

    //this is called when you right click with this item in hand
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        // If in creative or remote, skip.
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        // Get the item & seed.
        final ItemStack stack = player.getHeldItem(hand);
        final AgriSeed seed = AgriApi.getSeedRegistry().valueOf(stack).orElse(null);

        // If seed is missing, error and pass.
        if (seed == null) {
            AgriCore.getLogger("agricraft").info("Unable to resolve an ItemClipping to an instance of an AgriSeed!");
            return EnumActionResult.PASS;
        }

        // Look for a crop instance at the given location.
        final IAgriCrop crop = WorldHelper.getTile(world, pos, IAgriCrop.class).orElse(null);

        // If the crop is missing, does not accept the given seed, or is not fertile for the seed, pass.
        if (crop == null || !crop.acceptsSeed(seed) || !crop.isFertile(seed)) {
            return EnumActionResult.PASS;
        }

        // Destroy the seed if needed.
        if (world.rand.nextInt(10) > seed.getStat().getStrength()) {
            // Message the player as to explain.
            MessageUtil.messagePlayer(player, "`7The clipping did not take...`r");
            // Decrease the stack size.
            StackHelper.decreaseStackSize(player, stack, 1);
            // Return that the action was a success (or moreso a failure...).
            return EnumActionResult.FAIL;
        }

        // Return that nothing happened.
        return EnumActionResult.PASS;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String text = AgriCore.getTranslator().translate("item.agricraft:clipping.name");
        Optional<AgriSeed> seed = AgriApi.getSeedRegistry().valueOf(stack);
        // lol... This would have been a NPE, had it not been for the Optional class!
        return seed.map(s -> s.getPlant().getPlantName()).orElse("Generic") + " " + text;
    }

    public static ItemStack getClipping(AgriSeed seed, int amount) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(AgriNBT.SEED, seed.getPlant().getId());
        seed.getStat().writeToNBT(tag);
        ItemStack stack = new ItemStack(AgriItems.getInstance().AGRI_CLIPPING);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getModelId(ItemStack stack) {
        Optional<AgriSeed> seed = AgriApi.getSeedRegistry().valueOf(stack);
        return seed.map(s -> s.getPlant().getId()).orElse("");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getBaseTexture(ItemStack stack) {
        return "agricraft:items/clipping";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<ItemModelTexture> getOverlayTextures(ItemStack stack) {
        ResourceLocation tex = AgriApi.getSeedRegistry()
                .valueOf(stack)
                .map(s -> s.getPlant().getPrimaryPlantTexture(Constants.MATURE))
                .orElse(DEFAULT_PLANT_ICON);
        return TypeHelper.asList(
                new ItemModelTexture(tex, 4, 4, 12, 12, 0, 0, 16, 16)
        );
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<ResourceLocation> getAllTextures() {
        return TypeHelper.asList(
                new ResourceLocation("agricraft:items/clipping")
        );
    }

}
