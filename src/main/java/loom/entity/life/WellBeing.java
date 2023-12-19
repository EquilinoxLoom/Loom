package loom.entity.life;

import biomes.Biome;
import loom.entity.Classifiable;
import loom.entity.weaver.EntityComponent;
import loom.entity.weaver.EntityPrint;
import loom.equilinox.ducktype.BiomeReference;

import javax.annotation.Nonnull;

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
                                            @Nonnull BiomeReference... biomes) {
        add(2, growsInBarren, biomes.length == 0 ? "0" : EntityPrint.printArray(";", biomes,
                biome -> String.valueOf(Biome.valueOf(biome.name()).ordinal())), idealPercentage, influence);
        return this;
    }

    public WellBeing addUnsuitableBiomeFactor(float influence, @Nonnull BiomeReference... biomes) {
        add(3, EntityPrint.printArray(";", biomes,
                biome -> String.valueOf(Biome.valueOf(biome.name()).ordinal())), influence);
        return this;
    }

    public WellBeing addFavoriteBiomeFactor(float influence, String biome) {
        add(4, biome, influence);
        return this;
    }

    private WellBeing addSpeciesFactor(int id, float influence, @Nonnull Classifiable... classifications) {
        add(id, EntityPrint.printArray(";", classifications, Classifiable::getClassification), influence);
        return this;
    }

    public WellBeing addLikedSpeciesFactor(float influence, @Nonnull Classifiable... classifications) {
        return addSpeciesFactor(5, influence, classifications);
    }

    public WellBeing addDislikedSpeciesFactor(int influence, @Nonnull Classifiable... classifications) {
        return addSpeciesFactor(6, influence, classifications);
    }
}
