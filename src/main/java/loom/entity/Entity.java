package loom.entity;

import equilinox.VanillaComponent;
import equilinox.classification.Classifiable;
import food.FoodSectionType;

import java.awt.*;
import java.util.Map;


public interface Entity {
    boolean hasComponent(VanillaComponent component);

    Map<VanillaComponent, String> getComponents();

    int getId();

    String getClassification();

    boolean goesUnderwater();

    boolean goesOverwater();

    String[] getModelPaths();

    Classifiable getLineage();

    default float getSize() {
        return 1;
    }

    default boolean isModelRandomized() {
        return false;
    }

    default float getIconSize() {
        return 1;
    }

    default float getIconHeight() {
        return 0;
    }

    /**
     * If positive, the entity can only be placed in a spot where the height of the water is at least the <tt>specified height</tt>.
     * <p>If negative, the entity can be placed in any spot where there is water and also, can be placed in any spot that is up to the <tt>specified height</tt> from a body of water.</p>
     */
    default float getWaterHeightRequired() {
        return 0;
    }

    default boolean hasEggStage() {
        return false;
    }

    Map<Color, Integer> getMaterials();

    Color getSecondNaturalColor();

    Map<FoodSectionType, String> getFoodInfo();
}
