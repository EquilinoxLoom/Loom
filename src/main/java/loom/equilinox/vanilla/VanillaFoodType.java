package loom.equilinox.vanilla;

import loom.entity.plant.FruitProducer;
import loom.entity.structure.StructureEntity;
import loom.equilinox.ducktype.FoodTypeReference;

/**
 * Enumerates different food types in the Equilinox diet system. The specified {@code food type} must match the list
 * of types present in the specified edible.
 */
public enum VanillaFoodType implements FoodTypeReference {

    /**
     * Represents food that, when eaten, results in the entity being devoured. If the entity is living,
     * it can only be consumed when it's halfway grown. Examples include small fishes, weird fishes,
     * most fruits, small plants (excluding seeds, carnivore plants, lilies, and carrots),
     * as well as nuts, bark, potatoes, leafy bushes, and yucca.
     */
    WHOLE,

    /**
     * Represents food that must be a {@link FruitProducer} like fruit bushes and prickly pear.
     * When consumed, the entity goes back a fruit stage.
     */
    FRUIT,

    /**
     * Represents food that includes most flowers (excluding carnivore plants),
     * as well as small cacti and flowery grass, and is eaten as a sample.
     */
    SAMPLE,

    /**
     * Represents meat, the only food to share. It comes in multiple portions, and when all portions are consumed,
     * the entity dies. Examples include various meat entities.
     */
    TO_SHARE,

    /**
     * Represents food that must be a {@link StructureEntity} and can only be consumed when the entity is fully built.
     * When eaten, its container count is decreased. The only entity eaten as honey is the bee hive.
     */
    HONEY,

    /**
     * Represents food that works like WHOLE but can only be eaten if there are at least three of it in the range,
     * and if it's at least three-quarters grown. If the entity has Spawn Death, when consumed, it spawns an amount
     * of entities from 4 to 9, multiplied by a float trait. All root vegetables are eaten as ROOT VEG.
     */
    ROOT_VEG
}