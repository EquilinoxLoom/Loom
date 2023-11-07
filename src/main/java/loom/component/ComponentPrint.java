package loom.component;

import componentArchitecture.Component;
import componentArchitecture.ComponentBundle;

import java.util.List;

public interface ComponentPrint {
    <T extends Component> T getComponent(ComponentBundle bundle);

    List<ComponentPrint> getRequirements();

    String name();
}
