import food.FoodSectionType;
import loom.Util.VanillaColors;
import loom.entity.animal.AnimalEntity;
import loom.entity.animal.Diet;
import loom.entity.animal.Movement;
import loom.entity.life.WellBeing;
import loom.entity.other.Named;
import equilinox.classification.Classifiable;
import equilinox.classification.Family;
import equilinox.classification.Order;
import equilinox.classification.Phylum;

import java.awt.*;

import static equilinox.Environment.*;

@Named
public class SheepEntity extends AnimalEntity {
    public SheepEntity() {
        super("Sheep", 2001, Movement.newBouncingMovement(0.4f, 180, 2), null, new WellBeing()
                .addSuitableBiomeFactor(0.5f, false, 100, GRASSLAND, FOREST, SNOW, SWAMP, LUSH, WOODLAND)
                .addLikedSpeciesFactor(0.25f, Phylum.TREE));
        addColor(2000, Color.WHITE);
        addColor(10000, VanillaColors.LIGHT_BROWN.color);
        addColor(6000, VanillaColors.GREY.color);
        addColor(80000, VanillaColors.BEIGE.color);
        addColor(500000, VanillaColors.LILAC.color);
        addColor(1500000, VanillaColors.CYAN.color);
        addColor(2000000, VanillaColors.RED.color);
        addColor(2000000, VanillaColors.LIGHT_PINK.color);
        addColor(2000000, VanillaColors.GOLD.color);
        addColor(5000000, VanillaColors.DARK_PURPLE.color);
        setPatrolAI(10, 20);
        setDiet(new Diet(50, 3, 0.2f)
                .addDietOption(Family.HIVE, FoodSectionType.HONEY, Diet.Animation.SIMPLE)
                .addDietOption(Phylum.FRUIT, FoodSectionType.WHOLE, Diet.Animation.SIMPLE)
                .addDietOption(Order.FRUIT_BUSH, FoodSectionType.FRUIT, Diet.Animation.SIMPLE)
                .addDietOption(Order.GRASSES, FoodSectionType.WHOLE, Diet.Animation.SIMPLE));
        setRandomSounder(15, 30, 10, "sheepBaa", "sheepBaa2");
        setSleeps(0, 2, 3, 5);
        setFlees(3);
        setDrops(Family.MEAT);
    }

    @Override
    public boolean goesUnderwater() {
        return false;
    }

    @Override
    public boolean goesOverwater() {
        return true;
    }

    @Override
    public String[] getModelPaths() {
        return new String[0];
    }

    @Override
    public Classifiable getLineage() {
        return Order.MEDIUM_HERBIVORE;
    }

    @Override
    public float population() {
        return 4.3f;
    }

    @Override
    public float lifespan() {
        return 43;
    }

    @Override
    public float breedingMaturity() {
        return 21.5f;
    }

    @Override
    public float breedingCooldown() {
        return 15.551813f;
    }

    @Override
    public float growthTime() {
        return 43f / 3f;
    }

    @Override
    public String description() {
        return "This cute, fluffy animal loves to spend its time roaming around and eating grass, or fruit if it " +
                "can find any! The sheep also likes to sleep at night and is often hunted by predators. This is one " +
                "of the most basic creatures in Equilinox.";
    }

    @Override
    public int price() {
        return 750;
    }

    @Override
    public int dpPerMin() {
        return 52;
    }

    @Override
    public int range() {
        return 2;
    }

    @Override
    public String sound() {
        return "sheepBaa2";
    }

    @Override
    public Color getSecondNaturalColor() {
        return VanillaColors.BLACK.color;
    }
}
