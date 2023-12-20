package loom.entity.animal;

import loom.entity.Classifiable;

public interface Territorial extends Fighter {
    float defendCooldown();

    Classifiable enemy();

    /**Wolves are noticeable, bees are not.*/
    boolean noticeable();
}
