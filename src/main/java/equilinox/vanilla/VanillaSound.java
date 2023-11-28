package equilinox.vanilla;

import equilinox.ducktype.SoundReference;

import java.util.Locale;

/**
 * Enum representing various vanilla sounds in the Equilinox game.
 */
@SuppressWarnings("unused")
public enum VanillaSound implements SoundReference {
    /**
     * Placement sounds of bears.
     */
    BEAR_PLACEMENT(false, "bearPlace"),

    /**
     * Sound played randomly by bears.
     */
    BEAR_ROAR,

    /**
     * Sound looped by bee hives.
     */
    BEES,

    /**
     * Reward sound of unlocking a new species by evolution.
     */
    BELL,

    /**
     * Sound looped on grassland biome.
     */
    GRASSLAND_BIRDS(false, "birds"),

    /**
     * Sound looped on woodland biome.
     */
    WOODLAND_BIRDS(false, "birds2"),

    BOUNCE, BUTTON(true, "button1"), BUTTON_SELECT,

    //Sounds played randomly by chickens.
    CHICKEN_0(false, "chic0"), CHICKEN_1(false, "chic1"), CHICKEN_2(false, "chic2"),
    CHICKEN_3(false, "chic3"), CHICKEN_4(false, "chic4"),

    //Random sound played when a button in a GUI is clicked.
    CLICK_1, CLICK_2, CLICK_3, CLICK_4,

    COMPLETE,

    /**
     * Sound played when a save is deleted.
     */
    DELETE_SAVE(true, "deleteSave"),

    //Sounds played randomly by ducks.
    DUCK_0, DUCK_1, DUCK_2,

    //Sounds played randomly by eagles.
    EAGLE_0, EAGLE_1,

    /**
     * Placement sound for frogs and toads. It's also randomly played by frogs.
     */
    FROG_PLACEMENT(false, "frog0"),

    /**
     * Sound played randomly by frogs.
     */
    FROG(false, "frog1"),

    //Sounds played randomly by guinea pigs.
    GUINEA_PIG_0(false, "gPig0"), GUINEA_PIG_1(false, "gPig1"), GUINEA_PIG_2(false, "gPig2"),
    GUINEA_PIG_3(false, "gPig3"), GUINEA_PIG_4(false, "gPig4"),


    //Sounds played randomly by goats.
    GOAT_0, GOAT_1, GOAT_2, GOAT_3,

    /**
     * Placement sounds of grasses, flowers, mushrooms, vegetable plants, and butterflies.
     */
    GRASS_PLACEMENT(false, "grassPlace"),

    /**
     * Sound looped on jungle biome.
     */
    JUNGLE,

    /**
     * Sound played when cash is spent in the game.
     */
    KERCHING(true, "kerching"),

    /**
     * Sound played when something goes wrong, like trying to buy a specimen without having cash or
     * attempting to place an entity in a non-placeable spot.
     */
    NEGATIVE(true, "negative"),

    /**
     * Sound played when a notification spawns.
     */
    NOTIFY(true, "notify"),

    /**
     * Placement sound for pigs and warthogs. It's also randomly played by pigs.
     */
    PIG_PLACEMENT(false, "pig0"),

    /**
     * Sound played randomly by pigs.
     */
    PIG_1, PIG_2, PIG_3,

    /**
     * Sound played when an option is selected in a GUI.
     */
    SELECT(true, "selected"),

    /**
     * Placement sound for sheep and randomly played by it.
     */
    SHEEP_PLACEMENT(false, "sheepBaa2"),

    /**
     * Sound played randomly by sheep.
     */
    SHEEP_BAA,

    /**
     * Placement sounds of fish.
     */
    SPLASH,

    /**
     * Sound looped on swamp biome.
     */
    SWAMP,

    /**
     * Placement sounds of trees, bushes, cactus, and most other entities.
     */
    THUD,

    /**
     * Sound looped on tropical biome.
     */
    TROPICAL,

    /**
     * Sound looped on snow biome.
     */
    SNOW(false, "wind");

    private final String file;
    private final boolean gui;

    VanillaSound(boolean gui, String file) {
        this.file = file;
        this.gui = gui;
        SoundReference.load(this);
    }

    VanillaSound() {
        String[] split = name().split("_");
        StringBuilder builder = new StringBuilder(split[0].toLowerCase(Locale.ROOT));
        for (int i = 1; i < split.length; i++) builder
                .append(split[i].charAt(0))
                .append(split[i].substring(1).toLowerCase(Locale.ROOT));
        this.file = builder.toString();
        this.gui = false;
        SoundReference.load(this);
    }

    @Override
    public String id() {
        return file;
    }

    @Override
    public boolean waits() {
        return !gui;
    }
}
