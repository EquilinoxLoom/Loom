package loom.entity.weaver;

import equilinox.VanillaComponent;
import equilinox.classification.Classifiable;
import equilinox.classification.Family;
import loom.Util;
import loom.entity.Entity;
import loom.entity.animal.*;
import loom.entity.life.BiomeSpreader;
import loom.entity.life.LivingEntity;
import loom.entity.life.Tooltip;
import loom.entity.other.*;
import loom.entity.plant.FruitProducer;

import java.awt.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static equilinox.VanillaComponent.*;

public final class EntityProcessor implements Printable {
    private final Map<VanillaComponent, String> components;
    private final Entity entity;

    private final List<String> header, body, footer;

    public EntityProcessor(Entity entity) {
        this.entity = entity;
        this.components = entity.getComponents();
        this.header = new ArrayList<>();
        this.body = new ArrayList<>();
        this.footer = new ArrayList<>();
    }

    @Override
    public String build() {
        buildHeader();


        buildFooter();
        return join(header) + "\n" + join(body) + "\n" + String.join("\n", footer);
    }

    public void addBody(Object... os) {
        body.add(joined(os));
    }

    private void addHeader(Object... os) {
        header.add(joined(os));
    }

    private void addFooter(String str) {
        footer.add(str);
    }

    @Override
    public String joined(Object... os) {
        return Arrays.stream(os).filter(Objects::nonNull).map(o -> {
            if (o instanceof Boolean) return ((boolean) o) ? "1" : "0";
            return String.valueOf(o);
        }).collect(Collectors.joining(joiner()));
    }

    private String join(List<String> list) {
        return String.join(joiner(), list);
    }

    public void buildHeader() {
        addHeader(entity.getSize());
        if (entity.isModelRandomized()) addHeader(-1, -1, 1);
        if (entity.getIconSize() != 1) addHeader(0, entity.getIconSize());
        if (entity.getIconHeight() != 0) addHeader(entity.getIconHeight() + "\n");
        addHeader(entity.getLineage().getClassification() + "\n");
        if (entity.goesUnderwater() || entity.goesOverwater()) {
            addHeader((entity.goesUnderwater() ? 1 : 0) + ";" + (entity.goesOverwater() ? 1 : 0));
        } else addHeader("0;1");
        addHeader((entity.goesUnderwater()) + ";" + (entity.goesOverwater()));
        if (entity.getWaterHeightRequired() != 0) addHeader(entity.getWaterHeightRequired());
    }

    public void buildFooter() {
        if (entity instanceof BiomeSpreader) {
            BiomeSpreader spreader = (BiomeSpreader) entity;
            components.put(SPREADER, spreader.biome() + ";0.1;0.1;0.1;" + spreader.strength() + ";"
                    + spreader.distance());
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
            components.put(MATERIAL, ";" + joined(entity.getSecondNaturalColor() != null, Printable.printArray(";",
                    map.entrySet().toArray(new Map.Entry[0]), e -> {
                        Map.Entry<Color, Integer> entry = (Map.Entry<Color, Integer>) e;
                        return Util.isVanilla(entry.getKey()) + ";" + entry.getValue();
                    })));
        }
        if (entity instanceof Tooltip) {
            Tooltip info = (Tooltip) entity;
            components.put(INFO, joined("EMBROIDER;" + info.name(), info.description(), info.price(), info.dpPerMin(),
                    info.range(), info.sound()));
        }
        if (entity instanceof LivingEntity) {
            LivingEntity life = (LivingEntity) entity;

            EntityComponent print1 = new EntityComponent();
            float[] factors = life.populationFactors();
            print1.addSub(life.population(), life.lifespan(), factors.length + (factors.length > 0 ? ";"
                            + IntStream.range(0, factors.length).mapToDouble(i -> factors[i]).mapToObj(String::valueOf)
                            .collect(Collectors.joining(";")) : ""),
                    life.breedingMaturity(), life.breedingCooldown());
            if (life.evolution == null) print1.addSub("-1;" + life.getDeath().build(), life.satisfaction.build());
            else print1.addSub(life.evolution.parent, life.evolution.points, life.evolution.build() + ";" +
                    life.getDeath().build(), life.satisfaction.build());
            if (life.getDefense() != 0) print1.addSub(life.getDefense());
            components.put(LIFE, print1.build());

            EntityComponent print2 = new EntityComponent();
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
        byAnnotation(LILY, Floater.class);
        byAnnotation(SUN_FACER, Sunflower.class);
        if (entity instanceof Builder) {
            Builder builder = (Builder) entity;
            components.put(BUILDER, ";" + Printable.print(";;", builder.structure().getId(), builder.points(),
                    builder.structure() instanceof Entity
                            ? ((Entity) builder.structure()).hasComponent(PERCHER)
                            : builder.structure().getId() == Family.NEST.getId(),
                    builder.buildTime(), (builder.age() - 1) / ((LivingEntity) entity).stages()) +
                    (builder.buildingHour() != 0 ? ";;" + builder.buildTime() : ""));
        }
        if (entity.hasComponent(FOOD)) components.put(FOOD, Printable.printArray(";", entity.getFoodInfo().values()
                .toArray(new String[0]), s -> s));
        byAnnotation(STINGING, Stinger.class);
        byAnnotation(CHARGE, TreeCharger.class);
        byAnnotation(PEACOCK, Peacock.class);
        if (entity instanceof Holder) components.put(EQUIP, ";" + Printable.printArray(";;",
                ((Holder) entity).positions(), v -> v.x + ";" + v.y + ";" + v.z));
        byAnnotation(NAME, Named.class);
        byAnnotation(BEAVER, DenBuilder.class);
        if (entity instanceof Fighter) {
            Fighter fighter = (Fighter) entity;
            components.put(FIGHT, ";" + Printable.print(";;", fighter.attackDamage(), fighter.takesRevenge(),
                    entity.hasComponent(BEE), fighter.attackRange(), fighter.attackCooldown()));
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
                else components.put(HUNT, ";" + Printable.print(";;", predator.range(),
                        Printable.printArray(";;", predator.preys(), Classifiable::getClassification),
                        predator.huntsYoung(), predator.huntsOld()));
            }
        }
        components.entrySet().stream().filter(e -> e.getValue() != null).filter(e -> e.getKey().check(entity))
                .forEach(e -> addFooter(e.getKey().name() + (e.getValue().isEmpty() ? "" : ";" + e.getValue())));
    }

    private void byAnnotation(VanillaComponent component, Class<? extends Annotation> annotation) {
        if (entity.getClass().isAnnotationPresent(annotation)) components.put(component, "");
    }
}
