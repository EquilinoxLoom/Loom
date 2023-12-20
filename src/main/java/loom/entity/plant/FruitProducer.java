package loom.entity.plant;

public interface FruitProducer {
    /**
     * If the entity has 5 models, and it wields fruits at the last two stages,
     * model index should be 3 and stages should be 2.
     */
    int modelIndex();

    int stages();

    default float time() {
        return 5;
    }

    int points();
}
