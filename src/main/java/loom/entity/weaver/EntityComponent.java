package loom.entity.weaver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EntityComponent {
    protected final List<String> print = new ArrayList<>();

    private final List<Object> printSub = new ArrayList<>();

    protected EntityComponent() {}

    protected String getDelimiter() {
        return ";";
    }

    public void add(Object... os) {
        print.add(EntityPrint.print(";;", os));
    }

    public void addSub(Object... os) {
        printSub.addAll(Arrays.asList(os));
    }

    public String build() {
        if (!printSub.isEmpty()) print.add(EntityPrint.print(";;", printSub.toArray()));

        String build;

        if (print.isEmpty()) build = "0";
        else if (print.size() == 1) build = getDelimiter() + print.getFirst();
        else build = EntityPrint.printArray(getDelimiter(), print.toArray(new String[0]), s -> s);

        print.clear();
        printSub.clear();

        return build;
    }
}
