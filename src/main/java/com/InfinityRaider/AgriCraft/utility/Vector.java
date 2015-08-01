package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public class Vector {
    private double x;
    private double y;
    private double z;

    public static final Vector NULLVECTOR = new Vector(0, 0, 0);

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Vec3 vec) {
        this.x = vec.xCoord;
        this.y = vec.yCoord;
        this.z = vec.zCoord;
    }

    public Vector(NBTTagCompound tag) throws UnknownPositionException {
        if(!tag.hasKey(Names.NBT.x)) {throw new UnknownPositionException();}
        if(!tag.hasKey(Names.NBT.y)) {throw new UnknownPositionException();}
        if(!tag.hasKey(Names.NBT.z)) {throw new UnknownPositionException();}
        this.x = tag.getDouble(Names.NBT.x);
        this.y = tag.getDouble(Names.NBT.y);
        this.z = tag.getDouble(Names.NBT.z);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble(Names.NBT.x, x);
        tag.setDouble(Names.NBT.y, y);
        tag.setDouble(Names.NBT.z, z);
        return tag;
    }

    public void setX( double x) {this.x = x;}

    public void setY(double y) {this.y = y;}

    public void setZ(double z) {this.z = z;}

    public  double getX() {return x;}

    public  double getY() {return y;}

    public  double getZ() {return z;}

    /** returns a new vector gotten by adding v to this vector (this + v) */
    public Vector add(Vector v) {
        return new Vector(this.x+v.x, this.y+v.y, this.z+v.z);
    }

    /** returns a new vector gotten by substracting vector v from this vector (this - v)*/
    public Vector substract(Vector v) {
        return new Vector(this.x-v.x, this.y-v.y, this.z-v.z);
    }

    /** returns a scaled copy of this vector */
    public Vector scale(double d) {
        Vector v = this.copy();
        if(d!=1) {
            v.x = v.x * d;
            v.y = v.y * d;
            v.z = v.z * d;
        }
        return this;
    }

    /** Returns a normalised vector normal to this vector in the xz plane*/
    public Vector getNormal() {
        Vector normal = new Vector(1.0D/this.getX(), 0, -1.0/this.getZ());
        normal.normalize();
        return normal;
    }

    /** Returns a normalised vector normal to this vector and its normal */
    public Vector getBiNormal() {
        Vector biNormal = crossProduct(this, this.getNormal());
        biNormal.normalize();
        return  biNormal;
    }

    /** Normalizes this vector */
    public Vector normalize() {
        double norm = norm();
        if(norm==0) {
            return this;
        }
        this.scale(1.0D/norm());
        return this;
    }

    /** Returns the norm of this vector */
    public double norm() {
        return Math.sqrt(dotProduct(this, this));
    }

    /** Calculates the dotproduct of a and b (a.b) */
    public static double dotProduct(Vector a, Vector b) {
        return a.getX()*b.getX() + a.getY()*b.getY() + a.getZ()*b.getZ();
    }

    /** Calculates the crossproduct of a and b (axb)*/
    public static Vector crossProduct(Vector a, Vector b) {
        double vX = a.y*b.z - a.z*b.y;
        double vY = a.z*b.x - a.x-b.z;
        double vZ = a.x*b.y - a.y*-b.x;
        return new Vector(vX, vY, vZ);
    }

    /** Projects this vector on the direction defined by the argument (argument is not modified) */
    public Vector projectOn(Vector v) {
        Vector copy = v.copy();
        copy.normalize();
        double norm = dotProduct(this, copy);
        if(norm == 0) {
            return NULLVECTOR.copy();
        }
        copy.scale(norm);
        return copy;
    }

    /** Copies this vector into a new Object */
    public Vector copy() {
        return new Vector(this.x, this.y, this.z);
    }

    public static class UnknownPositionException extends Exception {
        public UnknownPositionException() {
            super("Position not found on NBT");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vector) {
            Vector v = (Vector) obj;
            return v.x == this.x && v.y == this.y && v.z == this.z;
        }
        return false;
    }

}
