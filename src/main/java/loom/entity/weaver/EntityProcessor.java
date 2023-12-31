package loom.entity.weaver;

import biomes.Biome;
import loom.LoomMod;
import loom.component.PrintableComponent;
import loom.entity.Classifiable;
import loom.entity.Entity;
import loom.entity.Tooltip;
import loom.entity.behaviour.*;
import loom.entity.item.Timeout;
import loom.entity.life.Living;
import loom.entity.life.LivingEntity;
import loom.entity.other.Named;
import loom.entity.other.Peacock;
import loom.entity.other.Stinger;
import loom.entity.plant.BiomeSpreader;
import loom.entity.plant.FloaterPlant;
import loom.entity.plant.Flytrap;
import loom.entity.plant.Sunflower;
import loom.entity.structure.Projectile;
import loom.equilinox.vanilla.VanillaColor;
import loom.equilinox.vanilla.VanillaComponent;
import loom.equilinox.vanilla.VanillaSpecie;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static loom.equilinox.vanilla.VanillaComponent.*;

/**
 * Within game files, entity blueprints are stored in a special format in which,
 * data is split by semicolons and line breaks as follows:
 *
 * <p>
 *     Icon information...<br>
 *     Entity classification<br>
 *     Whether the entity can be placed underwater
 *     + semicolon + Whether it can be placed overwater
 *     + semicolon + Water level requirement<br>
 *     Entity model information...<br>
 *     Then it repeats the following for each component: Component name
 *     + double semicolons + Component required and optional parameters split by double semicolons + line break
 * </p>
 */
public final class EntityProcessor {
    private final Map<PrintableComponent, String> components;
    private final Entity entity;

    private final List<String> header, body, footer;

    public EntityProcessor(Entity entity) {
        this.entity = entity;
        this.components = entity.getComponents();
        this.header = new ArrayList<>();
        this.body = new ArrayList<>();
        this.footer = new ArrayList<>();
    }

    public String build() {
        buildHeader(); buildBody(); buildFooter();
        return String.join("\n", header) + "\n" + String.join("", body) + String.join("\n", footer);
    }

    private String join(Object... os) {
        return EntityPrint.print(";;", os);
    }

    private void addFooter(String str) {
        footer.add(str);
    }

    public void buildHeader() {
        header.add(String.valueOf(entity.getSize()));
        if (entity.isModelRandomized()) header.add("-1;;-1;;1");
        if (entity.getIconSize() != 1) header.add("0;;" + entity.getIconSize());
        if (entity.getIconHeight() != 0) header.add(String.valueOf(entity.getIconHeight()));
        header.add(entity.getLineage().getClassification());
        header.add((entity.goesUnderwater() ? 1 : 0) + ";" + (entity.goesOverwater() ? 1 : 0));
        if (entity.getWaterHeightRequired() != 0) header.add(String.valueOf(entity.getWaterHeightRequired()));
    }

