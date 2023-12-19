package loom.entity.animal;

import loom.entity.Classifiable;

public interface Predator extends Fighter {
    int range();

    Classifiable[] preys();

    boolean huntsYoung();

    boolean huntsOld();
}
