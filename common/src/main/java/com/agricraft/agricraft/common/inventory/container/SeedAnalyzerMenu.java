package com.agricraft.agricraft.common.inventory.container;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.common.block.SeedAnalyzerBlock;
import com.agricraft.agricraft.common.block.entity.SeedAnalyzerBlockEntity;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.item.JournalItem;
import com.agricraft.agricraft.common.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class SeedAnalyzerMenu extends AbstractContainerMenu {

	private final BlockPos pos;
	private final SeedAnalyzerBlockEntity analyzer;

	public SeedAnalyzerMenu(int id, Inventory playerInventory, Player player, BlockPos pos) {
		super(ModMenus.SEED_ANALYZER_MENU.get(), id);
		this.pos = pos;
		this.analyzer = ((SeedAnalyzerBlockEntity) player.level().getBlockEntity(pos));
		this.addSlot(new Slot(this.analyzer.getInventory(), SeedAnalyzerBlockEntity.SEED_SLOT, 26, 38) {
			@Override
			public void set(ItemStack stack) {
				if (!(stack.getItem() instanceof AgriSeedItem)) {
					ItemStack finalStack = stack;  // lambda :(
					stack = AgriApi.getPlantRegistry().flatMap(registry -> registry.stream().filter(plant -> plant.isSeedItem(finalStack)).findFirst())
							.map(AgriSeedItem::toStack)
							.orElse(stack);
				}
				super.set(stack);
				if (analyzer.hasJournal() && !stack.isEmpty()) {
					ItemStack journal = analyzer.getJournal();
					JournalItem.researchPlant(journal, new ResourceLocation(AgriSeedItem.getSpecies(stack)));
				}
			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof AgriSeedItem || AgriApi.getPlantRegistry().map(registry -> registry.stream().anyMatch(plant-> plant.isSeedItem(stack))).orElse(false);
			}
		});
		this.addSlot(new Slot(this.analyzer.getInventory(), SeedAnalyzerBlockEntity.JOURNAL_SLOT, 26, 71) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof JournalItem;
			}

			@Override
			public void set(ItemStack stack) {
				BlockState state = analyzer.getBlockState().setValue(SeedAnalyzerBlock.JOURNAL, true);
				analyzer.getLevel().setBlock(pos, state, Block.UPDATE_ALL);
				super.set(stack);
			}

			@Override
			public void onTake(Player player, ItemStack stack) {
				BlockState state = analyzer.getBlockState().setValue(SeedAnalyzerBlock.JOURNAL, false);
				analyzer.getLevel().setBlock(pos, state, Block.UPDATE_ALL);
				super.onTake(player, stack);
			}
		});
		//layout player inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(playerInventory, 9 + 9 * y + x, 13 + 18 * x, 104 + 18 * y));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 13 + 18 * i, 162));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemStack = slotStack.copy();
			if (index <= 1) {// shift-click in seed/journal slot
				if (!this.moveItemStackTo(slotStack, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index <= 38) {// shift-click in inventory slots
				if (slotStack.getItem() instanceof AgriSeedItem) {
					if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotStack.getItem() instanceof JournalItem) {
					if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (slotStack.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, slotStack);
		}
		return itemStack;
	}

	public Optional<AgriGenome> getGenomeToRender() {
		ItemStack seed = analyzer.getSeed().copy();
		if (seed.isEmpty()) {
			return Optional.empty();
		}
		AgriGenome genome = AgriGenome.fromNBT(seed.getTag());
		return Optional.ofNullable(genome);
	}

	@Override
	public boolean stillValid(Player player) {
		return player.distanceToSqr(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) < 64.0F;
	}

}
