package loom.entity.behaviour;

import loom.entity.Specie;

public interface Builder {
    Specie structure();

    int points();
    int age();

    /**Time in seconds*/
    float buildTime();

    /**Hour of the day it starts building. Birds start building at dawn*/
    default float buildingHour() {
        return 0;
    }
}
