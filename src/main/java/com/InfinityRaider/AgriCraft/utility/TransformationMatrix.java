package com.InfinityRaider.AgriCraft.utility;

import net.minecraft.util.Vec3;

public class TransformationMatrix {
    private static final int SIZE = 4;

    private double [][] matrix;

    /** TransformationMatrix for doing nothing (unity matrix) */
    public TransformationMatrix() {
        matrix = new double[SIZE][SIZE];
        for(int i=0;i<SIZE;i++) {
            matrix[i][i]=1;
        }
    }

    /** TransformationMatrix for a rotation (http://xkcd.com/184/)*/
    public TransformationMatrix(double angle, double x, double y, double z) {
        matrix = new double[SIZE][SIZE];
        setRotation(angle, x, y, z);
        matrix[3][3] = 1;
    }

    /** TransformationMatrix for a translation */
    public TransformationMatrix(double x, double y, double z) {
        matrix = new double[SIZE][SIZE];
        setTranslation(x, y, z);
        for(int i=0;i<SIZE;i++) {
            matrix[i][i]=1;
        }
    }

    /** TransformationMatrix for a translation */
    public TransformationMatrix(Vec3 translation) {
        this(translation.xCoord, translation.yCoord, translation.zCoord);
    }

    /** TransformationMatrix for a translation */
    public TransformationMatrix(Vector translation) {
        this(translation.getX(), translation.getY(), translation.getZ());
    }

    /** TransformationMatrix for a rotation and translation */
    public TransformationMatrix(double angle, double x, double y, double z, Vector translation) {
        matrix = new double[SIZE][SIZE];
        setRotation(angle, x, y, z);
        setTranslation(translation);
        //bottom line
        matrix[3][3] = 1;
    }

    /** Custom transformation */
    public TransformationMatrix(double[][] data) {
        this();
        int m = SIZE-1>=data.length?data.length:SIZE-1;
        for(int i=0;i<m;i++) {
            int n = SIZE>=data[i].length?data[i].length:SIZE;
            for(int j=0;j<n;j++) {
                this.matrix[i][j] = data[i][j];
            }
        }
    }

    /** sets the rotation compared to the absolute coordinates while keeping the translation */
    public void setRotation(double angle, double x, double y, double z) {
        Vector axis = new Vector(x, y, z).normalize();
        angle = Math.toRadians(angle);
        x = axis.getX();
        y = axis.getY();
        z = axis.getZ();
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        //x
        matrix[0][0] = x*x*(1-cos) + cos;
        matrix[0][1] = y*x*(1-cos) - z*sin;
        matrix[0][2] = z*x*(1-cos) + y*sin;
        //y
        matrix[1][0] = x*y*(1-cos) + z*sin;
        matrix[1][1] = y*y*(1-cos) + cos;
        matrix[1][2] = y*z*(1-cos) - x*sin;
        //z
        matrix[2][0] = x*z*(1-cos) - y*sin;
        matrix[2][1] = y*z*(1-cos) + x*sin;
        matrix[2][2] = z*z*(1-cos) + cos;
    }

    /** sets the translation compared to the absolute coordinates while keeping the rotation */
    public void setTranslation(Vector v) {
        this.setTranslation(v.getX(), v.getY(), v.getZ());
    }

    /** sets the translation compared to the absolute coordinates while keeping the rotation */
    public void setTranslation(double x, double y, double z) {
        this.matrix[0][3] = x;
        this.matrix[1][3] = y;
        this.matrix[2][3] = z;
    }

    /**
     * gets the translation for this matrix
     * @return a vector with size 3 containing the translation components
     */
    public double[] getTranslation() {
        return new double[] {matrix[0][3], matrix[1][3], matrix[2][3]};
    }

    /** scales the matrix */
    public TransformationMatrix scale(double x, double y, double z) {
        TransformationMatrix m = new TransformationMatrix();
        m.matrix[0][0] = x;
        m.matrix[1][1] = y;
        m.matrix[2][2] = z;
        this.multiplyRightWith(m);
        return this;
    }


    /** Left multiplies this transformation matrix with the argument, for inverse transformations */
    public TransformationMatrix multiplyLeftWith(TransformationMatrix m) {
        double[][] newValues = new double[SIZE][SIZE];
        for(int i=0;i<SIZE;i++) {
            for(int j=0;j<SIZE;j++) {
                double value = 0;
                for(int k=0;k<SIZE;k++) {
                    value = value + m.matrix[i][k] * this.matrix[k][j];
                }
                newValues[i][j] = value;
            }
        }
        this.matrix = newValues;
        return this;
    }

    /** Right multiplies this transformation matrix with the argument, for chaining transformations */
    public TransformationMatrix multiplyRightWith(TransformationMatrix m) {
        double[][] newValues = new double[SIZE][SIZE];
        for(int i=0;i<SIZE;i++) {
            for(int j=0;j<SIZE;j++) {
                double value = 0;
                for(int k=0;k<SIZE;k++) {
                    value = value + this.matrix[i][k] * m.matrix[k][j];
                }
                newValues[i][j] = value;
            }
        }
        this.matrix = newValues;
        return this;
    }

    /** Transforms the given coordinates */
    public double[] transform(double x, double y, double z) {
        double[] coords = new double[] {x, y, z, 1};
        double[] result = new double[3];
        for(int i=0;i<result.length;i++) {
            for(int j=0;j<SIZE;j++) {
                result[i] = result[i] + this.matrix[i][j]*coords[j];
            }
        }
        return result;
    }
}
