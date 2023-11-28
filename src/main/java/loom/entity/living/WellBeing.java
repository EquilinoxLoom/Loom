package loom.entity.living;

import biomes.Biome;
import com.sun.istack.internal.NotNull;
import equilinox.classification.Classifiable;
import equilinox.ducktype.BiomeReference;
import loom.entity.weaver.EntityComponent;
import loom.entity.weaver.PrintUtils;

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
                                            @NotNull BiomeReference... biomes) {
        add(2, growsInBarren, biomes.length == 0 ? "0" : PrintUtils.printArray(";", biomes,
                biome -> String.valueOf(Biome.valueOf(biome.name()).ordinal())), idealPercentage, influence);
        return this;
    }

    public WellBeing addUnsuitableBiomeFactor(float influence, @NotNull BiomeReference... biomes) {
        add(3, PrintUtils.printArray(";", biomes,
                biome -> String.valueOf(Biome.valueOf(biome.name()).ordinal())), influence);
        return this;
    }

    public WellBeing addFavoriteBiomeFactor(float influence, String biome) {
        add(4, biome, influence);
        return this;
    }

    private WellBeing addSpeciesFactor(int id, float influence, @NotNull Classifiable... classifications) {
        add(id, PrintUtils.printArray(";", classifications, Classifiable::getClassification), influence);
        return this;
    }

    public WellBeing addLikedSpeciesFactor(float influence, @NotNull Classifiable... classifications) {
        return addSpeciesFactor(5, influence, classifications);
    }

    public WellBeing addDislikedSpeciesFactor(int influence, @NotNull Classifiable... classifications) {
        return addSpeciesFactor(6, influence, classifications);
    }
}
