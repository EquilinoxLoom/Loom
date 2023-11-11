package equilinox.classification;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static equilinox.classification.Order.*;

/**
 * Enumeration representing various families of entities in the Equilinox classification system.
 * Each family is associated with a specific super-order, indicating its broader classification.
 *
 * <p>
 * Calling {@link Classifiable#getClassification()} in a family returns its classification followed by its ID.
 * For example, {@code OAK_TREE} returns "ptw2", which stands for plant, tree, woodland tree, and ordinal ID 2.
 * </p>
 *
 * <p>
 * Note: DO NOT USE FIELDS MARKED AS @DEPRECATED!!! Those fields are not implemented entities; they are
 * hollow files within the game files that aren't supposed to be loaded in the game.
 * </p>
 */
@SuppressWarnings("unused")
public enum Family implements Specie {
    @Deprecated TEST(HEAD),
    SHEEP(MEDIUM_HERBIVORE),
    OAK_TREE(WOODLAND_TREE),
    BERRY_BUSH(FRUIT_BUSH),
    JUNIPER_TREE(FOREST_TREE),
    ACER_TREE(FOREST_TREE),
    BROWN_ROCK(LARGE_ROCK),
    HEATHER(FLOWER),
    CHICKEN(SMALL_BIRD),
    BROWN_STONE(STONE),
    FERN(Order.FERN),
    WHEAT(GRASSES),
    TURTLE(REPTILE),
    KELP(WATER_PLANT),
    TROUT(SMALL_FISH),
    RED_FISH(SMALL_FISH),
    WATER_LILY(WATER_PLANT),
    SEAWEED(WATER_PLANT),
    CACTUS(Order.CACTUS),
    PRICKLY_PEAR(CACTUS),
    GRASS_TUFT(GRASSES),
    YUCCA(CACTUS),
    @Deprecated DESERT_ROCK_STRUCTURE(LARGE_ROCK),
    PIKE(BIG_FISH),
    @Deprecated TEST_FIR_TREE(TREE),
    BIRCH_TREE(GRASS_TREE),
    PINK_TREE(LUSH_TREE),
    PALM_TREE(Order.TROPICAL_TREE),
    TALL_TREE(FOREST_TREE),
    @Deprecated SNOW_WOLF(MEDIUM_HERBIVORE),
    CHERRY_TREE(LUSH_TREE),
    TALL_MUSHROOM(MUSHROOM),
    APPLE_TREE(WOODLAND_TREE),
    APPLE(FRUIT),
    @Deprecated TALL_JUNGLE_TREE(TREE),
    JUNGLE_ROCK(LARGE_ROCK),
    JUNGLE_PLANT(LEAFY_BUSH),
    VINE_TREE(JUNGLE_TREE),
    FROG(REPTILE),
    JUNGLE_MUSHROOM(MUSHROOM),
    COCONUT(FRUIT),
    RABBIT(SMALL_HERBIVORE),
    RED_TREE(LUSH_TREE),
    BANANA_TREE(Order.TROPICAL_TREE),
    BANANA(FRUIT),
    CARROT(ROOT_VEGETABLE),
    UMBRELLA_TREE(Order.DESERT_TREE),
    ORANGE_TREE(Order.TROPICAL_TREE),
    ORANGE(FRUIT),
    SQUIRREL(SMALL_HERBIVORE),
    WILD_BOAR(MEDIUM_HERBIVORE),
    POTATO_PLANT(ROOT_VEGETABLE),
    GUINEA_PIG(SMALL_HERBIVORE),
    POTATO(VEGETABLE),
    LARGE_TREE(FOREST_TREE),
    BUTTERFLY(INSECT),
    BEE(INSECT),
    HIVE(STRUCTURE),
    SLIMY_TREE(SWAMP_TREE),
    RED_MUSHROOM(MUSHROOM),
    SWAMP_GRASS(GRASSES),
    @Deprecated MOSS_ROCK(LARGE_ROCK),
    TOMATO_PLANT(FRUIT_BUSH),
    TOUCAN(SMALL_BIRD),
    SPARROW(SMALL_BIRD),
    DUCK(SMALL_BIRD),
    EUCALYPTUS_TREE(GRASS_TREE),
    SPIRAL_TREE(LUSH_TREE),
    NEST(STRUCTURE),
    SYCAMORE_TREE(WOODLAND_TREE),
    BAMBOO(LEAFY_BUSH),
    BLUEBERRY_BUSH(FRUIT_BUSH),
    WOLF(LARGE_CARNIVORE),
    DESERT_HARE(SMALL_HERBIVORE),
    TOAD(REPTILE),
    LIZARD(REPTILE),
    CLOWN_FISH(SMALL_FISH),
    @Deprecated CROCODILE(LARGE_REPTILE),
    BEAR(LARGE_HERBIVORE),
    WARTHOG(MEDIUM_HERBIVORE),
    TULIP(FLOWER),
    CEDAR_TREE(FOREST_TREE),
    @Deprecated TEST_BIRCH_TREE(GRASS_TREE),
    SPRUCE_TREE(SNOW_TREE),
    FOX(SMALL_CARNIVORE),
    CAMEL(LARGE_HERBIVORE),
    @Deprecated PANDA(MEDIUM_HERBIVORE),
    @Deprecated TAPIR(MEDIUM_HERBIVORE),
    @Deprecated ARMADILLO(MEDIUM_HERBIVORE),
    BEAVER(SMALL_HERBIVORE),
    @Deprecated KOMODO(LARGE_REPTILE),
    @Deprecated RACOON(MEDIUM_HERBIVORE),
    GOAT(MEDIUM_HERBIVORE),
    @Deprecated MONKEY(MEDIUM_HERBIVORE),
    MANGO_TREE(Order.TROPICAL_TREE),
    @Deprecated HORSE(MEDIUM_HERBIVORE),
    @Deprecated UNICORN(LARGE_HERBIVORE),
    @Deprecated TALL_DESERT_TREE(TREE),
    @Deprecated REINDEER(MEDIUM_HERBIVORE),
    DEER(MEDIUM_HERBIVORE),
    TWIG(OTHER),
    BARK(OTHER),
    BEAVER_LODGE(STRUCTURE),
    SNAP_DRAGON(FLOWER),
    MEAT(Order.MEAT),
    WILD_MINT(HERB),
    SAGE(HERB),
    OREGANO(HERB),
    ROSEMARY(HERB),
    FLOWER_TREE(Order.TROPICAL_TREE),
    WILLOW_TREE(SWAMP_TREE),
    JUNGLE_FLOWER(FLOWER),
    ELM_TREE(WOODLAND_TREE),
    WOBBLY_TREE(GRASS_TREE),
    DAISY(FLOWER),
    BUTTERCUP(FLOWER),
    POPPY(FLOWER),
    TROPICAL_FLOWER(FLOWER),
    BLUEBELL(FLOWER),
    BUTTON_MUSHROOM(MUSHROOM),
    SMALL_CACTUS(CACTUS),
    GIANT_CACTUS(CACTUS),
    DESERT_TREE(Order.DESERT_TREE),
    JUNGLE_GRASS(GRASSES),
    FICUS_TREE(JUNGLE_TREE),
    CANOPY_TREE(JUNGLE_TREE),
    TROPICAL_MUSHROOM(MUSHROOM),
    PRIMROSE(FLOWER),
    FLOWERY_GRASS(GRASSES),
    TROPICAL_TREE(LEAFY_BUSH),
    LEAFY_PLANT(LEAFY_BUSH),
    PINE_TREE(SNOW_TREE),
    FIR_TREE(SNOW_TREE),
    HOLY_BUSH(BUSH),
    SNOW_ROCK(LARGE_ROCK),
    RED_MAPLE_TREE(GRASS_TREE),
    TROPICAL_SEAWEED(WATER_PLANT),
    NUT_TREE(WOODLAND_TREE),
    NUT(Order.NUT),
    BIRD_MEAT(MEAT),
    SMALL_MEAT(MEAT),
    MANGO(FRUIT),
    TURNIP(ROOT_VEGETABLE),
    BULRUSH(WATER_PLANT),
    SWAMP_FLOWER(FLOWER),
    PEACOCK(SMALL_BIRD),
    DEAD_TREE(SWAMP_TREE),
    @Deprecated FIRE_FLOWER(FLOWER),
    BARLEY(GRASSES),
    LUSH_GRASS(GRASSES),
    ASH_TREE(WOODLAND_TREE),
    ROSE(FLOWER),
    LILY(FLOWER),
    SUN_FLOWER(FLOWER),
    PANSIES(FLOWER),
    PAGODA_TREE(GRASS_TREE),
    AUTUMNAL_TREE(GRASS_TREE),
    HEALBLOOM(FLOWER),
    SEED(FRUIT),
    WITCHWOOD_TREE(JUNGLE_TREE),
    WITCHWOOD_FRUIT(FRUIT),
    DOVE(SMALL_BIRD),
    STONES(STONE),
    BOULDERS(LARGE_ROCK),
    SPIT(OTHER),
    CORAL(WATER_PLANT),
    SHELL(STONE),
    SALMON(SMALL_FISH),
    ANGEL_FISH(SMALL_FISH),
    NEON_FISH(SMALL_FISH),
    ROYAL_GAMMA(SMALL_FISH),
    EAGLE(BIRD_OF_PREY),
    JELLY_FISH(WEIRD_FISH),
    MOON_BUSH(LEAFY_BUSH),
    EAGLE_NEST(STRUCTURE),
    FLY(INSECT),
    CARNIVORE_PLANT(FLOWER),
    TONGUE(STRUCTURE),
    MEERKAT(SMALL_HERBIVORE),
    BURROW(STRUCTURE),
    DESERT_GRASS(GRASSES),
    DESERT_ROCK(LARGE_ROCK),
    MARIGOLDS(FLOWER),
    DOLPHIN(BIG_FISH);

