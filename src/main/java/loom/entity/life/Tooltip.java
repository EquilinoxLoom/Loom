package loom.entity.life;

public interface Tooltip {
    String name();

    String description();

    int price();

    default int dpPerMin() {
        return 0;
    }

    int range();

    /**
     * Path to a sound file (.ogg or .wav) at sounds folder in resources or
     * the name a vanilla sound. Below are the vanilla placement sounds and
     * the entities they are used in:
     * <li>thud - trees, bushes, cactus and most other entities
     * <li>grassPlace - grasses, flowers, mushrooms, vegetable plants and butterfly
     * <li>splash - fish
     * <li>sheepBaa2 - sheep and monkey
     * <li>frog0 - frogs
     * <li>pig0 - boar and warthog
     * <li>gPig1 - guinea pig
     * <li>bearPlace - bear
     * <li>goat0 - goat
     */
    default String sound() {
        return "thud";
    }
}
