package loom.entity.living;

public interface FruitProducer {
    /**
     * If the entity has 5 models and at the last two it wields fruits, model index must be 3 and stages must be 2.
     */
    int modelIndex();

    int stages();

    default float time() {
        return 5;
    }
}
