package equilinox.classification;

public enum Kingdom implements Classifiable {
    PLANT('p'),
    ANIMAL('a'),
    NON_LIVING('e');

    final char c;

    Kingdom(char c) {
        this.c = c;
    }

    @Override
    public Classifiable getParent() {
        return HEAD;
    }

    @Override
    public String getNode() {
        return String.valueOf(c);
    }
}
