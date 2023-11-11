package loom.component;

import componentArchitecture.Component;
import componentArchitecture.ComponentBundle;

import java.util.List;

public interface PrintableComponent {
    /**
     * @return the equilinox component represented by this printable got from the bundle, or {@code null}
     * if it's not present in the bundle.
     */
    <T extends Component> T getComponent(ComponentBundle bundle);

    /**@return a list representing the components loaded at the creation of this component.*/
    List<PrintableComponent> getRequirements();

    String name();
}
