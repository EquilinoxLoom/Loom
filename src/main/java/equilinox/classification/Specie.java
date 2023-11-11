package equilinox.classification;

public interface Specie extends Classifiable {
    /**
     * Retrieves id of this classifiable entity. Which determines the order in which they are loaded in the game.
     *
     * @return The id of the classifiable entity.
     */
    int getId();
}
