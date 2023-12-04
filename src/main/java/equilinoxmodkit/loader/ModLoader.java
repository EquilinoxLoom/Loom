package equilinoxmodkit.loader;

import equilinoxmodkit.event.EmkEvent;
import equilinoxmodkit.mod.*;
import equilinoxmodkit.util.EmkLogger;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/* Loads and manages loaded mods. */
public class ModLoader {
	private static final ArrayList<EquilinoxMod> loadedMods = new ArrayList<>();
	private static int numOfLoadedMods;
	private static int numOfRejectedMods;
	
	public static void initializeMods() {
		Initializer initializer = new Initializer();
		loadedMods.forEach(mod -> mod.init(initializer));
	}
	
	public static ArrayList<EquilinoxMod> getLoadedMods() {
		return loadedMods;
	}
	
	public static ArrayList<ModInfo> getLoadedModsModInfos() {
		ArrayList<ModInfo> modInfos = new ArrayList<>();
		loadedMods.forEach(mod -> modInfos.add(mod.getModInfo()));
		return modInfos;
	}
	
	public static ArrayList<Dependency> getLoadedModDependencies() {
		ArrayList<Dependency> dependencies = new ArrayList<>();
		loadedMods.forEach(mod -> dependencies.add(mod.getDependency()));
		return dependencies;
	}
	
	public static int getNumberOfLoadedMods() {
		return numOfLoadedMods;
	}
	
	public static int getNumberOfRejectedMods() {
		return numOfRejectedMods;
	}


	/**
	 * Loads all classes extending {@link EquilinoxMod} and having a valid {@link ModInfo} that are found in jar files within the {@code mods}
	 * directory by iterating through every jar file, every {@code .class} file within and finally trying to create an instance of the matches.
	 */
	public static void loadMods(LaunchClassLoader loader) {
		EmkLogger.log( "Loading mods from 'mods' folder" );
		try {
			for (File file : LaunchHelper.getModsDir().listFiles()) {
				if (file.getName().endsWith(".jar")) {
					loader.addURL(file.toURI().toURL());
					try (URLClassLoader urlLoader = new URLClassLoader(new URL[] { file.toURI().toURL() })) {
						try (JarFile jarFile = new JarFile(file)) {
							for (JarEntry entry : Collections.list(jarFile.entries())) {
								if (entry.getName().endsWith(".class")) {
									checkClass(urlLoader, entry.getName().replace(".class", "").replace("/", "."));
								}
							}
						} catch (IOException ignored) {
						}
					} catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
					System.err.print("File " + file.getName() + " is not a jar");
				}
			}
		} catch (ClassNotFoundException ignored) {} catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

	private static void checkClass(URLClassLoader loader, String className) throws ClassNotFoundException {
		Class<?> clazz = loader.loadClass(className);
		if (EquilinoxMod.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(ModInfo.class)) {
			EmkLogger.log(" - loading " + className);
			EquilinoxMod mod = ModLoader.createInstance(clazz);
			if (mod != null && isModInfoValid(mod.getModInfo(), className)) {
				EmkLogger.log("Loading mod: '" + mod.getModInfo().name() + "' v" + mod.getModInfo().version());

				loadedMods.add(mod);
				numOfLoadedMods++;
			} else {
				numOfRejectedMods++;
			}
		}
	}

	public static void sortMods() {
		EmkLogger.log("Sorting mods according to dependencies");
		int numOfDependencies;
		HashMap<Integer,ArrayList<EquilinoxMod>> modBatches = new HashMap<>();
		for (EquilinoxMod mod : loadedMods) {
			if (mod.getDependency() != null) {
				numOfDependencies = mod.getDependency().dependencyIDs().length;
				if (modBatches.containsKey(numOfDependencies)) {
					modBatches.get(numOfDependencies).add(mod);
				} else {
					ArrayList<EquilinoxMod> newBatch = new ArrayList<>();
					newBatch.add(mod);
					modBatches.put(numOfDependencies,newBatch);
				}
			}
		}
	}
	
	public static void preInitializeMods() {
		PreInitializer preInitializer = new PreInitializer(
				LaunchHelper.isEmlDebugModeEnabled(),
				LaunchHelper.isEquilinoxDebugModeEnabled(),
				LaunchHelper.getOperatingSystem(),
				LaunchHelper.getEquilinoxDir(),
				LaunchHelper.getEquilinoxJar(),
				LaunchHelper.getLogFile(),
				LaunchHelper.getNativesDir(),
				LaunchHelper.getModsDir(),
				loadedMods,
				numOfLoadedMods,
				numOfRejectedMods
		);
		loadedMods.forEach(mod -> mod.preInit(preInitializer));
		handleEventClasses(preInitializer.getEventClasses());
		handleBlueprintClasses(preInitializer.getBlueprintClasses());
	}
	
	
	private static boolean isModInfoValid(ModInfo modInfo, String className) {
		if (modInfo != null) {
			if (modInfo.id().isEmpty()) {
				EmkLogger.warn("Mod was not loaded because 'id' was empty: " + className);
			} else {
				if (modInfo.version().matches("^\\d\\.\\d\\.\\d"))
					return true;
				EmkLogger.warn("Mod was not loaded because 'version' is formatted incorrectly: " + className);
			}
		} else {
			EmkLogger.warn("Mod was not loaded because no ModInfo was present: " + className);
		}
		return false;
	}
	
	private static EquilinoxMod createInstance(Class<?> clazz) {
		try {
			for (Constructor<?> constructor : clazz.getConstructors()) {
				if (constructor.getParameterCount() == 0) {
					constructor.setAccessible(true);
					return (EquilinoxMod) constructor.newInstance();
				} else {
					EmkLogger.warn(clazz.getName()," has no zero-args constructor present");
					numOfLoadedMods++;
				}
			}
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException ignored) {}
		return null;
	}
	
	private static void handleEventClasses(ArrayList<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.getParameterCount() == 1 && method.isAnnotationPresent(EmkEvent.class)) {
					EventManager.addMethod(method.getParameters()[0].getType(),method,clazz);
				}
			}
		}
	}
	
	private static void handleBlueprintClasses(ArrayList<Class<?>> classes) {}
}