package equilinoxmodkit.mod;

import equilinoxmodkit.util.OperatingSystem;

import java.io.File;
import java.util.ArrayList;

public class PreInitializer {
    private final boolean emlDebugModeEnabled;
    private final boolean equilinoxDebugModeEnabled;

    private final OperatingSystem operatingSystem;
    private final File equilinoxFolder, equilinoxJarFile, logFile, nativesFolder, modsFolder;

    private final ArrayList<String> presentMods;
    private final int numberOfLoadedMods;
    private final int numberOfRejectedMods;

    private final ArrayList<Class<?>> eventClasses;
    private final ArrayList<Class<?>> blueprintClasses;
    
    public PreInitializer(boolean emlDebugModeEnabled,boolean equilinoxDebugModeEnabled,OperatingSystem operatingSystem,File equilinoxFolder,File equilinoxJarFile,File logFile,File nativesFolder,File modsFolder,ArrayList<EquilinoxMod> presentMods,int numberOfLoadedMods,int numberOfRejectedMods) {
        this.emlDebugModeEnabled = emlDebugModeEnabled;
        this.equilinoxDebugModeEnabled = equilinoxDebugModeEnabled;
        this.operatingSystem = operatingSystem;
        this.equilinoxFolder = equilinoxFolder;
        this.equilinoxJarFile = equilinoxJarFile;
        this.logFile = logFile;
        this.nativesFolder = nativesFolder;
        this.modsFolder = modsFolder;
        this.presentMods = new ArrayList<>();
        for (EquilinoxMod presentMod : presentMods) this.presentMods.add(presentMod.getModInfo().id());
        this.numberOfLoadedMods = numberOfLoadedMods;
        this.numberOfRejectedMods = numberOfRejectedMods;

        eventClasses = new ArrayList<>();
        blueprintClasses = new ArrayList<>();
    }
    
    public void addEventClass(Class<?> clazz) {
        eventClasses.add(clazz);
    }

    public void addBlueprintClass(Class<?> clazz) {
        blueprintClasses.add(clazz);
    }

    public boolean isEmlDebugModeEnabled() {
        return emlDebugModeEnabled;
    }

    public boolean isEquilinoxDebugModeEnabled() {
        return equilinoxDebugModeEnabled;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public File getEquilinoxFolder() {
        return equilinoxFolder;
    }

    public File getEquilinoxJarFile() {
        return equilinoxJarFile;
    }

    public File getLogFile() {
        return logFile;
    }

    public File getNativesFolder() {
        return nativesFolder;
    }

    public File getModsFolder() {
        return modsFolder;
    }

    public boolean isModPresent(String id) {
        return presentMods.contains(id);
    }

    public int getNumberOfLoadedMods() {
        return numberOfLoadedMods;
    }

    public int getNumberOfRejectedMods() {
        return numberOfRejectedMods;
    }

    public ArrayList<Class<?>> getEventClasses() {
        return eventClasses;
    }

    public ArrayList<Class<?>> getBlueprintClasses() {
        return blueprintClasses;
    }
}
