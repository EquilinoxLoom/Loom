package loom.entity.behaviour;

import loom.entity.Classifiable;

public interface Predator extends Fighter {
    int range();

    Classifiable[] preys();

    boolean huntsYoung();

    boolean huntsOld();
}
