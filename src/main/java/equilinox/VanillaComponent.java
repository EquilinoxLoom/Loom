package equilinox;

import componentArchitecture.Component;
import componentArchitecture.ComponentBundle;
import componentArchitecture.ComponentType;
import loom.component.ComponentPrint;

import java.util.Arrays;
import java.util.List;

public enum VanillaComponent implements ComponentPrint {
    TRANSFORM,
    MESH,
    SPREADER,
    DECAY,
    PROJECTILE,
    TIME_OUT,
    TONGUE_SHOOT,
    MATERIAL,
    ITEM,
    INFO(TRANSFORM),
    BUILD(MESH),
    DECOMPOSE(MESH, BUILD),
    HIVE(MESH, BUILD),
    LIFE(TRANSFORM, INFO),
    SOUND_LOOPER(TRANSFORM),
    GROWTH(TRANSFORM, LIFE, INFO),
    BLOOM(TRANSFORM, MESH, LIFE, GROWTH, INFO),
    FLY_TRAP(TRANSFORM, MESH, LIFE, GROWTH, INFO),
    FRUITER(TRANSFORM, MESH, LIFE, GROWTH, INFO),
    MOVEMENT(TRANSFORM, MESH, LIFE, GROWTH, INFO),
    AI(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT),
    FOOD,
    FRUIT_FALL(TRANSFORM, MESH, LIFE, GROWTH, INFO, FRUITER),
    LILY(TRANSFORM),
    SUN_FACER(TRANSFORM),
    WOOD,
    BUILDER(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT),
    EATING(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    FISH_HUNT(TRANSFORM, MESH, LIFE, GROWTH, INFO),
    HEALER,
    PANIC(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    STINGING(TRANSFORM, LIFE, GROWTH, INFO),
    CHARGE(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    NESTING(TRANSFORM, INFO),
    PEACOCK(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    PERCH(TRANSFORM, LIFE, INFO),
    PERCHER,
    EQUIP(TRANSFORM, LIFE, INFO),
    BEAVER(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, EQUIP, AI),
    PARTICLES(TRANSFORM, MESH),
    BIRD_HUNT(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    SPITTING(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    BEE(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT),
    FLINGING(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    NAME,
    SOUND(TRANSFORM),
    RANDOM_SOUNDER(TRANSFORM, SOUND),
    FIGHT(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    HUNT(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, EQUIP, AI, FIGHT),
    SLEEP(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    FLEE(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI),
    BURROW(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, AI, FLEE),
    DROP,
    HOSTILE(TRANSFORM, MESH, LIFE, GROWTH, INFO, MOVEMENT, EQUIP, AI, FIGHT);

    private final List<ComponentPrint> requirements;

    VanillaComponent(VanillaComponent... requirements) {
        this.requirements = Arrays.asList(requirements);
    }

    public List<ComponentPrint> getRequirements() {
        return requirements;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(ComponentBundle bundle) {
        return (T) bundle.getComponent(ComponentType.valueOf(name()));
    }
}
