package loom.entity.life;

import equilinox.Environment;

public interface BiomeSpreader {
    Environment biome();

    /**
     * Higher biome spreading strength ensures predominance of the biome within the range.
     * It's usually related to general population density of the specie.
     * The slow-expanding Willows (25), Fir Trees (24), and Spruce trees (23) are the strongest spreading plants,
     * while fast-expanding grasses, primroses (5.5), bamboos, daisies and mushrooms (5)
     * are the weakest spreading plants.<p>
     * Most plants don't go above 6.5, but Cacti (12 to 10) and Water plants (14 to 7) tend to be strongest-spreading.
     */
    int strength();

    /**
     * Distance in grid squares. It's usually 5, except for some trees (6), cacti (7 or 8), wheat and potato (4)
     */
    int distance();
}
