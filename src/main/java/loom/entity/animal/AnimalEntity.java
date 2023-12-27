package loom.entity.animal;

import loom.entity.Classifiable;
import loom.entity.Entity;
import loom.entity.Specie;
import loom.entity.food.Diet;
import loom.entity.life.Death;
import loom.entity.life.LivingEntity;
import loom.entity.weaver.EntityPrint;
import loom.equilinox.vanilla.VanillaComponent;

import javax.annotation.Nonnull;

/**
 * In Equilinox, what determines an animal is: a living entity which moves and has some
 * sort of artificial intelligence. That said, in order to inherently implement such features
 * Loom offers this class as a shortcut. All animals start with a base AI,
 * determined by whether it swims, walks, do both, or fly and whether it has an egg stage.
 * @see Movement#getMovementAi(Entity)
 */
@SuppressWarnings("unused")
public abstract class AnimalEntity extends LivingEntity {
    protected final Movement movement;

    public AnimalEntity(@Nonnull String name, @Nonnull Movement movement) {
        super(name, Death.newFallDeath(1.5f, 2.5f, 60));
        this.movement = movement;
        this.components.put(VanillaComponent.MOVEMENT, movement.build().substring(1));
        this.components.put(VanillaComponent.AI, movement.getMovementAi(this));
    }

    public final boolean dynamic() {
        return true;
    }

    public void setSleeps(float minStartHour, float maxStartHour, float minEndHour, float maxEndHour) {
        components.put(VanillaComponent.SLEEP, ";" + EntityPrint.print(";;", minStartHour, maxStartHour, minEndHour,
                maxEndHour));
    }

    public void setFlees(float range) {
        components.put(VanillaComponent.FLEE, ";" + EntityPrint.print(";;", range, goesOverwater(), goesUnderwater()));
    }

    public void setHides(float range, Classifiable hidingSpot) {
        components.put(VanillaComponent.FLEE, ";" + EntityPrint.print(";;", range, goesOverwater(), goesUnderwater(),
                hidingSpot.getClassification()));
    }

    public void setShellHides(float range) {
        components.put(VanillaComponent.FLEE, "TORTOISE;" + range);
    }

    public void setHoleHides(float range) {
        components.put(VanillaComponent.FLEE, "MEERKAT;" + range);
        components.put(VanillaComponent.BURROW, "");
    }

    public void setDiet(Diet diet) {
        diet.setEntity(this);
        components.put(VanillaComponent.EATING, diet.build());
    }

    /**
     * For flying birds it's a nest, for beavers a den.
     * @param siteStage least stage the site must be to support breeding.
     */
    public void setBreedSite(Specie site, int siteStage, boolean decreasesSiteStage) {
        components.put(VanillaComponent.NESTING, ";" + EntityPrint.print(";;", site.getClassification(),
                siteStage, decreasesSiteStage));
    }

    /**Params are the spit positions*/
    public void setSpits(float x, float y, float z) {
        components.put(VanillaComponent.SPITTING, ";" + x + ";" + y + ";" + z);
    }

    public void setInsectCatcher(float minCooldown, float maxCooldown) {
        components.put(VanillaComponent.FLINGING, ";" + minCooldown + ";;" + maxCooldown);
    }
}
