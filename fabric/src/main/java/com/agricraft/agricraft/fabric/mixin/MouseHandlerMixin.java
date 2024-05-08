package com.agricraft.agricraft.fabric.mixin;

import com.agricraft.agricraft.common.item.SeedBagItem;
import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

	@Inject(method = "onScroll(JDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void onMouseScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci, boolean discreteScroll, double sensitivity, double scrollDeltaX, double scrollDeltaY, int i, int j, int delta) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || !player.isShiftKeyDown() || !player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.SEED_BAG.get())) {
			return;
		}
		ci.cancel();
		SeedBagItem.changeSorter(player.getItemInHand(InteractionHand.MAIN_HAND), delta);
		int s = player.getItemInHand(InteractionHand.MAIN_HAND).getOrCreateTag().getInt("sorter");
		String id = SeedBagItem.SORTERS.get(s).getId().toString().replace(":", ".");
		player.displayClientMessage(Component.translatable("agricraft.tooltip.bag.sorter")
				.append(Component.translatable("agricraft.tooltip.bag.sorter." + id)), true);
	}
}
