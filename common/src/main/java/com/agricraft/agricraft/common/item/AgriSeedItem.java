package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.genetic.AgriGenePair;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class AgriSeedItem extends BlockItem {

	public AgriSeedItem(Properties properties) {
		super(ModBlocks.CROP.get(), properties);
	}

	/**
	 * Create an ItemStack with the default genome for a plant
	 *
	 * @param plant the plant to create to genome from
	 * @return an ItemStack with the default genome of the plant
	 */
	public static ItemStack toStack(AgriPlant plant) {
		ItemStack stack = new ItemStack(ModItems.SEED.get(), 1);
		AgriGenome genome = new AgriGenome(plant);
		CompoundTag tag = new CompoundTag();
		genome.writeToNBT(tag);
		stack.setTag(tag);
		return stack;
	}

	/**
	 * Create an ItemStack with the given genome
	 *
	 * @param genome the genome to create the ItemStack from
	 * @return an ItemStack with the given genome
	 */
	public static ItemStack toStack(AgriGenome genome) {
		ItemStack stack = new ItemStack(ModItems.SEED.get(), 1);
		CompoundTag tag = new CompoundTag();
		genome.writeToNBT(tag);
		stack.setTag(tag);
		return stack;
	}

	/**
	 * Compute the plant species from an ItemStack
	 *
	 * @param stack the itemstack to compute the species from
	 * @return the plant species formatted as a resource location, or {@code agricraft:unknown} if not found
	 */
	public static String getSpecies(ItemStack stack) {
		if (stack.getItem() != ModItems.SEED.get()) {
			return "agricraft:unknown";
		}
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return "agricraft:unknown";
		}
		AgriGenome genome = AgriGenome.fromNBT(tag);
		if (genome == null) {
			return "agricraft:unknown";
		}
		return genome.getSpeciesGene().getDominant().trait();
	}

	@Override
	public Component getName(ItemStack stack) {
		if (stack.getTag() == null) {
			return Component.translatable("seed.agricraft.agricraft.unknown");
		}
		AgriGenome genome = AgriGenome.fromNBT(stack.getTag());
		if (genome == null) {
			return Component.translatable("seed.agricraft.agricraft.unknown");
		}
		return Component.translatable("seed.agricraft." + genome.getSpeciesGene().getDominant().trait().replace(":", "."));
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		InteractionResult result = super.place(context);
		Level level = context.getLevel();
		if (result.consumesAction() && !level.isClientSide) {
			BlockEntity be = level.getBlockEntity(context.getClickedPos());
			if (be instanceof CropBlockEntity cbe) {
				CompoundTag tag = context.getItemInHand().getTag();
				if (tag != null) {
					cbe.setGenome(AgriGenome.fromNBT(tag));
				}
			}
		}
		return result;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			AgriGenome genome = AgriGenome.fromNBT(tag);
			if (genome != null) {
				if (isAdvanced.isAdvanced()) {
					genome.getSpeciesGene().getGene().addTooltip(tooltipComponents, genome.getSpeciesGene().getTrait());
				}
				genome.getStatGenes().stream()
						.sorted(Comparator.comparing(pair -> pair.getGene().getId()))
						.forEach(pair -> pair.getGene().addTooltip(tooltipComponents, pair.getTrait()));
			}
		}
	}

	@Override
	public Component getDescription() {
		return super.getDescription();
	}

}
