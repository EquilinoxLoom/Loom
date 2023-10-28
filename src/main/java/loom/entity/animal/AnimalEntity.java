package loom.entity.animal;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import loom.entity.life.LivingEntity;
import loom.entity.weaver.Printable;
import loom.entity.life.Death;
import loom.entity.life.Evolution;
import loom.entity.life.WellBeing;
import snowball.embroider.entity.life.*;
import equilinox.VanillaComponent;
import equilinox.classification.Classifiable;
import equilinox.classification.Specie;

@SuppressWarnings("unused")
public abstract class AnimalEntity extends LivingEntity {
    public AnimalEntity(String name, int id,
                        @NotNull Movement movement,
                        @Nullable Evolution evolution,
                        @NotNull WellBeing satisfaction) {
        super(name, id, Death.newFallDeath(1.5f, 2.5f, 60), evolution, satisfaction);
        this.components.put(VanillaComponent.MOVEMENT, movement.build());
        this.components.put(VanillaComponent.AI, goesOverwater() ? (goesUnderwater() ? "PATROL_WITH_SWIM" : "PATROL")
                : "SWIM");
    }

    public abstract boolean hasEggStage();

    public final boolean dynamic() {
        return true;
    }

    /**Standard values are: min = 5; max = 10*/
    public void setPatrolAI(float minIdleTime, float maxIdleTime) {
        this.components.put(VanillaComponent.AI, Printable.print(";;", "PATROL"
                + (goesUnderwater() ? "_WITH_SWIM" : ""), minIdleTime, maxIdleTime));
    }

    public void setFlyAI() {
        this.components.put(VanillaComponent.AI, "BIRD");
    }

    public void setFlyAI(float circleRotation, float circleMinTime) {
        this.components.put(VanillaComponent.AI, Printable.print(";;", "BIRD", circleRotation, circleMinTime));
    }

    public void setWalkingBirdAI(float minIdleTime, float maxIdleTime) {
        this.components.put(VanillaComponent.AI, Printable.print(";;", "WALKING_BIRD", minIdleTime, maxIdleTime,
                goesUnderwater()));
    }

    public void setBeeAI() {
        this.components.put(VanillaComponent.AI, "BEE");
    }

    public void setMeerkatAI() {
        this.components.put(VanillaComponent.AI, "MEERKAT");
    }

    public void setMeerkatAI(float minIdleTime, float maxIdleTime) {
        this.components.put(VanillaComponent.AI, Printable.print(";;", "MEERKAT", minIdleTime, maxIdleTime));
    }

    public void setDolphinAI() {
        this.components.put(VanillaComponent.AI, "DOLPHIN");
    }

    public void setSleeps(float minStartHour, float maxStartHour, float minEndHour, float maxEndHour) {
        components.put(VanillaComponent.SLEEP, ";" + Printable.print(";;", minStartHour, maxStartHour, minEndHour,
                maxEndHour));
    }

    public void setFlees(float range) {
        components.put(VanillaComponent.FLEE, ";" + Printable.print(";;", range, goesOverwater(), goesUnderwater()));
    }

    public void setHides(float range, Classifiable hidingSpot) {
        components.put(VanillaComponent.FLEE, ";" + Printable.print(";;", range, goesOverwater(), goesUnderwater(),
                hidingSpot.getClassification()));
    }

    public void setShellHides(float range) {
        components.put(VanillaComponent.FLEE, "TORTOISE;" + range);
    }

    public void setHoleHides(float range) {
        components.put(VanillaComponent.FLEE, "MEERKAT;" + range);
    }

    public void setDrops(Specie item) {
        components.put(VanillaComponent.DROP, ";" + item.getId());
    }

    public void setDiet(Diet diet) {
        components.put(VanillaComponent.EATING, diet.build());
    }
}
