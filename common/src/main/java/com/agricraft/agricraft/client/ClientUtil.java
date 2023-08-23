package com.agricraft.agricraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClientUtil {

	public static void spawnParticlesForPlant(String plant, LevelAccessor level, BlockState state, BlockPos pos) {
		BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(plant));
		if (model == null) {
			return;
		}
		TextureAtlasSprite particleIcon = model.getParticleIcon();
		VoxelShape voxelShape = state.getShape(level, pos);
		voxelShape.forAllBoxes((startX, startY, startZ, endX, endY, endZ) -> {
			double xBoxes = Math.min(1.0, endX - startX);
			double yBoxes = Math.min(1.0, endY - startY);
			double zBoxes = Math.min(1.0, endZ - startZ);
			int maxX = Math.max(2, Mth.ceil(xBoxes / 0.25));
			int maxY = Math.max(2, Mth.ceil(yBoxes / 0.25));
			int maxZ = Math.max(2, Mth.ceil(zBoxes / 0.25));
			for (int p = 0; p < maxX; ++p) {
				for (int q = 0; q < maxY; ++q) {
					for (int r = 0; r < maxZ; ++r) {
						double dx = (p + 0.5D) / maxX;
						double dy = (q + 0.5D) / maxY;
						double dz = (r + 0.5D) / maxZ;
						double ox = dx * xBoxes + startX;
						double oy = dy * yBoxes + startY;
						double oz = dz * zBoxes + startZ;
						TerrainParticle particle = new TerrainParticle((ClientLevel) level, pos.getX() + ox,
								pos.getY() + oy, pos.getZ() + oz, dx - 0.5, dy - 0.5, dz - 0.5, state, pos);
						particle.setSprite(particleIcon);
						Minecraft.getInstance().particleEngine.add(particle);
					}
				}
			}
		});
	}

}
