package loom.entity.weaver;

import loom.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final List<String> header, body, footer;

    public EntityProcessor(Entity entity) {
        this.header = new ArrayList<>();
        header.add(String.valueOf(entity.getSize()));
        if (entity.isModelRandomized()) header.add("-1;;-1;;1");
        if (entity.getIconSize() != 1) header.add("0;;" + entity.getIconSize());
        if (entity.getIconHeight() != 0) header.add(String.valueOf(entity.getIconHeight()));
        header.add(entity.getLineage().getClassification());
        header.add((entity.goesUnderwater() ? 1 : 0) + ";" + (entity.goesOverwater() ? 1 : 0));
        if (entity.getWaterHeightRequired() != 0) header.add(String.valueOf(entity.getWaterHeightRequired()));

        this.body = ModelReader.readEntityModel(entity);

        this.footer = ModelReader.readEntityComponents(entity).entrySet().stream()
                .filter(e -> e.getValue() != null)
                .filter(e -> e.getKey().getRequirements().stream().allMatch(entity::hasComponent))
                .map(e -> e.getKey().name() + (e.getValue().isEmpty() ? "" : ";" + e.getValue()))
                .collect(Collectors.toList());
    }

    public String build() {
        return String.join("\n", header) + "\n" + String.join("", body) + footer.size() + "\n" + String.join("\n", footer);
    }
}
