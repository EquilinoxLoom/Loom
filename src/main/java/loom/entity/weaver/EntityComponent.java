package loom.entity.weaver;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityComponent {
    protected EntityComponent() {}

    private final List<String> print = new ArrayList<>();

    private final List<String> printSub = new ArrayList<>();

    protected String getDelimiter() {
        return ";";
    }

    public void add(Object... os) {
        print.add(EntityPrint.print(";;", os));
    }

    public void addSub(Object... os) {
        print.add(EntityPrint.print(";;", os));
    }

    public String build() {
        if (!printSub.isEmpty()) print.add(EntityPrint.print(";;", printSub.toArray()));

        String build;

        if (print.isEmpty()) build = "0";
        else if (print.size() == 1) build = getDelimiter() + print.get(0);
        else build = EntityPrint.printArray(getDelimiter(), print.toArray(new String[0]), s -> s);

        print.clear();
        printSub.clear();

        return build;
    }
}
