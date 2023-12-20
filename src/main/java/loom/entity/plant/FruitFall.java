package loom.entity.plant;

import loom.entity.Specie;

public interface FruitFall extends FruitProducer {
    Specie fruit();

    float fallChancePerHour();

    float fruitsSpawnHeight();

    float fruitsSpawnRadius();
}
