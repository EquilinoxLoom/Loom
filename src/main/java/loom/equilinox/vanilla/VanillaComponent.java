package loom.equilinox.vanilla;

import componentArchitecture.Component;
import componentArchitecture.ComponentBundle;
import componentArchitecture.ComponentType;
import loom.component.PrintableComponent;
import loom.entity.Entity;
import loom.entity.Named;
import loom.entity.Specie;
import loom.entity.animal.*;
import loom.entity.food.Diet;
import loom.entity.food.Edible;
import loom.entity.food.FoodTable;
import loom.entity.life.Living;
import loom.entity.life.LivingEntity;
import loom.entity.plant.*;
import loom.entity.structure.Projectile;
import loom.entity.structure.StructureEntity;
import loom.entity.structure.Timeout;
import loom.entity.system.Particles;
import loom.entity.weaver.EquilinoxEntity;
import loom.equilinox.ducktype.SoundReference;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum VanillaComponent implements PrintableComponent {
    /**
     * Stores the size, position and rotation of the entity.
     * <p><b>Implementation:</b> Extend {@link EquilinoxEntity} class.</p>
     */
    TRANSFORM,

    /**
     * Stores the model data of the entity.
     * <p><b>Implementation:</b> Extend {@link EquilinoxEntity} class.</p>
     */
    MESH,

    /**
     * Spreads a biome in the specified range around its center.
     * <p><b>Implementation:</b> Extend {@link BiomeSpreader} interface.</p>
     */
    SPREADER,

    /**
     * Falls to the ground and starts burying itself in the ground, fading out, at the specified time.
     * <p><b>Implementation:</b> Extend {@link Edible} interface and invoke {@link FoodTable#setFoodItem(float)}
     * method in the {@link FoodTable} returned by {@link Edible#table()} function.</p>
     */
    DECAY,

    /**
     * Acts like a projectile, going forward until it reaches the ground, or an animal, exploding.
     * <p><b>Implementation:</b> Annotate with {@link Projectile}</p>
     */
    PROJECTILE,

    /**
     * Fades out after the specified time.
     * <p><b>Implementation:</b> Annotate with {@link Timeout}</p>
     */
    TIME_OUT,

    @Deprecated
    TONGUE_SHOOT,

    /**
     * One of its colors can be changed in Genetic Modification UI, being the first material of its object file model.
     * <p><b>Implementation:</b> Make {@link Entity#getMaterials()} not return an empty or null map.
     * For classes extending {@link EquilinoxEntity} or one of its subclasses, this can be achieved by
     * invoking {@link EquilinoxEntity#addColor(int, Color)} at its constructor.</p>
     */
    MATERIAL,

    /**
     * Works like {@link VanillaComponent#TIME_OUT}, but has a random variance.
     * <p><b>Implementation:</b> Extend {@link Edible} interface and invoking {@link FoodTable#setFoodItem(float)}
     * method in the {@link FoodTable} returned by {@link Edible#table()} function.</p>
     */
    ITEM,

    /**
     * Basically, is what makes it not a part of the scenario or of other entity.
     * When it's clicked, the name, description, icon and other core information of other components
     * like behaviours, genealogy, mutations, actions, environment requirements are displayed.
     * It can be bought in the game shop and placed in the equilinox world map and is detectable by other entities.
     * <p><b>Implementation:</b> Extend {@link LivingEntity} class.</p>
     * <p>Only clouds, fruits and other food items don't implement Information component.</p>
     */
    INFO(TRANSFORM),

    /**
     * Stores the model stage data and build points of the entity.
     * Also, can be built by a {@link VanillaComponent#BUILDER}.
     * <p><b>Implementation:</b> Extend {@link StructureEntity} class.</p>
     */
    BUILD(MESH),

    /**
     * Decomposes constantly, losing model stages. In order to keep existing, it must have its build points
     * consistently maintained by a {@link VanillaComponent#BUILDER}.
     * <p><b>Implementation:</b> Extend {@link StructureEntity} class and invoke {@link StructureEntity#setDecays(float)}.</p>
     * @see Entity#getModelPaths()
     */
    DECOMPOSE(MESH, BUILD),

    /**
     * Stores a variable as a container. After it's fully built, its additional model stages for this component
     * advance at the same pace the storage is fulfilled.
     * <p><b>Implementation:</b> Extend {@link StructureEntity} class and invoke {@link StructureEntity#setContainer(int, int)}.</p>
     * <p>The component is called hive because its only implementation in vanilla game is the bee hive, but it has nothing to do with bees.</p>
     */
    HIVE(MESH, BUILD),

    /**
     * Determines its lifespan, its breeding, evolution and well-being information,
     * its death animation and its average population in area.
     * <p><b>Implementation:</b> Extend {@link Living} interface.</p>
     * <p>As all entities that have life component also have growth component, they are both implemented via {@link Living} interface.</p>
     * @see loom.entity.life
     */
    LIFE(TRANSFORM, INFO),

    /**
     * Plays a sound in a loop.
     * <p><b>Implementation:</b> Extend {@link EquilinoxEntity} and invoke
     * {@link EquilinoxEntity#setLoopingSounder(float, float, SoundReference)} method at its constructor.</p>
     */
    SOUND_LOOPER(TRANSFORM),

    /**
     * Determines its model growth stages through its lifetime and how much time it takes to grow from one stage to another.
     * <p><b>Implementation:</b> Extend {@link Living} interface.</p>
     * <p>As all entities that have life component also have growth component, they are both implemented via {@link Living} interface.</p>
     */
    GROWTH(TRANSFORM, LIFE, INFO),

    BLOOM(TRANSFORM, MESH, LIFE, GROWTH, INFO),
    FLY_TRAP(TRANSFORM, MESH, LIFE, GROWTH, INFO),

    /**
     * Becomes edible periodically when it's fully grown.
     * <p><b>Implementation:</b> Extend {@link FruitProducer} interface.</p>
     */
    FRUITER(TRANSFORM, MESH, LIFE, GROWTH, INFO),

    /**
     * Moves around its roaming range dynamically.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} and pass a {@link Movement} as a parameter of its constructor.</p>
     * <p>There are many different ways of moving in equilinox, described by the {@link Movement} class.</p>
     * @see AnimalEntity#AnimalEntity(String, Movement)
     */
    MOVEMENT(TRANSFORM, MESH, LIFE, GROWTH, INFO),

    /**
     * Has different age-dependent behaviours.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} and pass a {@link Movement} as a parameter of its constructor. </p>
     * <p>The vast majority of animals have a simple ai: follow parents when young and wander around when adults.
     * Some animals have special age-dependent behaviours in order to make their egg stage don't move.
     * As it's related to movement, it's automatically chosen by Movement class based on the entity properties.</p>
     * @see AnimalEntity#AnimalEntity(String, Movement)
     */
    AI(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT),

    /**
     * Can be eaten by other entities.
     * <p><b>Implementation:</b> Extend {@link Edible} interface.</p>
     * <p>There are many different types of food, documented by the enum {@link VanillaFoodType}.</p>
     * @see FoodTable
     */
    FOOD,

    /**
     * Spawns entities around it, that randomly fall.
     * <p><b>Implementation:</b> Extend {@link FruitFall} interface.</p>
     */
    FRUIT_FALL(TRANSFORM, MESH, LIFE, GROWTH, INFO, FRUITER),

    /**
     * Floats up and down, simulating the movement of water waves.
     * <p><b>Implementation:</b> Annotate with {@link Floater}.</p>
     * <p>The component is called lily because its only implementation in vanilla game is the water lily.</p>
     */
    LILY(TRANSFORM),

    /**
     * Rotates its y-axis based on sun position.
     * <p><b>Implementation:</b> Annotate with {@link Sunflower}.</p>
     */
    SUN_FACER(TRANSFORM),

    /**
     * Spawns barks and twigs when destroyed by {@link DenBuilder Den Builders}.
     * <p><b>Implementation:</b> Extend {@link WoodProducer} interface.</p>
     */
    WOOD,

    /**
     * Builds a structure if none is available, otherwise, visits it periodically.
     * <p><b>Implementation:</b> Extend {@link Builder} interface.</p>
     */
    BUILDER(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Eats other entities, or parts of other entities.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} and invoke {@link AnimalEntity#setDiet(Diet)}
     * at its constructor, passing a {@link Diet} as argument.</p>
     */
    EATING(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Hunts {@link VanillaClassification#SMALL_FISH small fish}.
     * <p><b>Implementation:</b> Extend {@link Hunter} interface.
     * The component is automatically added if the entity only goes underwater and does not extend {@link WalkingHunter}.</p>
     */
    FISH_HUNT(TRANSFORM, MESH, LIFE, GROWTH, INFO),
    HEALER,

    /**
     * Randomly panics and run in circles.
     * <p><b>Implementation:</b> Annotate with {@link Panic} interface.</p>
     */
    PANIC(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Passively damages nearby animals when adult.
     * <p><b>Implementation:</b> Annotate with {@link Stinger} interface.</p>
     */
    STINGING(TRANSFORM, LIFE, GROWTH, INFO),

    /**
     * Charges at fruit trees, dropping all its fruits.
     * <p><b>Implementation:</b> Annotate with {@link TreeCharger} interface.</p>
     */
    CHARGE(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Builds nests and only reproduces inside it.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} class and
     * invoke {@link AnimalEntity#setBreedSite(Specie, int, boolean)}.</p>
     */
    NESTING(TRANSFORM, INFO),

    /**
     * Faces other entities with this component and advances a model stage while doing so.
     * <p><b>Implementation:</b> Annotate with {@link Peacock} interface.</p>
     */
    PEACOCK(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Has slots in which a {@link VanillaComponent#PERCHER} can rest.
     * <p><b>Implementation:</b> Extend {@link PlantEntity} class and invoke {@link PlantEntity#setPerchingPositions(Vector3f[])}.</p>
     */
    PERCH(TRANSFORM, LIFE, INFO),

    /**
     * Stores whether the entity is perching in a {@link VanillaComponent#PERCH} slot.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} class and invoke {@link AnimalEntity#setPerches(boolean)}.</p>
     */
    PERCHER,

    /**
     * Can pick objects up.
     * <p><b>Implementation:</b> Extend {@link Holder} interface.</p>
     */
    EQUIP(TRANSFORM, LIFE, INFO),

    /**
     * Cuts trees, picks up its twigs and builds a den when adult.
     * <p><b>Implementation:</b> Annotate with {@link DenBuilder}.</p>
     */
    BEAVER(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, EQUIP, AI),

    /**
     * Emits particles.
     * <p><b>Implementation:</b> Extend {@link EquilinoxEntity} and invoke {@link EquilinoxEntity#setParticles(Particles, float, int[], boolean)}.</p>
     */
    PARTICLES(TRANSFORM, MESH),

    /**
     * Hunts {@link VanillaClassification#SMALL_HERBIVORE small herbivores} while flying.
     * <p><b>Implementation:</b> Extend {@link Hunter} interface.
     * The component is automatically added if the entity has an egg stage and does not extend {@link WalkingHunter}.</p>
     */
    BIRD_HUNT(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Randomly spits on nearby animals.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} class and invoke {@link AnimalEntity#setSpits(float, float, float)}.</p>
     */
    SPITTING(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Fills specified nearby containers.
     * <p><b>Implementation:</b> Extend {@link ContainerFiller} interface.</p>
     * <p>The component is called bee because its only implementation in vanilla game is the bee, but it has nothing to do with bees.</p>
     * @see VanillaComponent#HIVE
     */
    BEE(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, BUILDER),
    FLINGING(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Is randomly named.
     * <p><b>Implementation:</b> Annotate with {@link Named}.</p>
     */
    NAME,

    /**
     * Stores sound emission data.
     * <p><b>Implementation:</b> Extend {@link EquilinoxEntity} class and invoke {@link EquilinoxEntity#setRandomSounder(float, float, int, SoundReference...)}.</p>
     */
    SOUND(TRANSFORM),

    /**
     * Randomly emits a sound.
     * <p><b>Implementation:</b> Extend {@link EquilinoxEntity} class and invoke {@link EquilinoxEntity#setRandomSounder(float, float, int, SoundReference...)}.</p>
     */
    RANDOM_SOUNDER(TRANSFORM, SOUND),

    /**
     * Stores fight data.
     * <p><b>Implementation:</b> Extend {@link Fighter} interface.</p>
     */
    FIGHT(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Hunts specified preys on land.
     * <p><b>Implementation:</b> Extend {@link WalkingHunter} interface.</p>
     */
    HUNT(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, EQUIP, AI, FIGHT),

    /**
     * Sleeps.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} class and invoke {@link AnimalEntity#setSleeps(float, float, float, float)}.</p>
     */
    SLEEP(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Flees when is attacked or is being hunted.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} class and invoke {@link AnimalEntity#setFlees(float)}.</p>
     */
    FLEE(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),

    /**
     * Creates burrows that can serve as hide spot for it.
     * <p><b>Implementation:</b> Extend {@link AnimalEntity} and invoke {@link AnimalEntity#setHoleHides(float)} .</p>
     */
    BURROW(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI, FLEE),

    /**
     * Drops an entity when killed.
     * <p><b>Implementation:</b> Extend {@link LivingEntity} and invoke {@link LivingEntity#setDrops}.</p>
     */
    DROP,

    /**
     * Attacks nearby entities.
     * <p><b>Implementation:</b> Extend {@link Territorial} interface.</p>
     */
    HOSTILE(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, EQUIP, AI, FIGHT);

    /**
     * List of components loaded at this component creation.
     */
    private final Set<PrintableComponent> requirements;

    VanillaComponent(VanillaComponent... requirements) {
        this.requirements = new HashSet<>(Arrays.asList(requirements));
    }

    public Set<PrintableComponent> getRequirements() {
        return requirements;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(ComponentBundle bundle) {
        return (T) bundle.getComponent(ComponentType.valueOf(name()));
    }
}
