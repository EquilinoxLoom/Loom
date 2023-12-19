package equilinoxmodkit.mixins.loom;

import blueprints.Blueprint;
import com.google.common.collect.ImmutableList;
import equilinoxmodkit.loader.ModLoader;
import equilinoxmodkit.mod.ModInfo;
import equilinoxmodkit.util.EmkLogger;
import gameManaging.GameManager;
import languages.GameText;
import loom.LoomMod;
import main.MainApp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import resourceManagement.BlueprintRepository;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(value = {MainApp.class}, remap = false)
public abstract class MixinMainApp {
	@Shadow private static void cheat() {}

	static {
		GameText.init(0);
	}

	/**
	 * Loads all classes extending {@link LoomMod} and having a valid {@link ModInfo} that are found in jar files within the {@code mods}
	 * directory by iterating through every jar file, every {@code .class} file within and finally trying to create an instance of the matches.
	 */
	@Unique private static final List<LoomMod> loom$LOOMS = ModLoader.getModMains().stream().map(entry -> {
		try {
			Constructor<?> constructor = Thread.currentThread().getContextClassLoader()
					.loadClass(entry.getName().replace(".class", "").replace("/", ".")).getConstructor();
			constructor.setAccessible(true);
			LoomMod mod = (LoomMod) constructor.newInstance();
			mod.registry(new LoomMod.LoomRegistry(mod));
			EmkLogger.log("Flirru fleuru");
			return mod;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}).collect(ImmutableList.toImmutableList());

	@Unique private static <T> void loom$print(Predicate<Blueprint> predicate, Function<Blueprint, String> func) {
		BlueprintRepository.getAllBlueprints().stream().filter(predicate).map(func).forEach(EmkLogger::log);
		System.exit(0);
	}

	@Unique private static void loom$cheat() {
		cheat(); GameManager.getSession().getStats().increaseDp(1000000000);
	}

	@Unique
	private static List<LoomMod> loom$getLooms() {
		return loom$LOOMS;
	}
}
