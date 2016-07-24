/*
 */
package com.infinityraider.agricraft.gui.component;

import com.infinityraider.agricraft.gui.GuiBase;
import com.infinityraider.agricraft.reference.Reference;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public class ComponentStack implements IComponent<ItemStack> {

	public static final ResourceLocation FRAME_ICON = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalSeedFrame.png");

	private final ItemStack stack;
	private final Rectangle bounds;
	private final boolean framed;

	public ComponentStack(ItemStack stack, int x, int y, boolean framed) {
		this.stack = stack;
		this.bounds = new Rectangle(x, y, 16, 16);
		this.framed = framed;
	}

	@Override
	public ItemStack getComponent() {
		return this.stack;
	}

	@Override
	public Rectangle getBounds() {
		return this.bounds;
	}

	@Override
	public void addToolTip(List<String> toolTip, EntityPlayer player) {
		final List<String> itemTip = this.stack.getTooltip(player, false);
		if (!itemTip.isEmpty()) {
			toolTip.addAll(itemTip);
		}
	}

	@Override
	public void renderComponent(GuiBase gui) {
		renderFrame(gui);
		GlStateManager.pushAttrib();
		RenderHelper.enableGUIStandardItemLighting();
		gui.getRenderItem().renderItemAndEffectIntoGUI(stack, this.bounds.x, this.bounds.y);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();
	}

	public void renderFrame(GuiBase gui) {
		if (framed) {
			GlStateManager.pushAttrib();
			GlStateManager.color(1, 1, 1, 1);
			Minecraft.getMinecraft().getTextureManager().bindTexture(FRAME_ICON);
			GuiBase.drawModalRectWithCustomSizedTexture(
					this.bounds.x - 1,
					this.bounds.y - 1,
					0,
					0,
					18,
					18,
					18,
					18
			);
			GlStateManager.popAttrib();
		}
	}

}
