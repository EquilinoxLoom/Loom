package equilinox;

import componentArchitecture.ComponentType;
import componentArchitecture.Requirement;
import death.DeathAICreator;
import environment.EnvironmentComponentLoader;
import loom.entity.living.Movement;
import loom.entity.living.Death;
import loom.entity.living.Evolution;
import loom.entity.living.WellBeing;
import loom.entity.other.Particle;
import org.lwjgl.util.vector.Vector3f;
import particleComponent.ParticleSystemLoader;
import particleSpawns.CircleSpawn;
import particleSpawns.LineSpawn;
import particleSpawns.PointSpawn;
import particleSpawns.SphereSpawn;
import utils.CSVReader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum VanillaLoader {
    DEATH(DeathAICreator::loadDeathAi, Death.class),
    PARTICLE(ParticleSystemLoader::loadParticleSystem, Particle.class),
    EVOLUTION(reader -> {
        int count = reader.getNextLabelInt();
        List<Requirement> requirements = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Requirement req = ComponentType.valueOf(reader.getNextString()).loadRequirement(reader);
            requirements.add(req);
        }
        return requirements;
    }, Evolution.class),
    WELL_BEING(EnvironmentComponentLoader::loadEnviroBlueprint, WellBeing.class),
    MOVEMENT(reader -> ComponentType.MOVEMENT.loadComponent(reader, null), Movement.class),
    DIET(reader -> ComponentType.FOOD.loadComponent(reader, null), Movement.class),
    SPAWN_PATTERN(reader -> {
        int spawnId = reader.getNextInt();
        if (spawnId == 0) return new PointSpawn();
        if (spawnId == 1) {
            float radius = reader.getNextFloat();
            return new SphereSpawn(radius);
        }
        if (spawnId == 2) {
            float length = reader.getNextFloat();
            Vector3f direction = reader.getNextVector();
            return new LineSpawn(length, direction);
        }
        if (spawnId == 3) {
            float radius = reader.getNextFloat();
            Vector3f normal = reader.getNextVector();
            return new CircleSpawn(normal, radius);
        }
        return new PointSpawn();
    }, Particle.SpawnPattern.class);

    private static final Map<Class<?>, Function<CSVReader, ?>> FUNCTIONS =
            new HashMap<Class<?>, Function<CSVReader, ?>>() {{ putAll(Arrays.stream(VanillaLoader.values())
                    .collect(Collectors.toMap(VanillaLoader::getClazz, VanillaLoader::getFunction))); }};

    private final Function<CSVReader, ?> function;
    private final Class<?> clazz;

    VanillaLoader(Function<CSVReader, ?> function, Class<?> clazz) {
        this.function = function;
        this.clazz = clazz;
    }

    public Function<CSVReader, ?> getFunction() {
        return function;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static Function<CSVReader, ?> getFunctionByClass(Class<?> clazz) {
        return FUNCTIONS.get(clazz);
    }
}
