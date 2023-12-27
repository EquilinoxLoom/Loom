package loom.entity.weaver;

import biomes.Biome;
import loom.LoomMod;
import loom.component.PrintableComponent;
import loom.entity.Classifiable;
import loom.entity.Entity;
import loom.entity.Named;
import loom.entity.Tooltip;
import loom.entity.animal.*;
import loom.entity.food.Edible;
import loom.entity.life.Living;
import loom.entity.life.LivingEntity;
import loom.entity.plant.*;
import loom.entity.structure.Projectile;
import loom.entity.structure.Timeout;
import loom.equilinox.vanilla.VanillaClassification;
import loom.equilinox.vanilla.VanillaColor;
import loom.equilinox.vanilla.VanillaComponent;
import loom.equilinox.vanilla.VanillaSpecie;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static loom.equilinox.vanilla.VanillaComponent.*;

public class ModelReader {
    public static List<String> readEntityModel(Entity entity) {
        List<String> body = new ArrayList<>();

        if (entity.getModelPaths() == null || entity.getModelPaths().length == 0) {
            throw new RuntimeException("Model from entity of id " + entity.getId() + " is null or empty.");
        }

        if (VanillaSpecie.isIdAvailable(entity.getId())) {
            throw new RuntimeException("Id:" + entity.getId() + " is not available.");
        }

//      String[stage][obj/mtl][lines]

        String[][][] model = loadModel(entity);

        int[][] pointerMatrix = new int[model.length][];
        String[][] faceMatrix = new String[model.length][];

        Vector3f[][] vertexMatrix = new Vector3f[model.length][];
        Vector3f[][] normalMatrix = new Vector3f[model.length][];
        Vector3f[][] colourMatrix = new Vector3f[model.length][];

        for (int i = 0, counter = 0; i < model.length; i++) {
            String[] obj = model[i][0];
            String[] mtl = model[i][1];

            List<Integer> pointers = new ArrayList<>();
            List<String> faces = new ArrayList<>();

            List<Vector3f> vertices = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();

            for (String line : obj) {
                String[] split = line.split(" ");
                switch (split[0]) {
                    case "v":
                        vertices.add(new Vector3f(
                                Float.parseFloat(split[1]),
                                Float.parseFloat(split[2]),
                                Float.parseFloat(split[3])
                        ));
                        break;
                    case "vn":
                        normals.add(new Vector3f(
                                Float.parseFloat(split[1]),
                                Float.parseFloat(split[2]),
                                Float.parseFloat(split[3])
                        ));
                        break;
                    case "usemtl":
                        faces.add("POINTER");
                        pointers.add(counter);
                        break;
                    case "f":
                        faces.add(split[1]);
                        faces.add(split[2]);
                        faces.add(split[3]);
                        counter+=3;
                        break;
                }
            }

            List<Vector3f> colours = readMtl(mtl);

            if (entity.getMaterials() != null && !entity.getMaterials().isEmpty() && !(entity.hasEggStage() && i == 0)) {
                for (int j = 0; j < colours.size(); j += 2) colours.set(j, new Vector3f(-1, 0, 0));
            }

            pointers.add(counter);
            pointerMatrix[i] = pointers.stream().mapToInt(Integer::valueOf).toArray();
            faceMatrix[i] = faces.toArray(new String[0]);
            vertexMatrix[i] = vertices.toArray(new Vector3f[0]);
            normalMatrix[i] = normals.toArray(new Vector3f[0]);
            colourMatrix[i] = colours.toArray(new Vector3f[0]);
        }

        body.add(model.length + "\n");
        for (int x = 0; x < model.length; x++) {
            body.add(String.format(Locale.US, "%.4f;%.4f;%.4f;%.4f;%.4f;%.4f;1\n", getBoundingBox(vertexMatrix[x])));

            int pointer = pointerMatrix[x].length - 1;
            body.add((faceMatrix[x].length - pointer) + ";" + pointer);
            for (int y = 0, z = 0; y < faceMatrix[x].length; y++) {
                if (faceMatrix[x][y].contains("/")) {
                    // 2//4 : faces0 = 2, faces1 = 4
                    String[] faces = faceMatrix[x][y].split("/");
                    int faces0 = Integer.parseInt(faces[0]) - 1;
                    int faces1 = Integer.parseInt(faces[2]) - 1;
                    body.add(String.format(Locale.US, "%.4f;%.4f;%.4f;%.4f;%.4f;%.4f;",
                            vertexMatrix[x][faces0].x, vertexMatrix[x][faces0].y, vertexMatrix[x][faces0].z,
                            normalMatrix[x][faces1].x, normalMatrix[x][faces1].y, normalMatrix[x][faces1].z
                    ));
                }

                if (faceMatrix[x][y].contains("POINTER")) {
                    body.add(String.format(Locale.US, "\n%d;%f;%f;%f\n",
                            pointerMatrix[x][z+1] - pointerMatrix[x][z],
                            colourMatrix[x][z].x, colourMatrix[x][z].y, colourMatrix[x][z].z
                    ));
                    z++;
                }
            }
            body.add("\n");
        }

        return body;
    }

