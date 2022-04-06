package com.infinityraider.agricraft.plugins.agrigui.analyzer;

import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrierItem;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.plugins.agrigui.GuiPlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SeedAnalyzerContainer extends AbstractContainerMenu {

	private final IItemHandler playerInventory;
	private final TileEntitySeedAnalyzer seedAnalyzer;
	private final BlockPos seedAnalyzerPos;

	private final ItemStackHandler seedHandler;
	private final ItemStackHandler journalHandler;

	public SeedAnalyzerContainer(int id, Level world, Inventory playerInventory, BlockPos seedAnalyzerPos) {
		super(GuiPlugin.SEED_ANALYZER_CONTAINER.get(), id);
		this.playerInventory = new InvWrapper(playerInventory);
		this.seedAnalyzerPos = seedAnalyzerPos;
		this.seedAnalyzer = (TileEntitySeedAnalyzer) world.getBlockEntity(seedAnalyzerPos);
		this.seedHandler = new ItemStackHandler(1) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return stack.getItem() instanceof IAgriSeedItem;
			}

			@Override
			protected void onContentsChanged(int slot) {
				if (!SeedAnalyzerContainer.this.journalHandler.getStackInSlot(0).isEmpty()) {
					ItemStack journalStack = SeedAnalyzerContainer.this.journalHandler.extractItem(0, 64, false);
					Optional<ItemStack> newJournal = TileEntitySeedAnalyzer.addSeedToJournal(this.getStackInSlot(slot).copy(), journalStack);
					if (newJournal.isPresent()) {
						SeedAnalyzerContainer.this.journalHandler.insertItem(0, newJournal.get(), false);
					} else {
						SeedAnalyzerContainer.this.journalHandler.insertItem(0, journalStack, false);
					}
				}
				super.onContentsChanged(slot);
			}
		};
		this.journalHandler = new ItemStackHandler(1) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return stack.getItem() instanceof IAgriJournalItem;
			}
		};
		if (this.seedAnalyzer.hasSeed()) {
			this.seedHandler.insertItem(0, seedAnalyzer.extractSeed(), false);
		}
		if (this.seedAnalyzer.hasJournal()) {
			this.journalHandler.insertItem(0, this.seedAnalyzer.extractJournal(), false);
		}

		//layout seed inventory
		this.addSlot(new SlotItemHandler(this.seedHandler, 0, 26, 38));
		//layout journal inventory
		this.addSlot(new SlotItemHandler(this.journalHandler, 0, 26 , 71));
		//layout player inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new SlotItemHandler(this.playerInventory, 9 + 9 * y + x, 13 + 18 * x, 104 + 18 * y));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlot(new SlotItemHandler(this.playerInventory, i, 13 + 18 * i, 162));
		}

	}

	public List<IAgriGenePair<?>> getGeneToRender() {
		ItemStack seed = seedHandler.getStackInSlot(0).copy();
		if (seed.isEmpty()) {
			return Collections.emptyList();
		}
		return ((IAgriGeneCarrierItem)seed.getItem()).getGenome(seed).map(IAgriGenome::getGeneList).orElse(Collections.emptyList());
	}

	@Override
	public boolean stillValid(Player player) {
		return player.distanceToSqr(seedAnalyzerPos.getX(), seedAnalyzerPos.getY(), seedAnalyzerPos.getZ()) < 8.0F;
	}

	@Override
	public void removed(@Nonnull Player player) {
		if (player instanceof ServerPlayer) {
			ItemStack seedStack = this.seedHandler.extractItem(0, 64, false);
			if (!seedStack.isEmpty()) {
				this.seedAnalyzer.insertSeed(seedStack);
			}
			ItemStack journalStack = this.journalHandler.extractItem(0, 64, false);
			if (!journalStack.isEmpty()) {
				this.seedAnalyzer.insertJournal(journalStack);
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull Player playerIn, int index) {
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
				if (slotStack.getItem() instanceof IAgriSeedItem) {
					if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotStack.getItem() instanceof IAgriJournalItem) {
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
			slot.onTake(playerIn, slotStack);
		}
		return itemStack;
	}

}
