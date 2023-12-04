package equilinoxmodkit.loader;

import equilinoxmodkit.util.EmkLogger;
import equilinoxmodkit.util.ExtendedLogger;
import equilinoxmodkit.util.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static equilinoxmodkit.util.OperatingSystem.*;

/* Class that assists the 'EmlLauncher' class on start-up. It more precisely manages handling
 * launch arguments, finding the Equilinox executable jar and extracting the natives required by
 * LWJGL from it. */
public class LaunchHelper {
	private static final String[] NATIVE_NAMES_WINDOWS = new String[] {
			"jinput-dx8.dll",
			"jinput-dx8_64.dll",
			"jinput-raw.dll",
			"jinput-raw_64.dll",
			"lwjgl.dll",
			"lwjgl64.dll",
			"OpenAL32.dll",
			"OpenAL64.dll"
	};
	
	private static final String[] NATIVE_NAMES_MACOS = new String[] {
			"libjinput-osx.dylib",
			"liblwjgl.dylib",
			"openal.dylib"
	};
	
	private static boolean emlDebugModeEnabled, equilinoxDebugModeEnabled;
	
	private static OperatingSystem operatingSystem;
	private static File equilinoxDir, equilinoxJar, logFile, nativesDir, modsDir, javaFile;
	
	public static boolean isEmlDebugModeEnabled() {
		return emlDebugModeEnabled;
	}
	
	public static boolean isEquilinoxDebugModeEnabled() {
		return equilinoxDebugModeEnabled;
	}
	
