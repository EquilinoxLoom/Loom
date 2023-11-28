package loom.entity.living;

import equilinox.classification.Classifiable;
import equilinox.ducktype.EatingAnimationReference;
import food.FoodSectionType;
import loom.entity.weaver.EntityComponent;
import loom.entity.weaver.PrintUtils;

import java.util.ArrayList;
import java.util.List;

public class Diet extends EntityComponent {
    public final int hunger;
    public final float hungerHour, radius;

    List<EatingAnimationReference> animations = new ArrayList<>();

    public Diet(int maxHunger, float hungerPerHour, float eatingRadius) {
        this.hunger = maxHunger;
        this.hungerHour = hungerPerHour;
        this.radius = eatingRadius;
    }

    @Override
    public String build() {
        return ";" + PrintUtils.print(";;", hunger, hungerHour, radius, PrintUtils.printArray(";",
                animations.toArray(new EatingAnimationReference[0]), animation -> String.valueOf(animation.ordinal()))
                + ";" + super.build());
    }

    public Diet addDietOption(Classifiable edible, FoodSectionType type, EatingAnimationReference animation) {
        animations.add(animation);
        add(edible.getClassification() + ";" + type.name() + ";" + new ArrayList<>(animations).indexOf(animation));
        return this;
    }
}
