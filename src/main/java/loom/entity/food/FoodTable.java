package loom.entity.food;

import loom.entity.Entity;
import loom.entity.Specie;
import loom.entity.Tooltip;
import loom.entity.life.Death;
import loom.entity.plant.FruitProducer;
import loom.entity.system.Particles;
import loom.entity.weaver.EntityComponent;
import loom.entity.weaver.EntityPrint;
import loom.equilinox.ducktype.FoodTypeReference;
import loom.equilinox.vanilla.VanillaClassification;
import loom.equilinox.vanilla.VanillaComponent;
import loom.equilinox.vanilla.VanillaFoodType;
import loom.equilinox.vanilla.VanillaSpecie;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a table that manages the edibility properties of a specific entity. It is designed to associate various
 * {@link FoodTypeReference} instances with corresponding edibility characteristics,
 * such as hunger points and death animations.
 * All edible entities have an edibility floating trait that multiplies the amount of hunger points fulfilled.
 */
public class FoodTable extends EntityComponent {
    private static final Map<VanillaFoodType, Set<Specie>> VANILLA_TABLE = new EnumMap<>(VanillaFoodType.class);

    static {
        Set<Specie> fruitProducers = VanillaClassification.FRUIT_BUSH.getOffspring();
        fruitProducers.add(VanillaSpecie.BARLEY);
        fruitProducers.add(VanillaSpecie.DESERT_GRASS);
        fruitProducers.add(VanillaSpecie.HOLY_BUSH);
        fruitProducers.add(VanillaSpecie.PRICKLY_PEAR);
        fruitProducers.add(VanillaSpecie.WHEAT);
        VANILLA_TABLE.put(VanillaFoodType.FRUIT, fruitProducers);

        VANILLA_TABLE.put(VanillaFoodType.HONEY, new HashSet<>(Collections.singletonList(VanillaSpecie.HIVE)));

        VANILLA_TABLE.put(VanillaFoodType.ROOT_VEG, VanillaClassification.ROOT_VEGETABLE.getOffspring());

        Set<Specie> samples = VanillaClassification.FLOWER.getOffspring();
        samples.remove(VanillaSpecie.CARNIVORE_PLANT);
        VANILLA_TABLE.put(VanillaFoodType.SAMPLE, samples);

        VANILLA_TABLE.put(VanillaFoodType.TO_SHARE, VanillaClassification.MEAT.getOffspring());

        Set<Specie> fruits = VanillaClassification.FRUIT.getOffspring();
        Set<Specie> smallFishes = VanillaClassification.SMALL_FISH.getOffspring();
        Set<Specie> smallPlants = VanillaClassification.SMALL_PLANT.getOffspring();
        smallPlants.remove(VanillaSpecie.CARNIVORE_PLANT);
        smallPlants.remove(VanillaSpecie.CARROT);
        VANILLA_TABLE.put(VanillaFoodType.WHOLE, Stream.of(fruits, smallFishes, smallPlants).flatMap(Set::stream).collect(Collectors.toSet()));
    }

    private static final Map<FoodTypeReference, Set<Specie>> TABLE = new HashMap<>(VANILLA_TABLE);

    private final Map<FoodTypeReference, String> foodTable = new HashMap<>();
    
    private final Entity entity;

    private String add = "";

    /**
     * @param entity The entity for which the FoodTable is created.
     */
    public FoodTable(Entity entity) {
        this.entity = entity;

        if (entity instanceof FruitProducer producer) {
            addEdible(VanillaFoodType.FRUIT, producer.points());
        }
    }

    private void addEdible(FoodTypeReference reference, int points, Object... build) {
        foodTable.put(reference, EntityPrint.print(";", reference.name(), points, build));
    }


    /**
     * @param points the hunger points fulfilled by eating this entity.
     * @return this FoodTable instance.
     * @see VanillaFoodType#WHOLE
     */
    public FoodTable addEdibleAsWhole(int points) {
        addEdible(VanillaFoodType.WHOLE, points, Death.newParticleDeath( new Particles(
                entity.getMaterials().keySet().iterator().next(),
                false, 0.1f, 0.8f, Particles.newPoint(),
                8, 0.6f, 0.2f, 0.5f, 0.05f, 0, 0, 0, 0.4f, 0.1f, 0.1f)
                .setDirection(0, 1, 0, 0.3f)).build());
        return this;
    }

    /**
     * @param points     the hunger points fulfilled by eating this entity.
     * @param death      the death animation played when it's eaten.
     * @param controlled if true, this can only be eaten if it's fully grown and there are at least three other ones
     *                   of it in the range of the eater.
     * @return this food table.
     * @see VanillaFoodType#ROOT_VEG
     * @see VanillaFoodType#WHOLE
     */
    public FoodTable addEdibleAsWhole(int points, Death death, boolean controlled) {
        addEdible(controlled ? VanillaFoodType.ROOT_VEG : VanillaFoodType.WHOLE, points, death.build());
        return this;
    }

    /**
     * @param points the hunger points fulfilled by eating this entity.
     * @return this food table.
     * @see VanillaFoodType#SAMPLE
     */
    public FoodTable addEdibleAsSample(int points) {
        addEdible(VanillaFoodType.SAMPLE, points);
        return this;
    }

    /**
     * @param points   the hunger points fulfilled by eating this entity.
     * @param portions the number of portions available for sharing.
     * @return this food table.
     * @see VanillaFoodType#TO_SHARE
     */
    public FoodTable addEdibleAsShareable(int points, int portions) {
        addEdible(VanillaFoodType.TO_SHARE, points, portions);
        return this;
    }


    /**
     * @param points the hunger points fulfilled by eating this entity.
     * @return this food table.
     * @see VanillaFoodType#SAMPLE
     * @see VanillaComponent#HIVE
     */
    public FoodTable addEdibleContainer(int points) {
        if (!entity.hasComponent(VanillaComponent.HIVE)) {
            System.err.println("Entity " + entity.getId() + " must be a structure container");
            return null;
        }
        addEdible(VanillaFoodType.HONEY, points);
        return this;
    }

    /**
     * Fruits, meat and other food entities are food items, that means they aren't an entity by themselves.
     *
     * @param decayingTime the time for this entity to disappear.
     * @return this food table.
     * @see VanillaComponent#ITEM
     * @see VanillaComponent#DECAY
     */
    public FoodTable setFoodItem(float decayingTime) {
        entity.getComponents().put(entity instanceof Tooltip ? VanillaComponent.ITEM : VanillaComponent.DECAY, String.valueOf(decayingTime));
        return this;
    }

    /**
     * Heals the diseases of the entity who eats it.
     *
     * @param death the death animation played when it's eaten.
     * @return this food table.
     * @see VanillaComponent#ITEM
     * @see VanillaComponent#DECAY
     */
    public FoodTable setDiseaseHealer(Death death) {
        entity.getComponents().put(VanillaComponent.HEALER, death.build());
        return this;
    }

    /**
     * Remove the hostility behaviour of the entity who eats it temporarily.
     *
     * @param time the duration of the pacifying effect.
     * @return this food table.
     */
    public FoodTable setPacifier(float time) {
        add = ";;0;;" + time;
        return this;
    }

    @Override
    public String build() {
        foodTable.forEach((type, build) -> {
            add(build); TABLE.get(type).add(entity);
        });
        return super.build() + add;
    }

    public boolean isEdibleAs(FoodTypeReference type) {
        return foodTable.containsKey(type);
    }

    public static void registerFoodType(FoodTypeReference reference) {
        if (!TABLE.containsKey(reference)) TABLE.put(reference, new HashSet<>());
    }
}
