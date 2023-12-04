package equilinoxmodkit.mixins.loom;

import equilinoxmodkit.EMK;
import equilinoxmodkit.loader.LaunchHelper;
import equilinoxmodkit.loader.ModLoader;
import equilinoxmodkit.mod.EquilinoxMod;
import equilinoxmodkit.mod.ModInfo;
import equilinoxmodkit.util.EmkLogger;
import loom.LoomMod;
import main.MainApp;
import mainGuis.EquilinoxGuis;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import textures.Texture;
import utils.MyFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Mixin(value = {MainApp.class}, remap = false)
public class MixinMainApp {
	@Unique
	private static final List<LoomMod> loom$LOOMS = new ArrayList<>();

	/**
	 * Loads all classes extending {@link LoomMod} and having a valid {@link ModInfo} that are found in jar files within the {@code mods}
	 * directory by iterating through every jar file, every {@code .class} file within and finally trying to create an instance of the matches.
	 */
	@Inject(method = {"main"}, at = {@At("HEAD")})
	private static void preInit(String[] args, CallbackInfo c) {
		try {
			for (File file : LaunchHelper.getModsDir().listFiles()) {
				if (file.getName().endsWith(".jar")) {
					((LaunchClassLoader) Thread.currentThread().getContextClassLoader()).addURL(file.toURI().toURL());
					try (JarFile jarFile = new JarFile(file)) {
						for (JarEntry entry : Collections.list(jarFile.entries())) {
							loom$loadEntry(entry);
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch (ClassNotFoundException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Inject(method = {"main"}, at = {@At(value = "INVOKE", target = "LgameManaging/GameManager;init()V", shift = At.Shift.AFTER)})
	private static void init(String[] args, CallbackInfo c) {
		EquilinoxGuis.notify("EML initialized", String.format("Version %s - %d mods loaded, %d mods rejected",
                        EMK.VERSION, ModLoader.getNumberOfLoadedMods(), ModLoader.getNumberOfRejectedMods()),
                Texture.newTexture(new MyFile("emk_logo.png")).noFiltering().clampEdges().create(), null);

		EmkLogger.log( "Loading mods from 'mods' folder" );
	}

	@Unique
	private static void loom$loadEntry(JarEntry entry) throws ClassNotFoundException {
		if (entry.getName().endsWith(".class")) {
			String className = entry.getName().replace(".class", "").replace("/", ".");
			Class<?> clazz = Class.forName(className);
			if (EquilinoxMod.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(ModInfo.class)) {
				EmkLogger.log(" - loading " + className);
				EquilinoxMod mod = loom$createInstance(clazz);
				if (mod != null && loom$isModInfoValid(mod.getModInfo(), className)) {
					EmkLogger.log("Loading mod: '" + mod.getModInfo().name() + "' v" + mod.getModInfo().version());
					if (LoomMod.class.isAssignableFrom(clazz)) {
						LoomMod loom = (LoomMod) mod;
						loom.registry(new LoomMod.LoomRegistry(loom));
						loom$LOOMS.add(loom);
					}
				} else {
					EmkLogger.warn(clazz.getName(), " has no zero-args constructor present");
				}
			}
		}
	}

	@Unique
	private static boolean loom$isModInfoValid(ModInfo modInfo, String className) {
		if (modInfo != null) {
			if (modInfo.id().isEmpty()) {
				EmkLogger.warn("Mod was not loaded because 'id' was empty: " + className);
			} else {
				if (modInfo.version().matches("^\\d\\.\\d\\.\\d")) {
					return true;
				} else {
					EmkLogger.warn("Mod was not loaded because 'version' is formatted incorrectly: " + className);
				}
			}
		} else {
			EmkLogger.warn("Mod was not loaded because no ModInfo was present: " + className);
		}
		return false;
	}

	@Unique
	private static EquilinoxMod loom$createInstance(Class<?> clazz) {
		try {
			for (Constructor<?> constructor : clazz.getConstructors()) {
				if (constructor.getParameterCount() == 0) {
					constructor.setAccessible(true);
					return (EquilinoxMod) constructor.newInstance();
				} else {
					EmkLogger.warn(clazz.getName()," has no zero-args constructor present");
				}
			}
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException ignored) {}
		return null;
	}

	@Unique
	private static List<LoomMod> loom$getLooms() {
		return loom$LOOMS;
	}
}
