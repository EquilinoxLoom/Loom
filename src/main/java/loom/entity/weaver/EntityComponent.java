package loom.entity.weaver;

import java.util.ArrayList;
import java.util.List;

public class EntityComponent implements Printable {
    protected EntityComponent() {}

    private final List<String> print = new ArrayList<>();

    private final List<String> printSub = new ArrayList<>();

    @Override
    public String build() {
        if (!printSub.isEmpty()) print.add(join(printSub.toArray()));

        String build;

        if (print.size() == 0) build = "0";
        else if (print.size() == 1) build = getDelimiter() + print.get(0);
        else build = Printable.printArray(getDelimiter(), print.toArray(new String[0]), s -> s);

        print.clear();
        printSub.clear();

        return build;
    }

    protected String getDelimiter() {
        return ";";
    }

    public void add(Object... os) {
        print.add(join(os));
    }

    public void addSub(Object... os) {
        printSub.add(join(os));
    }
}
