package loom.entity.animal;

import equilinox.classification.Classifiable;

public interface Predator extends Fighter {
    int range();

    Classifiable[] preys();

    boolean huntsYoung();

    boolean huntsOld();
}
