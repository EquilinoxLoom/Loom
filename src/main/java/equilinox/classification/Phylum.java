package equilinox.classification;

import static equilinox.classification.Kingdom.*;

public enum Phylum implements Classifiable {
    TREE('t', PLANT),
    BUSH('b', PLANT),
    CACTUS('c', PLANT),
    SMALL_PLANT('n', PLANT),

    FISH('f', ANIMAL),
    HERBIVORE('h', ANIMAL),
    BIRD('b', ANIMAL),
    CARNIVORE('c', ANIMAL),
    INSECT('i', ANIMAL),
    REPTILE('r', ANIMAL),

    ROCK('r', NON_LIVING),
    OTHER('o', NON_LIVING),
    CLOUD('c', NON_LIVING),
    FRUIT('f', NON_LIVING),
    VEGETABLE('v', NON_LIVING),
    NUT('n', NON_LIVING),
    MEAT('m', NON_LIVING),
    STRUCTURE('s', NON_LIVING);

    final char c;
    private final Classifiable kingdom;

    Phylum(char c, Classifiable kingdom) {
        this.c = c;
        this.kingdom = kingdom;
    }

    @Override
    public Classifiable getParent() {
        return kingdom;
    }

    @Override
    public String getNode() {
        return String.valueOf(c);
    }
}
