package loom.entity.plant;

import equilinox.classification.Specie;

public interface FruitProducer {
    Specie fruit();

    int count();

    default float time() {
        return 5;
    }
}
