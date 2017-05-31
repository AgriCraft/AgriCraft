package com.infinityraider.agricraft.items;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.crafting.RecipeCopyJournal;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
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
        list.add(AgriCore.getTranslator().translate("agricraft_tooltip.discoveredSeeds") + ": " + getDiscoveredSeedIds(stack).count());
    }

    private Stream<String> getDiscoveredSeedIds(@Nullable ItemStack journal) {
        return Optional.ofNullable(journal)
                .map(ItemStack::getTagCompound)
                .map(tag -> tag.getString(AgriNBT.DISCOVERED_SEEDS))
                .map(ids -> ids.split(";"))
                .map(Arrays::stream)
                .orElseGet(Stream::empty);
    }

    @Override
    public void addEntry(@Nullable ItemStack journal, @Nullable IAgriPlant plant) {
        if (journal != null && plant != null) {
            if (!isSeedDiscovered(journal, plant)) {
                NBTTagCompound tag = StackHelper.getTag(journal);
                String old = tag.getString(AgriNBT.DISCOVERED_SEEDS);
                tag.setString(AgriNBT.DISCOVERED_SEEDS, old + plant.getId() + ";");
                journal.setTagCompound(tag);
            }
        }
    }

    @Override
    public boolean isSeedDiscovered(@Nullable ItemStack journal, @Nullable IAgriPlant plant) {
        return (journal != null) && (plant != null) && getDiscoveredSeedIds(journal).anyMatch(plant.getId()::equals);
    }

    @Override
    public List<IAgriPlant> getDiscoveredSeeds(@Nullable ItemStack journal) {
        return Optional.ofNullable(journal)
                .map(this::getDiscoveredSeedIds)
                .orElseGet(Stream::empty)
                .map(AgriApi.getPlantRegistry()::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
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
