package com.infinityraider.agricraft.items;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.AgriApi;
import com.infinityraider.agricraft.api.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.crafting.RecipeCopyJournal;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemJournal extends ItemBase implements IAgriJournalItem, IItemWithModel, IRecipeRegister {

    public ItemJournal() {
        super("journal");
        this.setMaxStackSize(1);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
    }

    //this has to return true to make it so the getContainerItem method gets called when this item is used in a recipe
    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    //when this item is used in a crafting recipe it is replaced by the item return by this method
    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.JOURNAL_GUI_ID, world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        list.add(AgriCore.getTranslator().translate("agricraft_tooltip.discoveredSeeds") + ": " + getDiscoveredSeedIds(stack).size());
    }

    private List<String> getDiscoveredSeedIds(ItemStack journal) {
        if (journal == null) {
            return new ArrayList<>();
        }
        //check if the journal has AgriCraftNBT and if it doesn't, create a new one
        if (!journal.hasTagCompound()) {
            journal.setTagCompound(new NBTTagCompound());
            return new ArrayList<>();
        }

        NBTTagCompound tag = journal.getTagCompound();
        String discovered = tag.getString(AgriNBT.DISCOVERED_SEEDS);
        if (discovered.isEmpty()) {
            return new ArrayList<>();
        } else {
            return Arrays.asList(discovered.split(";"));
        }
    }

    @Override
    public void addEntry(ItemStack journal, IAgriPlant plant) {
        if (journal != null && journal.getItem() != null && plant != null) {
            List<String> seeds = getDiscoveredSeedIds(journal);
            if (!seeds.contains(plant.getId())) {
                NBTTagCompound tag = journal.getTagCompound();
                String old = tag.getString(AgriNBT.DISCOVERED_SEEDS);
                tag.setString(AgriNBT.DISCOVERED_SEEDS, old + plant.getId() + ";");
                journal.setTagCompound(tag);
            }
        }
    }

    @Override
    public boolean isSeedDiscovered(ItemStack journal, IAgriPlant plant) {
        return journal != null && plant != null && getDiscoveredSeedIds(journal).contains(plant.getId());
    }

    @Override
    public List<IAgriPlant> getDiscoveredSeeds(ItemStack journal) {
        List<IAgriPlant> list = new ArrayList<>();
        if (journal != null && journal.hasTagCompound()) {
            for (String id : getDiscoveredSeedIds(journal)) {
                IAgriPlant plant = AgriApi.PlantRegistry().get().get(id);
                if (plant != null) {
                    list.add(plant);
                }
            }
        }
        return list;
    }

    @Override
    public void registerRecipes() {
        // Normal Crafting
        GameRegistry.addRecipe(new ShapedOreRecipe(this, "csc", "sbs", "csc", 'c', AgriItems.getInstance().CROPS, 's', Items.WHEAT_SEEDS, 'b', Items.BOOK));
        // Copy Crafting
        RecipeSorter.register("recipe.copy_journal", RecipeCopyJournal.class, RecipeSorter.Category.SHAPELESS, "");
        GameRegistry.addRecipe(new RecipeCopyJournal());
    }

}
