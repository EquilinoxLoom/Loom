package equilinox.classification;

/**
 * An interface representing entities that can be classified. All entities inherently inherit the HEAD node.
 */
public interface Classifiable {

    /**
     * The mother classification of all entities.
     */
    Classifiable HEAD = new Classifiable() {
        @Override
        public Classifiable getLineage() {
            return this;
        }

        @Override
        public String getNode() {
            return "";
        }
    };

    /**
     * Retrieves the lineage or group to which this specie or classification belongs.
     *
     * @return The classification lineage.
     */
    Classifiable getLineage();

    /**
     * Retrieves the new piece of information added by this classifiable entity.
     *
     * @return The node representing new information.
     */
    String getNode();

    /**
     * Retrieves a string representing the cumulative classification of all nodes before this entity.
     *
     * @return A string representing the sum of all nodes before it.
     * For example, erl represents large rocks: e is the key for non-living entities,
     * r is for rocks and finally l is for large rocks.
     */
    default String getClassification() {
        if (this == HEAD) return "";
        return getLineage().getClassification() + getNode();
    }
}