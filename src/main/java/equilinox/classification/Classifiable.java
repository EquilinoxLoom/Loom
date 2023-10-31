package equilinox.classification;

public interface Classifiable {
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

    Classifiable getLineage();

    String getNode();

    default String getClassification() {
        if (this == HEAD) return "";
        return getLineage().getClassification() + getNode();
    }
}
