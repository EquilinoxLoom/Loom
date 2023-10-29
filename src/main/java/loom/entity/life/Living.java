package loom.entity.life;

public interface Living {
    float population();

    float lifespan();

    /**
     * How much parent classification are considered for average population, it is empty if they are not.<p>
     * Array length <STRONG>can not</STRONG> go above classification length.
     * For example, trouts are small fish (afs) and the specie has a population factor of {@code 0.1},
     * so it takes 10% of other small fish populations in account for its own average population.
     */
    default float[] populationFactors() {
        return new float[]{};
    }

    float breedingMaturity();

    float breedingCooldown();

    /**
     * True if the entity grows in size within life stages like all animals<br>
     * False if the entity simply jumps within model stages like all plants
     */
    boolean dynamic();

    /**
     * Size substages within plant stages
     */
    default int subStages() {
        return 1;
    }

    float growthTime();

    int stages();
}
