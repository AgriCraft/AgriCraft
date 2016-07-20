/*
 */
package com.infinityraider.agricraft.renderers;

import com.google.common.collect.ImmutableMap;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * Borrowed from MinecraftForge base code.
 * 
 * Had to create a new class since this is hidden in a jumble of forge code normally.
 *
 * @author Forge Team
 */
public class AgriTransform {
	
	// Must go first, for proper classloading.
	private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

	public static final ImmutableMap<TransformType, TRSRTransformation> BLOCK = generateBlockTransform();
	public static final ImmutableMap<TransformType, TRSRTransformation> ITEM = generateItemTransform();

	private static ImmutableMap<TransformType, TRSRTransformation> generateBlockTransform() {
		final TRSRTransformation thirdperson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
		final ImmutableMap.Builder<TransformType, TRSRTransformation> transform = new ImmutableMap.Builder<>();
		transform.put(TransformType.GUI, get(0, 0, 0, 30, 225, 0, 0.625f));
		transform.put(TransformType.GROUND, get(0, 3, 0, 0, 0, 0, 0.25f));
		transform.put(TransformType.FIXED, get(0, 0, 0, 0, 0, 0, 0.5f));
		transform.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
		transform.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
		transform.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
		transform.put(TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 0, 0, 225, 0, 0.4f));
		return transform.build();
	}

	private static ImmutableMap<TransformType, TRSRTransformation> generateItemTransform() {
		final TRSRTransformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
		final TRSRTransformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
		final ImmutableMap.Builder<TransformType, TRSRTransformation> transform = new ImmutableMap.Builder<>();
		transform.put(TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f));
		transform.put(TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
		transform.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
		transform.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
		transform.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
		transform.put(TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
		return transform.build();
	}

	private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
		return new TRSRTransformation(
				new Vector3f(tx / 16, ty / 16, tz / 16),
				TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
				new Vector3f(s, s, s),
				null
		);
	}

	private static TRSRTransformation leftify(TRSRTransformation transform) {
		return TRSRTransformation.blockCenterToCorner(
				flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX)
		);
	}

	public static final Matrix4f getBlockMatrix(TransformType type) {
		if (BLOCK.containsKey(type)) {
			return BLOCK.get(type).getMatrix();
		}  else {
			return TRSRTransformation.identity().getMatrix();
		}
	}

	public static final Matrix4f getItemMatrix(TransformType type) {
		if (ITEM.containsKey(type)) {
			return ITEM.get(type).getMatrix();
		} else {
			return TRSRTransformation.identity().getMatrix();
		}
	}

}
