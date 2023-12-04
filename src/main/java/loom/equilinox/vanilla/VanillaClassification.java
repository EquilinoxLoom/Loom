package loom.equilinox.vanilla;

import loom.entity.Classifiable;

/**
 * Enum representing various orders of entities in the Equilinox classification system.
 * Each family is associated with a specific parent, indicating its broader classification.
 *
 * <p>
 * Note: DO NOT USE FIELDS MARKED AS @DEPRECATED!!! Those fields are not implemented classifications;
 * they are hollow files within the game files that aren't supposed to be loaded in the game.
 * </p>
 */
public enum VanillaClassification implements Classifiable {
    PLANT('p', HEAD),
    ANIMAL('a', HEAD),
    NON_LIVING('e', HEAD),
    
    TREE('t', PLANT),
    BUSH('b', PLANT),
    CACTUS('c', PLANT),
    SMALL_PLANT('n', PLANT),

    FISH('f', ANIMAL),
    HERBIVORE('h', ANIMAL),
    BIRD('b', ANIMAL),
    CARNIVORE('c', ANIMAL),
    INSECT('i', ANIMAL),
    REPTILE('r', ANIMAL),

    ROCK('r', NON_LIVING),
    OTHER('o', NON_LIVING),
    CLOUD('c', NON_LIVING),
    FRUIT('f', NON_LIVING),
    VEGETABLE('v', NON_LIVING),
    NUT('n', NON_LIVING),
    MEAT('m', NON_LIVING),
    STRUCTURE('s', NON_LIVING),
    
    FOREST_TREE('f', TREE),
    GRASS_TREE('g', TREE),
    WOODLAND_TREE('w', TREE),
    LUSH_TREE('l', TREE),
    DESERT_TREE('d', TREE),
    SNOW_TREE('m', TREE),
    SWAMP_TREE('s', TREE),
    JUNGLE_TREE('j', TREE),
    TROPICAL_TREE('t', TREE),

    FRUIT_BUSH('f', BUSH),
    LEAFY_BUSH('l', BUSH),

    WATER_PLANT('w', SMALL_PLANT),
    GRASSES('g', SMALL_PLANT),
    ROOT_VEGETABLE('v', SMALL_PLANT),
    FERN('x', SMALL_PLANT),
    FLOWER('f', SMALL_PLANT),
    HERB('h', SMALL_PLANT),
    MUSHROOM('m', SMALL_PLANT),

    SMALL_FISH('s', FISH),
    BIG_FISH('b', FISH),
    WEIRD_FISH('w', FISH),

    LARGE_HERBIVORE('l', HERBIVORE),
    MEDIUM_HERBIVORE('m', HERBIVORE),
    SMALL_HERBIVORE('s', HERBIVORE),

    SMALL_BIRD('s', BIRD),
    BIRD_OF_PREY('p', BIRD),

    SMALL_CARNIVORE('s', CARNIVORE),
    LARGE_CARNIVORE('l', CARNIVORE),

    STONE('s', ROCK),
    LARGE_ROCK('l', ROCK),

    @Deprecated LARGE_REPTILE('r', SMALL_HERBIVORE);

    final char c;
    private final Classifiable parent;

    VanillaClassification(char c, Classifiable parent) {
        this.c = c;
        this.parent = parent;
    }

    @Override
    public Classifiable getLineage() {
        return parent;
    }

    @Override
    public String getNode() {
        return String.valueOf(c);
    }
}
