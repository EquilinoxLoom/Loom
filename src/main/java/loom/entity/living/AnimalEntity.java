package loom.entity.living;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import equilinox.VanillaComponent;
import equilinox.classification.Classifiable;
import equilinox.classification.Specie;
import loom.entity.weaver.PrintUtils;

@SuppressWarnings("unused")
public abstract class AnimalEntity extends LivingEntity {
    protected final int id;

    public AnimalEntity(String name, int id,
                        @NotNull Movement movement,
                        @Nullable Evolution evolution,
                        @NotNull WellBeing satisfaction) {
        super(name, id, Death.newFallDeath(1.5f, 2.5f, 60), evolution, satisfaction);
        this.components.put(VanillaComponent.MOVEMENT, movement.build());
        this.components.put(VanillaComponent.AI, goesOverwater() ? (goesUnderwater() ? "PATROL_WITH_SWIM" : "PATROL")
                : "SWIM");
        this.id = id;
    }

    public abstract boolean hasEggStage();

    public final boolean dynamic() {
        return true;
    }

    /**
     * Standard values are: min = 5; max = 10
     */
    public void setPatrolAI(float minIdleTime, float maxIdleTime) {
        this.components.put(VanillaComponent.AI, PrintUtils.print(";;", "PATROL"
                + (goesUnderwater() ? "_WITH_SWIM" : ""), minIdleTime, maxIdleTime));
    }

    public void setFlyAI() {
        this.components.put(VanillaComponent.AI, "BIRD");
    }

    public void setFlyAI(float circleRotation, float circleMinTime) {
        this.components.put(VanillaComponent.AI, PrintUtils.print(";;", "BIRD", circleRotation, circleMinTime));
    }

    public void setWalkingBirdAI(float minIdleTime, float maxIdleTime) {
        this.components.put(VanillaComponent.AI, PrintUtils.print(";;", "WALKING_BIRD", minIdleTime, maxIdleTime,
                goesUnderwater()));
    }

    public void setBeeAI() {
        this.components.put(VanillaComponent.AI, "BEE");
    }

    public void setMeerkatAI() {
        this.components.put(VanillaComponent.AI, "MEERKAT");
    }

    public void setMeerkatAI(float minIdleTime, float maxIdleTime) {
        this.components.put(VanillaComponent.AI, PrintUtils.print(";;", "MEERKAT", minIdleTime, maxIdleTime));
    }

    public void setDolphinAI() {
        this.components.put(VanillaComponent.AI, "DOLPHIN");
    }

    public void setSleeps(float minStartHour, float maxStartHour, float minEndHour, float maxEndHour) {
        components.put(VanillaComponent.SLEEP, ";" + PrintUtils.print(";;", minStartHour, maxStartHour, minEndHour,
                maxEndHour));
    }

    public void setFlees(float range) {
        components.put(VanillaComponent.FLEE, ";" + PrintUtils.print(";;", range, goesOverwater(), goesUnderwater()));
    }

    /**Guinea pigs panic*/
    public void setPanic(float range) {
        components.put(VanillaComponent.PANIC, "");
    }

    public void setHides(float range, Classifiable hidingSpot) {
        components.put(VanillaComponent.FLEE, ";" + PrintUtils.print(";;", range, goesOverwater(), goesUnderwater(),
                hidingSpot.getClassification()));
    }

    public void setShellHides(float range) {
        components.put(VanillaComponent.FLEE, "TORTOISE;" + range);
    }

    public void setHoleHides(float range) {
        components.put(VanillaComponent.FLEE, "MEERKAT;" + range);
        components.put(VanillaComponent.BURROW, "");
    }

    public void setDrops(Specie item) {
        components.put(VanillaComponent.DROP, ";" + item.getId());
    }

    public void setDiet(Diet diet) {
        components.put(VanillaComponent.EATING, diet.build());
    }

    /**
     * For flying birds it's a nest, for beavers a den.
     * @param siteStage least stage the site must be to support breeding.
     */
    public void setBreedSite(Specie site, int siteStage, boolean decreasesSiteStage) {
        components.put(VanillaComponent.NESTING, ";" + PrintUtils.print(";;", site.getClassification(),
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