	public static OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}
	
	public static File getEquilinoxDir() {
		return equilinoxDir;
	}
	
	public static File getEquilinoxJar() {
		return equilinoxJar;
	}
	
	public static File getLogFile() {
		return logFile;
	}
	
	public static File getNativesDir() {
		return nativesDir;
	}
	
	public static File getModsDir() {
		return modsDir;
	}
	
	public static File getJavaFile() {
		return javaFile;
	}
	
	static void handleLaunchArguments(String[] args) {
		for (String arg : args) {
			if (arg.equals("-EmlDebug")) {
				emlDebugModeEnabled = true;
				EmkLogger.log("Extended debugging enabled");
			} else if (arg.equals("-EqDebug")) {
				equilinoxDebugModeEnabled = true;
				EmkLogger.log("Equilinox debugging enabled");
			} else {
				String[] tokens = arg.replace("\"","").split("=");
				if (arg.startsWith("-EqDir")) {
					equilinoxDir = new File(tokens[1]);
					EmkLogger.log("Equilinox directory manually set to '",equilinoxDir,"'");
				} else if (arg.startsWith("-EmkLogFile")) {
					logFile = new File(tokens[1]);
					EmkLogger.log("Log file manually set to '",logFile,"'");
				} else if (arg.startsWith("-EmlNativesDir")) {
					nativesDir = new File(tokens[1]);
					EmkLogger.log("Natives directory manually set to '",nativesDir,"'");
				} else if (arg.startsWith("-EmlModsDir")) {
					modsDir = new File(tokens[1]);
					EmkLogger.log("Mods directory manually set to '",modsDir,"'");
				}
			}
		}
	}
	
	static void prepareLaunch() {
		boolean equDirManCgd = equilinoxDir != null;
		boolean logFleManCgd = logFile != null;
		boolean ntvDirManCgd = nativesDir != null;
		boolean mdsDirManCgd = modsDir != null;
		operatingSystem = LaunchHelper.determineOperatingSystem();
		
		final String THIS_FOLDER_PATHNAME = LaunchHelper.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		switch(operatingSystem) {
			case WINDOWS:
				if (equilinoxDir == null) equilinoxDir = LaunchHelper.determineEquilinoxDirectory(THIS_FOLDER_PATHNAME);
				if (equilinoxDir == null) equilinoxDir = LaunchHelper.determineEquilinoxDirectory("C:/Program Files (x86)/Steam/steamapps/common/Equilinox");
				if (equilinoxDir == null) equilinoxDir = LaunchHelper.determineEquilinoxDirectory("C:/Program Files/Steam/steamapps/common/Equilinox");
				if (equilinoxDir == null) {
					System.err.println("Could not determine Equilinox directory!");
					System.exit(-1);
				}
				equilinoxJar = new File(equilinoxDir.getPath() + "/EquilinoxWindows.jar");
				if (logFile == null) logFile = new File(equilinoxDir.getPath() + "/emk.log");
				if (nativesDir == null) nativesDir = new File(equilinoxDir.getPath() + "/natives");
				if (modsDir == null) modsDir = new File(equilinoxDir.getPath() + "/mods");
				LaunchHelper.loopThroughNatives(NATIVE_NAMES_WINDOWS);
				javaFile = new File(equilinoxDir.getPath() + "/jreWindows32/bin/java");
				break;
			case LINUX:
				if (equilinoxDir == null) equilinoxDir = LaunchHelper.determineEquilinoxDirectory(THIS_FOLDER_PATHNAME);
				if (equilinoxDir == null)
					equilinoxDir = LaunchHelper.determineEquilinoxDirectory("/home/" + System.getProperty("user.name") + "/.var/app/com.valvesoftware.Steam/.local/share/Steam/steamapps/common/Equilinox");
				if (equilinoxDir == null) {
					System.err.println("Could not determine Equilinox directory!");
					System.exit(-1);
				}
				equilinoxJar = new File(equilinoxDir.getPath() + "/input.jar");
				if (logFile == null) logFile = new File(equilinoxDir.getPath() + "/emk.log");
				if (nativesDir == null) nativesDir = new File(equilinoxDir.getPath()); // may cause issues ?!
				if (modsDir == null) modsDir = new File(equilinoxDir.getPath() + "/mods");
				javaFile = new File(equilinoxDir.getPath() + "/jre/bin/java");
				break;
			case MACOS:
				if (equilinoxDir == null && LaunchHelper.determineEquilinoxDirectory(THIS_FOLDER_PATHNAME + "/EquilinoxMac.app/Contents/Java") != null)
					equilinoxDir = new File(THIS_FOLDER_PATHNAME);
				if (equilinoxDir == null && LaunchHelper.determineEquilinoxDirectory("/Users/" + System.getProperty("user.name") + "/Library/Application Support/Steam/steamapps/common/Equilinox/EquilinoxMac.app/Contents/Java") != null)
					equilinoxDir = new File("/Users/" + System.getProperty("user.name") + "/Library/Application Support/Steam/steamapps/common/Equilinox");
				if (equilinoxDir == null) {
					System.err.println("Could not determine Equilinox directory!");
					System.exit(-1);
				}
				equilinoxJar = new File(equilinoxDir.getPath() + "/EquilinoxMac.app/Contents/Java/EquilinoxMac.jar");
				if (logFile == null) logFile = new File(equilinoxDir.getPath() + "/emk.log");
				if (nativesDir == null) nativesDir = new File(equilinoxDir.getPath());
				if (modsDir == null) modsDir = new File(equilinoxDir.getPath() + "/mods");
				LaunchHelper.loopThroughNatives(NATIVE_NAMES_MACOS);
				javaFile = new File(equilinoxDir.getPath() + "/PlugIns/OracleJdkMac.jdk/Contents/Home/bin/java");
				break;
		}

		System.setProperty("org.lwjgl.librarypath",nativesDir.getPath());
		
		if (!equDirManCgd) ExtendedLogger.log("Equilinox directory set to '",equilinoxDir,"'");
		if (!logFleManCgd) ExtendedLogger.log("Log file set to '",logFile,"'");
		if (!ntvDirManCgd) ExtendedLogger.log("Natives directory set to '",nativesDir,"'");
		if (!mdsDirManCgd) ExtendedLogger.log("Mods directory set to '",modsDir,"'");
	}
	
	
	private static OperatingSystem determineOperatingSystem() {
		OperatingSystem operatingSystem = null;
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) operatingSystem = WINDOWS;
		else if (osName.contains("lin")) operatingSystem = LINUX;
		else if (osName.contains("mac")) operatingSystem = MACOS;
		else {
			System.err.println("Could not determine operating system '" + osName + '!');
			EmlLauncher.stop();
		}
		ExtendedLogger.log("Operating system is " + operatingSystem);
		return operatingSystem;
	}
	
	private static File determineEquilinoxDirectory(String pathname) {
		File dir = new File(pathname);
		if (dir.exists()) {
			String filename;
			for (File file : dir.listFiles()) {
				filename = file.getName();
				if (filename.equals("EquilinoxWindows.jar") || filename.equals("EquilinoxMac.jar") || filename.equals("input.jar")) {
					return dir;
				}
			}
		}
		return null;
	}
	
	private static void loopThroughNatives(String[] nativeNames) {
		if (nativesDir.mkdir()) EmkLogger.log("Created natives file.");
		if (modsDir.mkdir()) EmkLogger.log("Created mods file.");
		try (JarFile jarFile = new JarFile(equilinoxJar)) {
			for (String nativeName : nativeNames) {
				JarEntry entry = jarFile.getJarEntry(nativeName);
				if (nativeName.equals(entry.getName())) {
					File targetFile = new File(nativesDir + "/" + nativeName);
					if (!targetFile.exists()) {
						Files.copy(jarFile.getInputStream(entry), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						EmkLogger.log(LaunchHelper.class, " - copied " + nativeName);
					}
				}

			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}