    public void buildBody() {
        if (entity.getModelPaths() == null || entity.getModelPaths().length == 0) {
            throw new RuntimeException("Model from entity of id " + entity.getId() + " is null or empty.");
        }

        if (VanillaSpecie.isIdAvailable(entity.getId())) {
            throw new RuntimeException("Id:" + entity.getId() + " is not available.");
        }

        final List<List<List<String>>> model = loadModel();

        List<List<Integer>> pointerLists = new ArrayList<>();
        List<List<String>> faceLists = new ArrayList<>();
        List<List<Float>> vertexLists = new ArrayList<>();
        List<List<Float>> normalLists = new ArrayList<>();
        List<List<Float>> colourLists = new ArrayList<>();
        int counter = 0;

        for (List<List<String>> stage : model) {
            List<String> objLines = stage.get(0);
            List<String> mtlLines = stage.get(1);

            List<Integer> pointers = new ArrayList<>();
            List<String> faces = new ArrayList<>();
            List<Float> vertices = new ArrayList<>();
            List<Float> normals = new ArrayList<>();
            List<Float> colours = new ArrayList<>();

            for (String obj : objLines) {
                String[] objSplit = obj.split(" ");
                switch (objSplit[0]) {
                    case "v":
                        vertices.add(Float.parseFloat(objSplit[1]));
                        vertices.add(Float.parseFloat(objSplit[2]));
                        vertices.add(Float.parseFloat(objSplit[3]));
                        break;
                    case "vn":
                        normals.add(Float.parseFloat(objSplit[1]));
                        normals.add(Float.parseFloat(objSplit[2]));
                        normals.add(Float.parseFloat(objSplit[3]));
                        break;
                    case "usemtl":
                        faces.add("POINTER");
                        pointers.add(counter);

                        loadMtl(mtlLines, colours);
                        break;
                    case "f":
                        faces.add(objSplit[1]);
                        faces.add(objSplit[2]);
                        faces.add(objSplit[3]);
                        counter += 3;
                        break;
                }
            }

            pointers.add(counter);

            setNoMaterial(stage, model, colours);

            pointerLists.add(pointers);
            faceLists.add(faces);
            vertexLists.add(vertices);
            normalLists.add(normals);
            colourLists.add(colours);
        }

        body.add(entity.getModelPaths().length + "\n");
        for (int i = 0; i < pointerLists.size(); i++) {
            body.add(EntityPrint.print(";",
                    String.format(Locale.US, "%.4f", getMin(vertexLists.get(i), 0)),
                    String.format(Locale.US, "%.4f", getMin(vertexLists.get(i), 1)),
                    String.format(Locale.US, "%.4f", getMin(vertexLists.get(i), 2)),
                    String.format(Locale.US, "%.4f", getMax(vertexLists.get(i), 0)),
                    String.format(Locale.US, "%.4f", getMax(vertexLists.get(i), 1)),
                    String.format(Locale.US, "%.4f", getMax(vertexLists.get(i), 2)),
                    "1\n"));

            int pointerSize = pointerLists.get(i).size() - 1;
            body.add((faceLists.get(i).size() - pointerSize) + ";" + pointerSize);

            int point = colourLists.get(i).size() / 3 - 1;
            for (int j = 0, k = 1; j < faceLists.get(i).size(); j++) {
                if (faceLists.get(i).get(j).contains("/")) {
                    String[] convertedFaces = faceLists.get(i).get(j).split("/");
                    int faces0 = Integer.parseInt(convertedFaces[0]) - 1;
                    int faces1 = Integer.parseInt(convertedFaces[2]) - 1;
                    body.add(EntityPrint.print(";",
                            String.format(Locale.US, "%.4f", vertexLists.get(i).get(faces0 * 3)),
                            String.format(Locale.US, "%.4f", vertexLists.get(i).get(faces0 * 3 + 1)),
                            String.format(Locale.US, "%.4f", vertexLists.get(i).get(faces0 * 3 + 2)),
                            String.format(Locale.US, "%.4f", normalLists.get(i).get(faces1 * 3)),
                            String.format(Locale.US, "%.4f", normalLists.get(i).get(faces1 * 3 + 1)),
                            String.format(Locale.US, "%.4f", normalLists.get(i).get(faces1 * 3 + 2))));
                    if (j + 1 < faceLists.get(i).size() && !faceLists.get(i).get(j + 1).contains("POINTER")) body.add(";");
                }

                if (faceLists.get(i).get(j).contains("POINTER")) {
                    body.add(EntityPrint.print(";", "\n" +
                            (pointerLists.get(i).get(k) - pointerLists.get(i).get(k - 1)),
                            colourLists.get(i).get(point * 3).toString(),
                            colourLists.get(i).get(point * 3 + 1).toString(),
                            colourLists.get(i).get(point * 3 + 2).toString() + "\n"));
                    point--; k++;
                }
            }
            body.add("\n");
        }
    }

    private void setNoMaterial(List<List<String>> stage, List<List<List<String>>> model, List<Float> colours) {
        if (!entity.getMaterials().isEmpty() && !(entity.hasEggStage() && stage.equals(model.get(0)))) {
            for (int i = 0; i < colours.size(); i += 6) {
                colours.set(i, -1F); colours.set(i + 1, 0F); colours.set(i + 2, 0F);
            }
        }
    }

