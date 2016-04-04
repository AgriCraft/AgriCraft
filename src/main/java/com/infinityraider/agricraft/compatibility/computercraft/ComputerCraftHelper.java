package com.infinityraider.agricraft.compatibility.computercraft;

import com.infinityraider.agricraft.compatibility.ModHelper;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ComputerCraftHelper extends ModHelper {

	public ComputerCraftHelper() {
		super(AgriCraftMods.computerCraft);
	}

	@Override
	protected void init() {
		if (AgriCraftBlocks.blockPeripheral != null) {
			GameRegistry.addRecipe(
					new ShapedOreRecipe(
							new ItemStack(AgriCraftBlocks.blockPeripheral, 1),
							"iai",
							"rcr",
							"iri",
							'i', "ingotIron",
							'a', AgriCraftBlocks.blockSeedAnalyzer,
							'r', Items.comparator,
							'c', (Block) Block.blockRegistry.getObject(new ResourceLocation("ComputerCraft:CC-Computer"))
					)
			);
		}
	}

	@Override
	protected void postInit() {
		dan200.computercraft.api.ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) AgriCraftBlocks.blockPeripheral);
	}

}
