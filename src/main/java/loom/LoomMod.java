package loom;

import loom.component.LoomComponent;
import loom.entity.Classifiable;
import loom.entity.Entity;
import loom.equilinox.CustomEatingAnimation;
import loom.equilinox.ducktype.BiomeReference;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class LoomMod implements ModInitializer {
    public static final String MOD_POINTER = "@loom@", MODID = "loom-api";
    final Map<String, Classifiable> classifications = new HashMap<>();
    final Map<Integer, Entity> entities = new LinkedHashMap<>();

    final List<LoomComponent> components = new ArrayList<>();
    final List<BiomeReference> biomes = new ArrayList<>();
    final List<CustomEatingAnimation> eatingAnimations = new ArrayList<>();

    public static final List<LoomMod> LOOMS = FabricLoaderImpl.INSTANCE.getAllMods()
            .stream().filter(mod -> mod instanceof LoomMod).map(mod -> (LoomMod) mod)
            .collect(Collectors.toList());

    public void onInitialize() {
        registry(new LoomRegistry(this));
    }

    private final LoomInfo info;

    public LoomMod(String id, String name, String version, String author, String description) {
        this.info = new LoomInfo(id, name, version, author, description);

        if (info.isValid()) LOOMS.add(this);
        else Log.warn(LogCategory.LOG, "Mod information is not valid");
    }

    public LoomInfo getInfo() {
        return info;
    }

    public abstract void registry(LoomRegistry registry);

    public List<LoomComponent> getComponents() {
        return new ArrayList<>(components);
    }

    public List<BiomeReference> getBiomes() {
        return new ArrayList<>(biomes);
    }

    public List<CustomEatingAnimation> getEatingAnimations() {
        return new ArrayList<>(eatingAnimations);
    }

    public Map<Integer, Entity> getEntities() {
        return new LinkedHashMap<>(entities);
    }
}