    private static void loadMtl(List<String> mtlLines, List<Float> colours) {
        for (String mtl : mtlLines) {
            String[] mtlSplit = mtl.split(" ");
            if (mtlSplit[0].equals("Kd")) {
                colours.add(Float.parseFloat(mtlSplit[1]));
                colours.add(Float.parseFloat(mtlSplit[2]));
                colours.add(Float.parseFloat(mtlSplit[3]));
            }
        }
    }

    private List<List<List<String>>> loadModel() {
        List<List<List<String>>> model = new ArrayList<>();
        for (String stage : entity.getModelPaths()) {
            List<List<String>> lists = new ArrayList<>();
            try {
                lists.add(readFile(stage + ".obj"));
                lists.add(readFile(stage + ".mtl"));
            } catch (NullPointerException e) {
                System.err.println("Couldn't find " + stage + ".obj and " + stage + ".mtl at resources folder.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            model.add(lists);
        }
        return model;
    }

    private static List<String> readFile(String path) throws IOException {
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
            }
        } else {
            System.err.println("File not found: " + path);
        }

        return lines;
    }


    private static float getMin(List<Float> floatList, int axis) {
        float min = floatList.get(axis);
        for (int i = axis; i < floatList.size(); i += 3)
            if (floatList.get(i) < min) min = floatList.get(i);
        return min;
    }

    private static float getMax(List<Float> floatList, int axis) {
        float max = floatList.get(axis);
        for (int i = axis; i < floatList.size(); i += 3)
            if (floatList.get(i) > max) max = floatList.get(i);
        return max;
    }

    public void buildFooter() {
        buildVanillaComponents();

        List<Map.Entry<PrintableComponent, String>> collect = components.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .filter(e -> e.getKey().getRequirements().stream().allMatch(entity::hasComponent))
                .collect(Collectors.toList());

        addFooter(String.valueOf(collect.size()));
        collect.forEach(e -> addFooter(e.getKey().name() + (e.getValue().isEmpty() ? "" : ";" + e.getValue())));
    }