    private static List<Vector3f> readMtl(String[] mtl) {
        List<Vector3f> colours = new ArrayList<>();
        for (String line : mtl) {
            String[] split = line.split(" ");
            if (split[0].equals("Kd")) {
                colours.add(new Vector3f(
                        Float.parseFloat(split[1]),
                        Float.parseFloat(split[2]),
                        Float.parseFloat(split[3])
                ));
            }
        }
        return colours;
    }

    private static Object[] getBoundingBox(Vector3f[] vertexMatrix) {
        float lowestX = vertexMatrix[0].x;
        float lowestY = vertexMatrix[0].y;
        float lowestZ = vertexMatrix[0].z;

        float highestX = vertexMatrix[0].x;
        float highestY = vertexMatrix[0].y;
        float highestZ = vertexMatrix[0].z;

        for (Vector3f vector : vertexMatrix) {
            if (vector.x < lowestX) lowestX = vector.x;
            if (vector.y < lowestY) lowestY = vector.y;
            if (vector.z < lowestZ) lowestZ = vector.z;

            if (vector.x > highestX) highestX = vector.x;
            if (vector.y > highestY) highestY = vector.y;
            if (vector.z > highestZ) highestZ = vector.z;
        }

        return new Object[] { lowestX, lowestY, lowestZ, highestX, highestY, highestZ };
    }

    private static String[][][] loadModel(Entity entity) {
        String[][][] model = new String[entity.getModelPaths().length][2][];
        String[] modelPaths = entity.getModelPaths();
        for (int i = 0; i < modelPaths.length; i++) {
            model[i] = new String[][]{
                    readFile(modelPaths[i] + ".obj"),
                    readFile(modelPaths[i] + ".mtl")
            };
        }
        return model;
    }

    private static String[] readFile(String path) {
        List<String> lines = new ArrayList<>();

        // Use the ClassLoader to get the input stream of the file inside the JAR
        InputStream inputStream = EntityProcessor.class.getClassLoader().getResourceAsStream(path);

        if (inputStream != null) {
            // Read the lines using a BufferedReader
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("File not found: " + path);
        }

        return lines.toArray(new String[0]);
    }

    private static final Map<Class<? extends Annotation>, VanillaComponent> ANNOTATIONS = new HashMap<>() {{
        put(Projectile.class, PROJECTILE); put(Flytrap.class, FLY_TRAP); put(Floater.class, LILY); put(Sunflower.class, SUN_FACER); put(Stinger.class, STINGING);
        put(TreeCharger.class, CHARGE); put(Peacock.class, PEACOCK); put(Named.class, NAME); put(DenBuilder.class, BEAVER); put(Panic.class, PANIC);
    }};

