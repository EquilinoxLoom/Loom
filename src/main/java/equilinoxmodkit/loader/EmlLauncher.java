package equilinoxmodkit.loader;

import equilinoxmodkit.util.EmkLogger;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class EmlLauncher implements ITweaker {
	public static void main(String[] args) {
		EmkLogger.log("Starting the Equilinox Mod Loader");
		LaunchHelper.handleLaunchArguments(args);
		LaunchHelper.prepareLaunch();

		EmlLauncher.launchEquilinox();
	}

	static void stop() {
		EmkLogger.log("Stopping the Equilinox Mod Loader");

		if (LaunchHelper.getLogFile() != null) EmkLogger.save(LaunchHelper.getLogFile());
		System.exit(-1);
	}

	private static void launchEquilinox() {
		EmkLogger.log("Launching Equilinox");
		Launch.main(new String[]{"--tweakClass", EmlLauncher.class.getName()});
		EmkLogger.log("Stopped Equilinox");
	}

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader loader) {
		EmkLogger.log("Configuring launch settings");
		try {
			loader.addURL(LaunchHelper.getEquilinoxJar().toURI().toURL());

			MixinBootstrap.init();
			Mixins.addConfiguration("mixins.eml.json");

			ModLoader.loadMods(loader);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
    }

	@Override
	public String getLaunchTarget() {
		return "main.MainApp";
	}

	@Override
	public String[] getLaunchArguments() {
		return new String[0];
	}
}
