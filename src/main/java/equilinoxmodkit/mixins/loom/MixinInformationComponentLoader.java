package equilinoxmodkit.mixins.loom;

import blueprints.Blueprint;
import componentArchitecture.ComponentBlueprint;
import components.InformationComponent;
import languages.GameText;
import loom.LoomMod;
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
            boolean loom = reader.getNextString().equals(LoomMod.MOD_POINTER);

            String name = reader.getNextString();
            loom$setField(blueprint, "name", loom ? name : GameText.getText(Integer.parseInt(name)));

            String description = reader.getNextLabelString();
            loom$setField(blueprint, "description", loom ? description : GameText.getText(Integer.parseInt(description)));

            loom$setField(blueprint, "dpCost", reader.getNextLabelInt());
            loom$setField(blueprint, "baseDpPerMin", reader.getNextLabelInt());
            loom$setField(blueprint, "roamingRange", reader.getNextLabelInt());
            loom$setField(blueprint, "placementSound", SoundCache.CACHE.requestSound(reader.getNextLabelString(), true));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return blueprint;
    }

    @Unique
    private static void loom$setField(Object o, String field, Object set)
            throws NoSuchFieldException, IllegalAccessException {
        Field readerField = o.getClass().getDeclaredField(field);
        readerField.setAccessible(true);
        readerField.set(o, set);
    }
}