    public static Map<PrintableComponent, String> readEntityComponents(Entity entity) {
        final Map<PrintableComponent, String> components = entity.getComponents();

        if (entity instanceof BiomeSpreader spreader)
            try {
                components.put(SPREADER, Biome.valueOf(spreader.biome().name()).ordinal() + ";0.1;0.1;0.1;"
                        + spreader.strength() + ";" + spreader.distance());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Biome " + spreader.biome().name() + " was not registered");
            }
        if (entity.getClass().isAnnotationPresent(Timeout.class))
            components.put(TIME_OUT, String.valueOf(entity.getClass().getAnnotation(Timeout.class).value()));
        if (entity.getMaterials() != null && !entity.getMaterials().isEmpty()) {
            Map<Color, Integer> map = new LinkedHashMap<>();
            AtomicInteger atomic = new AtomicInteger(0);
            entity.getMaterials().forEach((color, price) -> {
                if (atomic.incrementAndGet() == 1 && entity.getSecondNaturalColor() != null) {
                    map.put(entity.getSecondNaturalColor(), entity.getMaterials().values().iterator().next());
                }
                map.put(color, price);
            });
            components.put(MATERIAL, ";" + EntityPrint.print(";;", entity.getSecondNaturalColor() != null,
                    EntityPrint.printArray(";", map.entrySet().toArray(new Map.Entry[0]), e -> {
                        Map.Entry<Color, Integer> entry = (Map.Entry<Color, Integer>) e;
                        return VanillaColor.isVanilla(entry.getKey()) + ";" + entry.getValue();
                    })
            ));
        }
        if (entity instanceof Tooltip info) components.put(INFO, EntityPrint.print(";;", LoomMod.MOD_POINTER + ";" + info.name(),
                info.description(), info.price(), info.dpPerMin(), info.range(), info.sound().toString().toLowerCase(Locale.ROOT)));
        if (entity instanceof Living life) {
            EntityComponent print = new EntityComponent() {};
            float[] factors = life.populationFactors();
            print.addSub(life.population(), life.lifespan(), factors.length + (factors.length > 0 ? ";"
                            + IntStream.range(0, factors.length).mapToDouble(i -> factors[i]).mapToObj(String::valueOf)
                            .collect(Collectors.joining(";")) : ""),
                    life.breedingMaturity(), life.breedingCooldown());
            if (life.getEvolution() == null) print.addSub("-1;" + life.getDeath().build(), life.getWellBeing().build());
            else print.addSub(life.getEvolution().parent, life.getEvolution().points,
                    life.getEvolution().build() + ";" + life.getDeath().build(), life.getWellBeing().build());
            if (life.getDefense() != 0) print.addSub(life.getDefense());
            components.put(LIFE, print.build());

            print.addSub(life.dynamic(), life.growthTime(), life.stages());
            if (!life.dynamic()) print.addSub(life.subStages());
            components.put(GROWTH, print.build());
        }
        if (entity instanceof FruitProducer fruit) {
            components.put(FRUITER, ";" + fruit.modelIndex() + ";;" + fruit.stages()
                    + (fruit.time() != 5 ? ";;" + fruit.time() : ""));
            if (fruit instanceof FruitFall fall) {
                components.put(FRUIT_FALL, ";" + EntityPrint.print(";;", fall.fruit().getId(),
                        0.012f / fall.fallChancePerHour(), fall.fruitsSpawnHeight(), fall.fruitsSpawnRadius()));
            }
        }
        if (entity instanceof WoodProducer wood) components.put(WOOD, ";" + wood.cuttingTime() + ";;"
                + wood.barkChance() + ";;" + wood.woodColour().getRed() + ";" + wood.woodColour().getGreen() + ";" + wood.woodColour().getBlue());
        if (entity instanceof Builder builder) {
            assert entity instanceof LivingEntity;
            components.put(BUILDER, ";" + EntityPrint.print(";;", builder.structure().getId(), builder.points(),
                    builder.structure() instanceof Entity
                            ? ((Entity) builder.structure()).hasComponent(PERCHER)
                            : builder.structure().getId() == VanillaSpecie.NEST.getId(),
                    builder.buildTime(), (builder.age() - 1) / ((LivingEntity) entity).stages()) +
                    (builder.buildingHour() != 0 ? ";;" + builder.buildTime() : ""));
            if (entity instanceof ContainerFiller) components.put(BEE, "");
        }
        if (entity instanceof Edible edible) components.put(FOOD, edible.table().build());
        if (entity instanceof Holder holder) components.put(EQUIP, ";" + EntityPrint.printArray(";;",
                holder.positions(), v -> v.x + ";" + v.y + ";" + v.z));
        if (entity instanceof Fighter fighter) {
            components.put(FIGHT, ";" + EntityPrint.print(";;", fighter.attackDamage(), fighter.takesRevenge(), entity.hasComponent(BEE),
                    fighter.attackRange(), fighter.attackCooldown()));
            if (entity instanceof Territorial territorial) components.put(HOSTILE, ";" + territorial.defendCooldown()
                    + ";;" + territorial.enemy() + ";;" + (territorial.noticeable() ? 1 : 0));
        }
        if (entity instanceof Hunter) {
            if (entity instanceof WalkingHunter hunter) {
                components.put(HUNT, ";" + EntityPrint.print(";;", hunter.range(), EntityPrint.printArray(";;", hunter.preys(),
                        Classifiable::getClassification), hunter.huntsYoung(), hunter.huntsOld()));
            } else {
                if (entity.goesUnderwater() && !entity.goesOverwater()) components.put(FISH_HUNT, "");
                else if (entity.hasEggStage()) components.put(BIRD_HUNT, "");
            }
        }
        if (entity instanceof WalkingHunter predator) if (predator.preys() != null && predator.preys().length > 0) {
            if (entity.goesUnderwater() && predator.preys()[0].equals(VanillaClassification.SMALL_FISH))
                components.put(FISH_HUNT, "");
            else {
                if (entity.hasEggStage()) components.put(BIRD_HUNT, "");
                else components.put(HUNT, ";" + EntityPrint.print(";;", predator.range(), EntityPrint.printArray(";;", predator.preys(),
                        Classifiable::getClassification), predator.huntsYoung(), predator.huntsOld()));
            }
        }

        ANNOTATIONS.forEach((annotation, component) -> {
            if (entity.getClass().isAnnotationPresent(annotation)) components.put(component, "");
        });
        return components;
    }
}