    @SuppressWarnings("unchecked")
    private void buildVanillaComponents() {
        if (entity instanceof BiomeSpreader) {
            BiomeSpreader spreader = (BiomeSpreader) entity;
            try {
                components.put(SPREADER, Biome.valueOf(spreader.biome().name()).ordinal() + ";0.1;0.1;0.1;"
                        + spreader.strength() + ";" + spreader.distance());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Biome " + spreader.biome().name() + " was not registered");
            }
        }
        byAnnotation(PROJECTILE, Projectile.class);
        if (entity.getClass().isAnnotationPresent(Timeout.class))
            components.put(TIME_OUT, String.valueOf(entity.getClass().getAnnotation(Timeout.class).value()));
        if (!entity.getMaterials().isEmpty()) {
            Map<Color, Integer> map = new LinkedHashMap<>();
            int i = 0;
            for (Map.Entry<Color, Integer> entry : entity.getMaterials().entrySet()) {
                if (i == 1 && entity.getSecondNaturalColor() != null) {
                    map.put(entity.getSecondNaturalColor(), entity.getMaterials().values().iterator().next());
                }
                map.put(entry.getKey(), entry.getValue());
                i++;
            }
            components.put(MATERIAL, ";" + join(entity.getSecondNaturalColor() != null,
                    EntityPrint.printArray(";", map.entrySet().toArray(new Map.Entry[0]), e -> {
                        Map.Entry<Color, Integer> entry = (Map.Entry<Color, Integer>) e;
                        return VanillaColor.isVanilla(entry.getKey()) + ";" + entry.getValue();
                    })));
        }
        if (entity instanceof Tooltip) {
            Tooltip info = (Tooltip) entity;
            components.put(INFO, join(LoomMod.MOD_POINTER + ";" + info.name(), info.description(), info.price(),
                    info.dpPerMin(), info.range(), info.sound().toString().toLowerCase(Locale.ROOT)));
        }
        if (entity instanceof Living) {
            Living life = (Living) entity;

            EntityComponent print1 = new EntityComponent() {};
            float[] factors = life.populationFactors();
            print1.addSub(life.population(), life.lifespan(), factors.length + (factors.length > 0 ? ";"
                            + IntStream.range(0, factors.length).mapToDouble(i -> factors[i]).mapToObj(String::valueOf)
                            .collect(Collectors.joining(";")) : ""),
                    life.breedingMaturity(), life.breedingCooldown());
            if (life.getEvolution() == null) print1.addSub(";-1;" + life.getDeath().build(), life.getWellBeing().build());
            else print1.addSub(";" + life.getEvolution().parent, life.getEvolution().points,
                    life.getEvolution().build() + ";" + life.getDeath().build(), life.getWellBeing().build());
            if (life.getDefense() != 0) print1.addSub(life.getDefense());
            components.put(LIFE, print1.build().replaceFirst(String.valueOf(components.size()), ""));

            EntityComponent print2 = new EntityComponent() {
            };
            print2.addSub(life.dynamic(), life.growthTime(), life.stages());
            if (!life.dynamic()) print2.addSub(life.subStages());
            components.put(GROWTH, print2.build());
        }
        byAnnotation(FLY_TRAP, Flytrap.class);
        if (entity instanceof FruitProducer) {
            FruitProducer fruit = (FruitProducer) entity;
            components.put(FRUITER, ";" + fruit.modelIndex() + ";;" + fruit.stages()
                    + (fruit.time() != 5 ? ";;" + fruit.time() : ""));
        }
        byAnnotation(LILY, FloaterPlant.class);
        byAnnotation(SUN_FACER, Sunflower.class);
        if (entity instanceof Builder) {
            Builder builder = (Builder) entity;
            assert entity instanceof LivingEntity;
            components.put(BUILDER, ";" + join(builder.structure().getId(), builder.points(),
                    builder.structure() instanceof Entity
                            ? ((Entity) builder.structure()).hasComponent(PERCHER)
                            : builder.structure().getId() == VanillaSpecie.NEST.getId(),
                    builder.buildTime(), (builder.age() - 1) / ((LivingEntity) entity).stages()) +
                    (builder.buildingHour() != 0 ? ";;" + builder.buildTime() : ""));
        }
        if (entity.hasComponent(FOOD)) components.put(FOOD, EntityPrint.printArray(";", entity.getFoodInfo().values()
                .toArray(new String[0]), s -> s));
        byAnnotation(STINGING, Stinger.class);
        byAnnotation(CHARGE, TreeCharger.class);
        byAnnotation(PEACOCK, Peacock.class);
        if (entity instanceof Holder) components.put(EQUIP, ";" + EntityPrint.printArray(";;",
                ((Holder) entity).positions(), v -> v.x + ";" + v.y + ";" + v.z));
        byAnnotation(NAME, Named.class);
        byAnnotation(BEAVER, DenBuilder.class);
        if (entity instanceof Fighter) {
            Fighter fighter = (Fighter) entity;
            components.put(FIGHT, ";" + join(fighter.attackDamage(), fighter.takesRevenge(), entity.hasComponent(BEE),
                    fighter.attackRange(), fighter.attackCooldown()));
            if (entity instanceof Territorial) {
                Territorial territorial = (Territorial) fighter;
                components.put(HOSTILE, ";" + territorial.defendCooldown() + ";;" + territorial.enemy() + ";;"
                        + (territorial.noticeable() ? 1 : 0));
            }
        }
        if (entity instanceof Predator) {
            Predator predator = (Predator) entity;
            if (entity.goesUnderwater()) components.put(FISH_HUNT, "");
            if (entity.goesOverwater()) {
                if (entity.hasEggStage()) components.put(BIRD_HUNT, "");
                else components.put(HUNT, ";" + join(predator.range(), EntityPrint.printArray(";;", predator.preys(),
                                Classifiable::getClassification), predator.huntsYoung(), predator.huntsOld()));
            }
        }
    }

    private void byAnnotation(VanillaComponent component, Class<? extends Annotation> annotation) {
        if (entity.getClass().isAnnotationPresent(annotation)) components.put(component, "");
    }
}
