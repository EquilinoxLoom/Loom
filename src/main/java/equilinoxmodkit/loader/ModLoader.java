package equilinoxmodkit.loader;

import equilinoxmodkit.mod.EquilinoxMod;
import equilinoxmodkit.mod.ModInfo;
import equilinoxmodkit.util.EmkLogger;
import loom.LoomMod;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/* Loads and manages loaded mods. */
public class ModLoader {
	private static final ArrayList<EquilinoxMod> loadedMods = new ArrayList<>();
	
	public static ArrayList<EquilinoxMod> getLoadedMods() {
		return loadedMods;
	}

	private static List<Class<?>> modMains;

	public static void addURL(File file, LaunchClassLoader loader) {
		try {
			loader.addURL(file.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Class<?>> getModMains() {
		return new ArrayList<>(modMains);
	}

	public static void loadMods(LaunchClassLoader loader) {
		EmkLogger.log("Loading mods from 'mods' folder");
		File[] mods = LaunchHelper.getModsDir().listFiles();
		if (mods == null) throw new RuntimeException("Couldn't load mods directory");

		List<File> modFiles = Arrays.stream(mods).filter(file -> file.getName().endsWith(".jar")).collect(Collectors.toList());

		URLClassLoader[] url = new URLClassLoader[1];

		modMains = modFiles.stream()
				.peek(file -> ModLoader.addURL(file, loader))
				.map(file -> {
					try {
						url[0] = new URLClassLoader(new URL[]{file.toURI().toURL()});
						return new JarFile(file);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				})
				.flatMap(jar -> Collections.list(jar.entries()).stream())
				.filter(entry -> entry.getName().endsWith(".class"))
				.map(entry -> {
					try {
						return url[0].loadClass(entry.getName().replace(".class", "").replace("/", "."));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				})
				.filter(clazz -> LoomMod.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(ModInfo.class))
				.peek(clazz -> EmkLogger.log(" - loading " + clazz.getName())).map(ModLoader::createInstance)
				.filter(mod -> mod != null && ModLoader.isModInfoValid(mod.getModInfo(), mod.getClass().getName()) && LoomMod.class.isAssignableFrom(mod.getClass()))
				.peek(mod -> EmkLogger.log("Loading mod: '" + mod.getModInfo().name() + "' v" + mod.getModInfo().version()))
				.peek(mod -> {
					String mixin = ((LoomMod) mod).mixin();
					if (mixin != null) Mixins.addConfiguration(mixin);
				}).map(EquilinoxMod::getClass).collect(Collectors.toList());
	}

	public static boolean isModInfoValid(ModInfo modInfo, String className) {
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

	public static EquilinoxMod createInstance(Class<?> clazz) {
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
}