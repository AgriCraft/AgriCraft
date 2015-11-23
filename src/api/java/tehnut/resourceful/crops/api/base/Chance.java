package tehnut.resourceful.crops.api.base;

public class Chance {

    private double extraSeed;
    private double essenceDrop;

    protected Chance(double extraSeed, double essenceDrop) {
        this.extraSeed = extraSeed;
        this.essenceDrop = essenceDrop;
    }

    public double getExtraSeed() {
        return extraSeed;
    }

    public double getEssenceDrop() {
        return essenceDrop;
    }

    @Override
    public String toString() {
        return "Chance{" +
                "extraSeed=" + extraSeed +
                ", essenceDrop=" + essenceDrop +
                '}';
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(extraSeed);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(essenceDrop);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chance chance = (Chance) o;

        if (Double.compare(chance.extraSeed, extraSeed) != 0) return false;
        return Double.compare(chance.essenceDrop, essenceDrop) == 0;

    }
}
