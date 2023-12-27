package loom.entity.food;

import loom.entity.Classifiable;
import loom.entity.Entity;
import loom.entity.weaver.EntityComponent;
import loom.entity.weaver.EntityPrint;
import loom.equilinox.ducktype.EatingAnimationReference;
import loom.equilinox.ducktype.FoodTypeReference;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

import java.util.ArrayList;
import java.util.List;

import static loom.equilinox.vanilla.VanillaComponent.AI;

public class Diet extends EntityComponent {
    public final int hunger;
    public final float hungerHour, radius;

    final List<EatingAnimationReference> animations = new ArrayList<>();

    private boolean runs = false;

    private Entity entity;

    public Diet(int maxHunger, float hungerPerHour, float eatingRadius) {
        this.hunger = maxHunger;
        this.hungerHour = hungerPerHour;
        this.radius = eatingRadius;
    }

    @Override
    public String build() {
        String add = "";

        boolean ai = entity.hasComponent(AI);
        String aiComp = entity.getComponents().get(AI);

        boolean fish = ai && aiComp.equals("SWIM");
        boolean bird = ai && aiComp.equals("BIRD");
        if (runs || entity.hasEggStage() || (fish || bird))
            add = ";;" + (runs ? 1 : 0) + ";;" + (bird ? 2 : (fish ? 1 : 0)) + (entity.hasEggStage() ? ";;1" : "");

        return ";" + EntityPrint.print(";;",
                hunger, hungerHour, radius,
                animations.isEmpty()
                        ? "0"
                        : EntityPrint.printArray(";",
                        animations.toArray(new EatingAnimationReference[0]),
                        animation -> String.valueOf(animation.ordinal())),
                EntityPrint.printArray(";", print.toArray(new String[0]), s -> s) + add);
    }

    public Diet setRunsToFood(boolean b) {
        this.runs = b;
        return this;
    }

    public Diet addDietOption(Classifiable edible, FoodTypeReference type, EatingAnimationReference animation) {
        if (edible instanceof Edible) {
            if (!((Edible) edible).table().isEdibleAs(type)) {
                Log.warn(LogCategory.LOG, ((Edible) edible).name() + " cannot be eaten as " + type.name() + ".");
            }
        } else {
            animations.add(animation);
            add(edible.getClassification() + ";" + type.name() + ";" + animations.indexOf(animation));
        }
        return this;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
