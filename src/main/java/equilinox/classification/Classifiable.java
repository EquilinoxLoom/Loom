package equilinox.classification;

public interface Classifiable {
    Classifiable HEAD = new Classifiable() {
        @Override
        public Classifiable getParent() {
            return this;
        }

        @Override
        public String getNode() {
            return "";
        }
    };

    Classifiable getParent();

    String getNode();

    default String getClassification() {
        if (this == HEAD) return "";
        return getParent().getClassification() + getNode();
    }
}