    private static final List<Integer> ids = Arrays.stream(Family.values()).filter(family -> {
        try {
            return !Family.class.getField(family.name()).isAnnotationPresent(Deprecated.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }).map(Family::getId).collect(Collectors.toList());

    /**
     * Represents a cloud entity, belonging to the CLOUD super-order.
     */
    private static class Cloud implements Specie {
        private final int c;

        private Cloud(int c) {
            this.c = c;
            ids.add(c);
        }

        @Override
        public Classifiable getLineage() {
            return CLOUD;
        }

        @Override
        public String getNode() {
            return String.valueOf(c);
        }

        @Override
        public int getId() {
            return c;
        }
    }

    /**
     * Cloud entities with specific IDs.
     */
    public static final Specie CLOUD_0 = new Cloud(1000);
    public static final Specie CLOUD_1 = new Cloud(1001);
    public static final Specie CLOUD_2 = new Cloud(1002);
    public static final Specie CLOUD_3 = new Cloud(1003);

    private final Classifiable superOrder;

    Family(Classifiable superOrder) {
        this.superOrder = superOrder;
    }

    @Override
    public Classifiable getLineage() {
        return superOrder;
    }

    @Override
    public String getNode() {
        return String.valueOf(ordinal());
    }

    @Override
    public int getId() {
        return ordinal();
    }

    /**
     * Checks if a given ID is available for use within the enumeration.
     *
     * @param id The ID to check for availability.
     * @return True if the ID is available, false otherwise.
     */
    public static boolean isIdAvailable(int id) {
        return ids.contains(id);
    }
}
