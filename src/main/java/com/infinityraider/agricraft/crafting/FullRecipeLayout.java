/*
 */
package com.infinityraider.agricraft.crafting;

import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.Objects;
import java.util.function.Function;

/**
 * A class to manage a full 3x3 recipe.
 */
public class FullRecipeLayout {

    // Top Row
    public final FuzzyStack e00;
    public final FuzzyStack e01;
    public final FuzzyStack e02;

    // Mid Row
    public final FuzzyStack e10;
    public final FuzzyStack e11;
    public final FuzzyStack e12;

    // Bot Row
    public final FuzzyStack e20;
    public final FuzzyStack e21;
    public final FuzzyStack e22;

    public FullRecipeLayout(FuzzyStack e00, FuzzyStack e01, FuzzyStack e02, FuzzyStack e10, FuzzyStack e11, FuzzyStack e12, FuzzyStack e20, FuzzyStack e21, FuzzyStack e22) {
        this.e00 = e00;
        this.e01 = e01;
        this.e02 = e02;
        this.e10 = e10;
        this.e11 = e11;
        this.e12 = e12;
        this.e20 = e20;
        this.e21 = e21;
        this.e22 = e22;
    }

    public FuzzyStack get(int row, int col) {
        switch (row + col * 10) {
            case 00:
                return this.e00;
            case 01:
                return this.e01;
            case 02:
                return this.e02;
            case 10:
                return this.e10;
            case 11:
                return this.e11;
            case 12:
                return this.e12;
            case 20:
                return this.e20;
            case 21:
                return this.e21;
            case 22:
                return this.e22;
            default:
                return null;
        }
    }

    public FullRecipeLayout map(Function<FuzzyStack, FuzzyStack> mapper) {
        return new FullRecipeLayout(
                mapper.apply(e00), mapper.apply(e01), mapper.apply(e02),
                mapper.apply(e10), mapper.apply(e11), mapper.apply(e12),
                mapper.apply(e20), mapper.apply(e21), mapper.apply(e22)
        );
    }

    public FuzzyStack[] getElements() {
        return new FuzzyStack[]{
            this.e00, this.e01, this.e02,
            this.e10, this.e11, this.e12,
            this.e20, this.e21, this.e22
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FullRecipeLayout) {
            final FullRecipeLayout other = (FullRecipeLayout) obj;
            return Objects.equals(this.e00, other.e00) && Objects.equals(this.e01, other.e01) && Objects.equals(this.e02, other.e02)
                    && Objects.equals(this.e10, other.e10) && Objects.equals(this.e11, other.e11) && Objects.equals(this.e12, other.e12)
                    && Objects.equals(this.e20, other.e20) && Objects.equals(this.e21, other.e21) && Objects.equals(this.e22, other.e22);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.e00);
        hash = 23 * hash + Objects.hashCode(this.e01);
        hash = 23 * hash + Objects.hashCode(this.e02);
        hash = 23 * hash + Objects.hashCode(this.e10);
        hash = 23 * hash + Objects.hashCode(this.e11);
        hash = 23 * hash + Objects.hashCode(this.e12);
        hash = 23 * hash + Objects.hashCode(this.e20);
        hash = 23 * hash + Objects.hashCode(this.e21);
        hash = 23 * hash + Objects.hashCode(this.e22);
        return hash;
    }

}
