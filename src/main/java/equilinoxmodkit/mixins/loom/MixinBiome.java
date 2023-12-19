package equilinoxmodkit.mixins.loom;

import audio.Sound;
import biomes.Biome;
import loom.LoomMod;
import loom.equilinox.ducktype.SoundReference;
import loom.equilinox.vanilla.VanillaColor;
import loom.equilinox.vanilla.VanillaLoader;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import particles.Particle;
import particles.ParticleSystem;
import toolbox.Colour;
import utils.CSVReader;
import utils.MyFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mixin(value = Biome.class, remap = false)
public class MixinBiome {

    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    private static Biome newBiome(String internalName, int internalId, String name, Colour colour,
                                  ParticleSystem system, Sound sound) {
        throw new AssertionError();
    }

    @SuppressWarnings("ShadowTarget")
    @Shadow
    private static @Final
    @Mutable Biome[] ENUM$VALUES;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lbiomes/Biome;ENUM$VALUES:[Lbiomes/Biome;", shift = At.Shift.AFTER))
    private static void addCustomBiome(CallbackInfo ci) {
        List<Biome> biomes = new ArrayList<>(Arrays.asList(ENUM$VALUES));
        LoomMod.LOOMS.stream().map(LoomMod::getBiomes).flatMap(Collection::stream).forEach(biome -> {
            try {
                biomes.add(newBiome(biome.name(), biomes.size(), biome.inGameName(),
                        VanillaColor.parseColor(biome.color()), (ParticleSystem) VanillaLoader
                                .getFunctionByClass(Particle.class)
                                .apply(new CSVReader(new MyFile(biome.particle().build()))),
                        SoundReference.load(biome.sound())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ENUM$VALUES = biomes.toArray(new Biome[0]);
    }
}
