package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.renderers.models.ModelPeripheralProbe;
import com.infinityraider.agricraft.tileentity.TileEntityPeripheral;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import static com.infinityraider.agricraft.renderers.RenderUtil.*;
import com.infinityraider.agricraft.utility.icon.IconUtil;
import net.minecraft.entity.item.EntityItem;

public class RenderPeripheral extends RenderBlockAgriCraft {

	private static final ResourceLocation probeTexture = new ResourceLocation(Reference.MOD_ID + ":textures/blocks/peripheralProbe.png");
	private static final ModelBase probeModel = new ModelPeripheralProbe();

	public RenderPeripheral() {
		super(AgriCraftBlocks.blockPeripheral, new TileEntityPeripheral(), true, true, true);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item) {
		renderBase(tess, COLOR_MULTIPLIER_STANDARD);
	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos) {
		renderBase(tess, block.colorMultiplier(world, pos));
	}

	@Override
	protected void doRenderTileEntity(TessellatorV2 tess, TileEntity te) {
		if (te instanceof TileEntityPeripheral) {
			final TileEntityPeripheral peripheral = (TileEntityPeripheral) te;
			tess.draw();
			drawSeed(peripheral);
			//performAnimations(tess, peripheral, BaseIcons.DEBUG.getIcon(), COLOR_MULTIPLIER_STANDARD);
			tess.startDrawingQuads();
		}
	}

	private void drawSeed(TileEntityPeripheral peripheral) {
		ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
		if (stack == null || stack.getItem() == null) {
			return;
		}

		float dx = 0.75f;
		float dy = 0.85f;
		float dz = 0.50f;
		float scale = 0.75F;
		float angle = 90.0F;

		GL11.glPushMatrix();
		GL11.glTranslated(dx, dy, dz);
		//resize the texture to half the size
		GL11.glScalef(scale, scale, scale);
		//rotate the renderer
		GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(90, 0, 0, 1);

		//TODO: render the seed

		EntityItem item = new EntityItem(AgriCraft.proxy.getClientWorld(), 0, 0, 0, stack.copy().splitStack(1));
		item.hoverStart = 0;
		Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(item, 0, 0, 0, 0, 0);
		
		GL11.glPopMatrix();
	}

	private void performAnimations(TessellatorV2 tessellator, TileEntityPeripheral peripheral, TextureAtlasSprite icon, int cm) {
		int maxDoorPos = TileEntityPeripheral.MAX / 2;
		float unit = Constants.UNIT;

		GL11.glPushMatrix();

		for (AgriForgeDirection dir : TileEntityPeripheral.VALID_DIRECTIONS) {
			int timer = peripheral.getTimer(dir);

			//doors
			float doorPosition = (timer >= maxDoorPos ? maxDoorPos : timer) * 4.0F / maxDoorPos;
			if (doorPosition < 4) {
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
				tessellator.startDrawingQuads();
				drawScaledPrism(tessellator, 4, 2, 0, 8 - doorPosition, 14, 1, icon, cm);
				drawScaledPrism(tessellator, 8 + doorPosition, 2, 0, 12, 14, 1, icon, cm);
				tessellator.draw();
			}

			//probe
			float probePosition = (timer < maxDoorPos ? 0 : timer - maxDoorPos) * 90 / maxDoorPos;
			GL11.glRotatef(180, 0, 0, 1);
			float dx = -0.5F;
			float dy = -1.5F;
			float dz = 9 * unit;
			GL11.glTranslatef(dx, dy, dz);

			float dX = 0.0F;
			float dY = 21.5F * unit;
			float dZ = -5.5F * unit;

			GL11.glTranslatef(dX, dY, dZ);
			GL11.glRotatef(probePosition, 1, 0, 0);
			GL11.glTranslatef(-dX, -dY, -dZ);

			Minecraft.getMinecraft().renderEngine.bindTexture(probeTexture);
			probeModel.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

			GL11.glTranslatef(dX, dY, dZ);
			GL11.glRotatef(-probePosition, 1, 0, 0);
			GL11.glTranslatef(-dX, -dY, -dZ);

			GL11.glTranslatef(-dx, -dy, -dz);
			GL11.glRotatef(-180, 0, 0, 1);

			//rotate 90� for the next render
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotatef(-90, 0, 1, 0);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		}

		GL11.glPopMatrix();
	}

	private void renderBase(TessellatorV2 tessellator2, int colorMultiplier) {
		final TextureAtlasSprite iconTop = IconUtil.getIcon("agricraft:blocks/peripheralTop");
		final TextureAtlasSprite iconSide = IconUtil.getIcon("agricraft:blocks/peripheralSide");
		final TextureAtlasSprite iconBottom = IconUtil.getIcon("agricraft:blocks/peripheralBottom");
		final TextureAtlasSprite iconInside = IconUtil.getIcon("agricraft:blocks/peripheralInner");
		float unit = Constants.UNIT;

		//top
		drawScaledFaceFrontXZ(tessellator2, 0, 0, 16, 16, iconTop, 1, colorMultiplier);
		drawScaledFaceBackXZ(tessellator2, 0, 0, 16, 16, iconTop, 1, colorMultiplier);
		//bottom
		drawScaledFaceFrontXZ(tessellator2, 0, 0, 16, 16, iconBottom, 0, colorMultiplier);
		drawScaledFaceBackXZ(tessellator2, 0, 0, 16, 16, iconBottom, 0, colorMultiplier);
		//front
		drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, iconSide, 0, colorMultiplier);
		drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, iconSide, 0, colorMultiplier);
		//right
		drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, iconSide, 1, colorMultiplier);
		drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, iconSide, 1, colorMultiplier);
		//left
		drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, iconSide, 0, colorMultiplier);
		drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, iconSide, 0, colorMultiplier);
		//back
		drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, iconSide, 1, colorMultiplier);
		drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, iconSide, 1, colorMultiplier);
		//inside top
		drawScaledFaceFrontXZ(tessellator2, 4, 4, 12, 12, iconBottom, 12 * unit, colorMultiplier);
		//inside front
		drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, iconInside, 4 * unit, colorMultiplier);
		drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, iconInside, 4 * unit, colorMultiplier);
		//inside right
		drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, iconInside, 12 * unit, colorMultiplier);
		drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, iconInside, 12 * unit, colorMultiplier);
		//inside left
		drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, iconInside, 4 * unit, colorMultiplier);
		drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, iconInside, 4 * unit, colorMultiplier);
		//inside back
		drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, iconInside, 12 * unit, colorMultiplier);
		drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, iconInside, 12 * unit, colorMultiplier);

	}

}
