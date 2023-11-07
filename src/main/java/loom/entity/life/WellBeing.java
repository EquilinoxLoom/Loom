package loom.entity.life;

import com.sun.istack.internal.NotNull;
import equilinox.classification.Classifiable;
import loom.IEnvironment;
import loom.entity.weaver.EntityComponent;
import loom.entity.weaver.Printable;

@SuppressWarnings("unused")
public class WellBeing extends EntityComponent {


    @Override
    protected String getDelimiter() {
        return ";;";
    }

    public WellBeing addAltitudeFactor(int min, int max, float influence) {
        add(1, min, max, influence);
        return this;
    }

    /**
     * @param growsInBarren Whether the entity accepts being at barren land.
     *                      <STRONG>{@code True}</STRONG> for all grasses.
     */
    public WellBeing addSuitableBiomeFactor(float influence, boolean growsInBarren, float idealPercentage,
                                            @NotNull IEnvironment... biomes) {
        add(2, growsInBarren, biomes.length == 0 ? "0" : Printable.printArray(";", biomes,
                biome -> String.valueOf(biome.getId())), idealPercentage, influence);
        return this;
    }

    public WellBeing addUnsuitableBiomeFactor(float influence, @NotNull IEnvironment... biomes) {
        add(3, Printable.printArray(";", biomes, biome -> String.valueOf(biome.getId())), influence);
        return this;
    }

    public WellBeing addFavoriteBiomeFactor(float influence, String biome) {
        add(4, biome, influence);
        return this;
    }

    private WellBeing addSpeciesFactor(int id, float influence, @NotNull Classifiable... classifications) {
        add(id, Printable.printArray(";", classifications, Classifiable::getClassification), influence);
        return this;
    }

    public WellBeing addLikedSpeciesFactor(float influence, @NotNull Classifiable... classifications) {
        return addSpeciesFactor(5, influence, classifications);
    }

    public WellBeing addDislikedSpeciesFactor(int influence, @NotNull Classifiable... classifications) {
        return addSpeciesFactor(6, influence, classifications);
    }
}
