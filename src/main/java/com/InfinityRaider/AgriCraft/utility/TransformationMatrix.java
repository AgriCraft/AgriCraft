package com.InfinityRaider.AgriCraft.utility;

public class TransformationMatrix {
    private static final int SIZE = 4;

    private double [][] matrix;

    TransformationMatrix(double angle, double x, double y, double z, Vector translation) {
        Vector axis = new Vector(x, y, z).normalize();
        angle = Math.toRadians(angle);
        matrix = new double[SIZE][SIZE];
        x = axis.getX();
        y = axis.getY();
        z = axis.getZ();
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        //x
        matrix[0][0] = x*x*(1-cos) + cos;
        matrix[0][1] = y*x*(1-cos) - z*sin;
        matrix[0][2] = z*x*(1-cos) + y*sin;
        matrix[0][3] = translation.getX();
        //y
        matrix[1][0] = x*y*(1-cos) + z*sin;
        matrix[1][1] = y*y*(1-cos) + cos;
        matrix[1][2] = y*z*(1-cos) - x*sin;
        matrix[1][3] = translation.getY();
        //z
        matrix[2][0] = x*z*(1-cos) - y*sin;
        matrix[2][1] = y*z*(1-cos) + x*sin;
        matrix[2][2] = z*z*(1-cos) + cos;
        matrix[2][3] = translation.getZ();
        //bottom line
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;
    }

    public void multiplyLeftWith(TransformationMatrix m) {
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
    }

    public void multiplyRightWith(TransformationMatrix m) {
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
    }
}
