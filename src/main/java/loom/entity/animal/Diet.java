package loom.entity.animal;

import equilinox.classification.Classifiable;
import food.FoodSectionType;
import loom.entity.Entity;
import loom.entity.weaver.EntityComponent;
import loom.entity.weaver.Printable;

import java.util.ArrayList;
import java.util.TreeSet;

public class Diet extends EntityComponent {
    public final int hunger;
    public final float hungerHour, radius;

    TreeSet<Animation> animations = new TreeSet<>();

    public Diet(int maxHunger, float hungerPerHour, float eatingRadius) {
        this.hunger = maxHunger;
        this.hungerHour = hungerPerHour;
        this.radius = eatingRadius;
    }

    @Override
    public String build() {
        return ";" + Printable.print(";;", hunger, hungerHour, radius, Printable.printArray(";",
                animations.toArray(new Animation[0]), animation -> String.valueOf(animation.ordinal()))
                + ";" + super.build());
    }

    public Diet addDietOption(Entity entity, FoodSectionType type, Animation animation) {
        if (entity.getFoodInfo().containsKey(type)) {
            animations.add(animation);
            add(entity.getClassification() + entity.getId() + ";" + type.name() + ";" +
                    new ArrayList<>(animations).indexOf(animation));
        }
        return this;
    }

    /**
     * Equilinox diet system is tough in the sense of being overcomplicated.
     * The food {@code type} must match the list of types present in the specified edible.
     * <dl>
     *     <dt>{@link food.FoodSectionType#WHOLE WHOLE}</dt>
     *     <dd>
     *         The entity is devoured when eaten, dying in the process.
     *         If it's living, it can only be eaten when it's halfway grown.
     *         All small and weird fishes, most fruits and small plants
     *         (except from seed, carnivore plant, lily and carrot)
     *         and also nut, bark, potato, leafy bush, yucca can be eaten as WHOLE.
     *     </dd>
     *     <dt>{@link food.FoodSectionType#FRUIT FRUIT}</dt>
     *     <dd>
     *         The entity must be a Fruit Producer
     *         like fruit bushes and prickly pear, when it's eaten it returns a fruit stage
     *     </dd>
     *     <dt>{@link food.FoodSectionType#UPROOT UPROOT}</dt>
     *     <dd>Marked as @Deprecated, if used it will crash the game</dd>
     *     <dt>{@link food.FoodSectionType#SAMPLE SAMPLE}</dt>
     *     <dd>Most flowers (except from carnivore plant) and also small cactus, flowery grass</dd>
     *     <dt>{@link food.FoodSectionType#TO_SHARE TO SHARE}</dt>
     *     <dd>Only meat is TO SHARE and it has many portions, when they end the entity dies</dd>
     *     <dt>{@link food.FoodSectionType#HONEY HONEY}</dt>
     *     <dd>
     *         When the entity is eaten, it has its honey count decreased.
     *         It can only be eaten when the entity is fully built.
     *         The only entity eaten as a HIVE is the bee hive
     *     </dd>
     *     <dt>{@link food.FoodSectionType#ROOT_VEG ROOT VEG}</dt>
     *     <dd>
     *         Works as WHOLE, but it can only be eaten if there are at least three of it in the range and
     *         if the entity is at least three-quarters grown. Additionally, if the entity has Spawn Death,
     *         when eaten it spawns an amount of entities from to 4-9 multiplied by a float trait.
     *         All root vegetables are eaten as ROOT VEG.
     *     </dd>
     * </dl>
     */
    public Diet addDietOption(Classifiable edible, FoodSectionType type, Animation animation) {
        animations.add(animation);
        add(edible.getClassification() + ";" + type.name() + ";" + new ArrayList<>(animations).indexOf(animation));
        return this;
    }

    public enum Animation {SIMPLE, THROWING, DIGGING, DIVING, INSTANT}
}
