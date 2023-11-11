package loom.entity;

import equilinox.classification.Specie;
import food.FoodSectionType;
import loom.component.PrintableComponent;

import java.awt.*;
import java.util.Map;

/**
 * An interface representing entities in the Equilinox game system. Entities are the various creatures and objects
 * that populate the game world and are characterized by their components, behaviors, and classifications.
 */
public interface Entity extends Specie {

    /**
     * Checks if the entity has a specific component.
     *
     * @param component The component to check for.
     * @return True if the entity has the specified component, false otherwise.
     */
    boolean hasComponent(PrintableComponent component);

    /**
     * @return A map containing components and their corresponding .csv data strings.
     */
    Map<PrintableComponent, String> getComponents();

    /**
     * @return True if the entity can go underwater, false otherwise.
     */
    boolean goesUnderwater();

    /**
     * @return True if the entity can go overwater, false otherwise.
     */
    boolean goesOverwater();

    /**
     * @return An array of paths inside the jar to the entity stage models.
     */
    String[] getModelPaths();

    /**
     * @return The size of the entity.
     */
    default float getSize() {
        return 1;
    }

    /**
     * @return True if the model is randomized, false otherwise.
     */
    default boolean isModelRandomized() {
        return false;
    }

    /**
     * @return The size of the icon associated with the entity.
     */
    default float getIconSize() {
        return 1;
    }

    /**
     * @return The height of the icon associated with the entity.
     */
    default float getIconHeight() {
        return 0;
    }

    /**
     * Retrieves the required height of water for the entity.
     * If positive, the entity can only be placed in a spot where
     * the height of the water is at least the specified height.
     * If negative, the entity can be placed in any spot where
     * there is water and also, can be placed in any spot that is up to the specified height from a body of water.
     *
     * @return The required water height for the entity.
     */
    default float getWaterHeightRequired() {
        return 0;
    }

    /**
     * @return True if the entity has an egg stage, false otherwise.
     */
    default boolean hasEggStage() {
        return false;
    }

    /**
     * Retrieves a map of materials associated with their corresponding colors and in-game prices for the entity.
     *
     * @return A map of colors and prices.
     */
    Map<Color, Integer> getMaterials();

    /**
     * @return The second natural color of the entity.
     */
    Color getSecondNaturalColor();

    /**
     * @return A map of food sections and its .csv data strings.
     */
    Map<FoodSectionType, String> getFoodInfo();
}
