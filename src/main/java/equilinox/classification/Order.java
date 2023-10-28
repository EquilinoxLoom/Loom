package equilinox.classification;

/**
 * NEVER USE FIELDS MARKED AS @DEPRECATED!!!
 */
public enum Order implements Classifiable {
    FOREST_TREE('f', Phylum.TREE),
    GRASS_TREE('g', Phylum.TREE),
    WOODLAND_TREE('w', Phylum.TREE),
    LUSH_TREE('l', Phylum.TREE),
    DESERT_TREE('d', Phylum.TREE),
    SNOW_TREE('m', Phylum.TREE),
    SWAMP_TREE('s', Phylum.TREE),
    JUNGLE_TREE('j', Phylum.TREE),
    TROPICAL_TREE('t', Phylum.TREE),

    FRUIT_BUSH('f', Phylum.BUSH),
    LEAFY_BUSH('l', Phylum.BUSH),

    WATER_PLANT('w', Phylum.SMALL_PLANT),
    GRASSES('g', Phylum.SMALL_PLANT),
    ROOT_VEGETABLE('v', Phylum.SMALL_PLANT),
    FERN('x', Phylum.SMALL_PLANT),
    FLOWER('f', Phylum.SMALL_PLANT),
    HERB('h', Phylum.SMALL_PLANT),
    MUSHROOM('m', Phylum.SMALL_PLANT),

    SMALL_FISH('s', Phylum.FISH),
    BIG_FISH('b', Phylum.FISH),
    WEIRD_FISH('w', Phylum.FISH),

    LARGE_HERBIVORE('l', Phylum.HERBIVORE),
    MEDIUM_HERBIVORE('m', Phylum.HERBIVORE),
    SMALL_HERBIVORE('s', Phylum.HERBIVORE),

    SMALL_BIRD('s', Phylum.BIRD),
    BIRD_OF_PREY('p', Phylum.BIRD),

    SMALL_CARNIVORE('s', Phylum.CARNIVORE),
    LARGE_CARNIVORE('l', Phylum.CARNIVORE),

    STONE('s', Phylum.ROCK),
    LARGE_ROCK('l', Phylum.ROCK),

    @Deprecated LARGE_REPTILE('r', SMALL_HERBIVORE);

    final char c;
    private final Classifiable parent;

    Order(char c, Classifiable parent) {
        this.c = c;
        this.parent = parent;
    }

    @Override
    public Classifiable getParent() {
        return parent;
    }

    @Override
    public String getNode() {
        return String.valueOf(c);
    }
}
