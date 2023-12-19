package equilinoxmodkit.mixins.loom;

import death.DeathAICreator;
import death.DeathAiBlueprint;
import food.*;
import languages.GameText;
import loom.LoomMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import utils.CSVReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Mixin(value = FoodCompLoader.class, remap = false)
public class MixinFoodCompLoader {
    /**
     * @author Sandáliaball
     * @reason Name isn't at GameText file, so they are explicitly added
     */
    @Overwrite
    private FoodSectionBlueprint[] loadSections(int count, CSVReader reader) {
        FoodSectionBlueprint[] sections = new FoodSectionBlueprint[count];
        try {
            for (int i = 0; i < count; i++) {
                String name;
                String str = reader.getNextString();
                if (str.startsWith(LoomMod.MOD_POINTER)) name = str.replace(LoomMod.MOD_POINTER, "");
                else name = GameText.getText(Integer.parseInt(str));
                int foodPoints = reader.getNextInt();
                FoodSectionType type = FoodSectionType.valueOf(reader.getNextString());
                Constructor<WholeFoodSectionBlueprint> whole = WholeFoodSectionBlueprint.class
                        .getDeclaredConstructor(String.class, int.class, DeathAiBlueprint.class);
                whole.setAccessible(true);
                if (type == FoodSectionType.WHOLE) {
                    DeathAiBlueprint deathAi = DeathAICreator.loadDeathAi(reader);
                    sections[i] = whole.newInstance(name, foodPoints, deathAi);
                } else if (type == FoodSectionType.FRUIT) {
                    Constructor<FruitSectionBlueprint> constructor = FruitSectionBlueprint.class
                            .getDeclaredConstructor(String.class, int.class);
                    whole.setAccessible(true);
                    sections[i] = constructor.newInstance(name, foodPoints);
                } else if (type == FoodSectionType.SAMPLE) {
                    Constructor<SampleSectionBlueprint> constructor = SampleSectionBlueprint.class
                            .getDeclaredConstructor(String.class, int.class);
                    whole.setAccessible(true);
                    sections[i] = constructor.newInstance(name, foodPoints);
                } else if (type == FoodSectionType.TO_SHARE) {
                    int portions = reader.getNextLabelInt();
                    Constructor<FoodToShareBlueprint> constructor = FoodToShareBlueprint.class
                            .getDeclaredConstructor(String.class, int.class, int.class);
                    whole.setAccessible(true);
                    sections[i] = constructor.newInstance(name, foodPoints, portions);
                } else if (type == FoodSectionType.HONEY) {
                    Constructor<HoneySectionBlueprint> constructor = HoneySectionBlueprint.class
                            .getDeclaredConstructor(String.class, int.class);
                    whole.setAccessible(true);
                    sections[i] = constructor.newInstance(name, foodPoints);
                } else if (type == FoodSectionType.ROOT_VEG) {
                    DeathAiBlueprint deathAi = DeathAICreator.loadDeathAi(reader);
                    Constructor<RootVegSectionBlueprint> constructor = RootVegSectionBlueprint.class
                            .getDeclaredConstructor(String.class, int.class, DeathAiBlueprint.class);
                    whole.setAccessible(true);
                    sections[i] = constructor.newInstance(name, foodPoints, deathAi);
                } else {
                    sections[i] = whole.newInstance(name, foodPoints, null);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return sections;
    }
}
