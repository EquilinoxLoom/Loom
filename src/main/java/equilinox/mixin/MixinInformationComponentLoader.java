package equilinox.mixin;

import blueprints.Blueprint;
import componentArchitecture.ComponentBlueprint;
import components.InformationComponent;
import languages.GameText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import resourceManagement.SoundCache;
import utils.CSVReader;

import java.lang.reflect.Field;

@Mixin(value = InformationComponent.InformationCompLoader.class, remap = false)
public class MixinInformationComponentLoader {
    /**
     * @author Sandáliaball
     * @reason Name and Descriptions aren't at GameText file, so they are explicitly added
     */
    @Overwrite
    public ComponentBlueprint load(CSVReader reader, Blueprint actualBlueprint) {
        InformationComponent.InformationCompBlueprint blueprint = new InformationComponent.InformationCompBlueprint();
        try {
            boolean embroider = reader.getNextString().equals("EMBROIDER");

            String name = reader.getNextString();
            embroider$setField(blueprint, "name", embroider ? name : GameText.getText(Integer.parseInt(name)));

            String description = reader.getNextLabelString();
            embroider$setField(blueprint, "description", embroider ? description : GameText.getText(Integer.parseInt(description)));

            embroider$setField(blueprint, "dpCost", reader.getNextLabelInt());
            embroider$setField(blueprint, "baseDpPerMin", reader.getNextLabelInt());
            embroider$setField(blueprint, "roamingRange", reader.getNextLabelInt());
            embroider$setField(blueprint, "placementSound", SoundCache.CACHE.requestSound(reader.getNextLabelString(), true));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return blueprint;
    }

    @Unique private static void embroider$setField(Object o, String field, Object set)
            throws NoSuchFieldException, IllegalAccessException {
        Field readerField = o.getClass().getDeclaredField(field);
        readerField.setAccessible(true);
        readerField.set(o, set);
    }
}
