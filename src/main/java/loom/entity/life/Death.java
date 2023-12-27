package loom.entity.life;

import loom.entity.Specie;
import loom.entity.system.Particles;
import loom.entity.weaver.EntityComponent;
import loom.entity.weaver.EntityPrint;

//TODO SUPPORT CUSTOM DEATH
@SuppressWarnings("unused")
public class Death extends EntityComponent {
    final String name;

    protected Death(String name, Object... args) {
        this.name = name;
        add(args);
    }

    @Override
    public String build() {
        return name.toUpperCase() + "_DEATH;" + super.build();
    }

    public static Death newFadeDeath(float timeToFade) {
        return new Death("FADE", timeToFade);
    }

    public static Death newParticleDeath(Particles particle) {
        return new Death(particle.udSpeed != 0 ? "PARTICLE" : "UP_DOWN", particle);
    }

    public static Death newFallDeath(float timeToFall, float timeToFade, int angle) {
        return new Death("FALL", timeToFall, timeToFade, angle, 0);
    }

    public static Death newFallDeathWithParticles(float timeToFall, float timeToFade, int angle, float timeToExplode,
                                                  boolean usesEntityColor, Particles particle, int[] stages) {
        return new Death("FALL", timeToFall, timeToFade, angle, 1, timeToExplode, usesEntityColor,
                particle.build().replace(";PARTICLE_DEATH;;", ""), EntityPrint.printArray(";", stages, String::valueOf));
    }

    public static Death newSpawnDeath(Specie specie, int minEntities, int maxEntities, boolean mustBeFullyGrown) {
        return new Death("SPAWN_DEATH", specie.getId() + ";" + minEntities + ";" + maxEntities
                + (mustBeFullyGrown ? ";1" : ";0"));
    }

    public static Death newFloaterDeath(float angle) {
        return new Death("FLOATER", angle);
    }
}

