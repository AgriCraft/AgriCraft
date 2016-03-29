package com.infinityraider.agricraft.utility;


import net.minecraft.util.math.Vec3d;

public final class TransformationMatrix {

	private static final int SIZE = 4;
	private static final int ELEMENTS = SIZE * SIZE;

	private double[] matrix;

	/**
	 * TransformationMatrix for doing nothing (unity matrix)
	 */
	public TransformationMatrix() {
		matrix = new double[ELEMENTS];
		for (int i = 0; i < SIZE; i++) {
			set(matrix, i, i, 1);
		}
	}

	/**
	 * TransformationMatrix for doing nothing (unity matrix)
	 */
	public TransformationMatrix(TransformationMatrix other) {
		this.matrix = new double[ELEMENTS];
		System.arraycopy(other.matrix, 0, this.matrix, 0, ELEMENTS);
	}

	/**
	 * TransformationMatrix for a rotation (http://xkcd.com/184/)
	 */
	public TransformationMatrix(double angle, double x, double y, double z) {
		matrix = new double[ELEMENTS];
		setRotation(angle, x, y, z);
		set(matrix, 3, 3, 1);
	}

	/**
	 * TransformationMatrix for a translation
	 */
	public TransformationMatrix(double x, double y, double z) {
		matrix = new double[ELEMENTS];
		setTranslation(x, y, z);
		for (int i = 0; i < SIZE; i++) {
			set(matrix, i, i, 1);
		}
	}

	/**
	 * TransformationMatrix for a translation
	 */
	public TransformationMatrix(Vec3d translation) {
		this(translation.xCoord, translation.yCoord, translation.zCoord);
	}

	/**
	 * TransformationMatrix for a translation
	 */
	public TransformationMatrix(Vector translation) {
		this(translation.getX(), translation.getY(), translation.getZ());
	}

	/**
	 * TransformationMatrix for a rotation and translation
	 */
	public TransformationMatrix(double angle, double x, double y, double z, Vector translation) {
		matrix = new double[ELEMENTS];
		setRotation(angle, x, y, z);
		setTranslation(translation);
		//bottom line
		set(matrix, 3, 3, 1);
	}

	private static void set(double[] m, int r, int c, double e) {
		m[c + r * SIZE] = e;
	}
	
	private static void inc(double[] m, int r, int c, double e) {
		m[c + r * SIZE] += e;
	}

	private static double get(double[] m, int r, int c) {
		return m[c + r * SIZE];
	}

	/**
	 * sets the rotation compared to the absolute coordinates while keeping the
	 * translation
	 */
	public void setRotation(double angle, double x, double y, double z) {
		Vector axis = new Vector(x, y, z).normalize();
		angle = Math.toRadians(angle);
		x = axis.getX();
		y = axis.getY();
		z = axis.getZ();
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);

		//x
		set(matrix, 0, 0, x * x * (1 - cos) + cos);
		set(matrix, 0, 1, y * x * (1 - cos) - z * sin);
		set(matrix, 0, 2, z * x * (1 - cos) + y * sin);
		//y
		set(matrix, 1, 0, x * y * (1 - cos) + z * sin);
		set(matrix, 1, 1, y * y * (1 - cos) + cos);
		set(matrix, 1, 2, y * z * (1 - cos) - x * sin);
		//z
		set(matrix, 2, 0, x * z * (1 - cos) - y * sin);
		set(matrix, 2, 1, y * z * (1 - cos) + x * sin);
		set(matrix, 2, 2, z * z * (1 - cos) + cos);
	}

	/**
	 * sets the translation compared to the absolute coordinates while keeping
	 * the rotation
	 */
	public void setTranslation(Vector v) {
		this.setTranslation(v.getX(), v.getY(), v.getZ());
	}

	/**
	 * sets the translation compared to the absolute coordinates while keeping
	 * the rotation
	 */
	public void setTranslation(double x, double y, double z) {
		set(matrix, 0, 3, x);
		set(matrix, 1, 3, y);
		set(matrix, 2, 3, z);
	}

	/**
	 * gets the translation for this matrix
	 *
	 * @return a vector with size 3 containing the translation components
	 */
	public double[] getTranslation() {
		return new double[]{get(matrix, 0, 3), get(matrix, 1, 3), get(matrix, 2, 3)};
	}

	/**
	 * scales the matrix
	 */
	public TransformationMatrix scale(double x, double y, double z) {
		TransformationMatrix m = new TransformationMatrix();
		set(m.matrix, 0, 0, x);
		set(m.matrix, 1, 1, y);
		set(m.matrix, 2, 2, z);
		multiplyRightWith(m);
		return this;
	}

	/**
	 * Left multiplies this transformation matrix with the argument, for inverse
	 * transformations
	 */
	public TransformationMatrix multiplyLeftWith(TransformationMatrix m) {
		this.matrix = multi(m, this);
		return this;
	}

	/**
	 * Right multiplies this transformation matrix with the argument, for
	 * chaining transformations
	 */
	public TransformationMatrix multiplyRightWith(TransformationMatrix m) {
		this.matrix = multi(this, m);
		return this;
	}
	
	private static double[] multi(TransformationMatrix a, TransformationMatrix b) {
		double[] newValues = new double[ELEMENTS];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				double value = 0;
				for (int k = 0; k < SIZE; k++) {
					value += get(a.matrix, i, k) * get(b.matrix, k, j);
				}
				set(newValues, i, j, value);
			}
		}
		return newValues;
	}

	/**
	 * Transforms the given coordinates
	 */
	public double[] transform(double x, double y, double z) {
		double[] coords = new double[]{x, y, z, 1};
		double[] result = new double[3];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < SIZE; j++) {
				result[i] += get(matrix, i, j) * coords[j];
			}
		}
		return result;
	}
}
