package loom.entity.living;

import equilinox.classification.Classifiable;

public interface Predator extends Fighter {
    int range();

    Classifiable[] preys();

    boolean huntsYoung();

    boolean huntsOld();
}
