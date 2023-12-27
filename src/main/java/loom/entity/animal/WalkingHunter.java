package loom.entity.animal;

import loom.entity.Classifiable;

public interface WalkingHunter extends Hunter, Fighter {
    int range();

    Classifiable[] preys();

    boolean huntsYoung();

    boolean huntsOld();
}
