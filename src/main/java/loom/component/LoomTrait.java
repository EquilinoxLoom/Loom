package loom.component;

import breedingTraits.FloatTrait;
import breedingTraits.FloatTraitBlueprint;

/**
 * Extension of the FloatTrait class with additional functionality.
 */
public class LoomTrait extends FloatTrait {
    private final int x;

    /**
     * Constructs a LoomTrait based on an existing FloatTrait.
     *
     * @param trait The original FloatTrait.
     */
    public LoomTrait(FloatTrait trait) {
        super(trait.value, (FloatTraitBlueprint) trait.blueprint);

        // Calculate the value of 'x' based on the trait's price.
        double t = ((FloatTraitBlueprint) this.blueprint).steepness;
        double d = Math.pow(t, 99);
        double m0 = 9e4;
        double m1 = 2.13e9 * 1200;

        this.x = (int) (Math.log(d / 2 - Math.sqrt(
                m0 * Math.pow(d, 2) + m1 * (d / t) - m1 * d - 4 * m0 * (d * t) + 1200
        ) / 600.0) / Math.log(t));
    }

    /**
     * Gets the maximum value that can be bought for the trait.
     *
     * @return The maximum value.
     */
    public double getMax() {
        return 100 + x;
    }

    /**
     * Gets the minimum value that can be bought for the trait.
     *
     * @return The minimum value.
     */
    public double getMin() {
        return Math.max(100 - x, 1);
    }
}
