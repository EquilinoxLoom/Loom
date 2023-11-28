package loom.entity.living;

import equilinox.ducktype.BiomeReference;

/**
 * An entity implementing this interface can spread biomes.
 */
public interface BiomeSpreader {

    /**
     * Gets the spreading biome.
     *
     * @return The biome.
     */
    BiomeReference biome();

    /**
     * Gets the biome spreading strength, which ensures predominance of the biome within the range.
     * Higher spreading strength is usually related to the general population density of the species.
     * Examples of spreading strengths:
     * <ul>
     *     <li>Slow-expanding plants: Willows (25), Fir Trees (24), Spruce Trees (23)</li>
     *     <li>Fast-expanding plants: Grasses, Primroses (5.5), Bamboos, Daisies, Mushrooms (5)</li>
     * </ul>
     * In addition, there are some biome spreaders whose biome is spread by only a few species,
     * and to counter this situation, some of them are really strong spreaders:
     * <ul>
     *     <li>Strongest spreading plants: Cacti (12 to 10), Aquatic Plants (14 to 7)</li>
     * </ul>
     *
     * @return The biome spreading strength.
     */
    int strength();

    /**
     * Gets the spreading distance in grid squares.
     * Examples of spreading distances:
     * <ul>
     *     <li>Most spreaders: 5</li>
     *     <li>Some trees: 6</li>
     *     <li>Cacti: 7 or 8</li>
     *     <li>Wheat and Potato: 4</li>
     * </ul>
     *
     * @return The spreading distance.
     */
    int distance();
}
