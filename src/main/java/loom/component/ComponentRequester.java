package loom.component;

import breedingTraits.FloatTrait;
import breedingTraits.FloatTraitBlueprint;
import breedingTraits.Trait;
import breedingTraits.TraitBlueprint;
import classification.Classifier;
import componentArchitecture.Component;
import componentArchitecture.ComponentBundle;
import loom.entity.Classifiable;
import loom.entity.weaver.EntityComponent;
import loom.equilinox.vanilla.VanillaComponent;
import loom.equilinox.vanilla.VanillaLoader;
import utils.CSVReader;
import utils.MyFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Used to retrieve component information for an entity.
 */
public class ComponentRequester {
    private ComponentBundle bundle;
    private LoomComponent component;

    final Set<PrintableComponent> requirements = new HashSet<>();
    final Map<String, TraitBlueprint> traits = new HashMap<>();

    /**
     * Requests and retrieves the Equilinox component represented by the given vanilla component
     * obtained from the entity bundle.
     *
     * @param component The vanilla component to request.
     * @param <T>       The type of the requested component.
     * @return The Equilinox component represented by the vanilla component, or null if not present in the bundle.
     * @see VanillaComponent#getComponent(ComponentBundle)
     */
    public <T extends Component> T requestComponent(VanillaComponent component) {
        this.requirements.add(component);
        return component.getComponent(bundle);
    }

    /**
     * Requests and retrieves the Equilinox component represented by the given custom component
     * obtained from the entity bundle.
     *
     * @param component The custom component to request.
     * @param <T>       The type requested component.
     * @return The Equilinox component represented by the custom component, or null if not present in the bundle.
     * @see LoomComponent#getComponent(ComponentBundle)
     */
    public <T extends LoomComponent> T requestComponent(T component) {
        this.requirements.add(component);
        return component.getComponent(bundle);
    }

    public Trait requestTrait(TraitBlueprint trait) {
        if (this.traits.containsKey(trait.getName())) {
            return component.getTrait(traits.get(trait.getName()).getIndex());
        } else return createNewTrait(trait);
    }

    /**
     * Requests or retrieves a floating trait with the specified characteristics. If a trait with the given name already
     * exists, it returns that trait; otherwise, it creates a new trait based on the provided parameters and adds it to
     * the component's blueprint.<br>
     * Floating traits are used in various components in the game, such as size, edibility, color, and speed traits.
     *
     * @param name    The name of the floating trait.
     * @param price   The price for changing the trait, given as a percentage. Prices escalate rapidly.
     *                For example, reducing the size trait of an entity by 5% costs around 2000 points,
     *                and changing it by 15% costs more than 24000 points.
     * @return The requested or newly created floating trait.
     * @throws RuntimeException if the value of the price parameter is unexpected.
     */
    public LoomTrait requestFloatingTrait(String name, double price) {
        if (this.traits.containsKey(name)) {
            return new LoomTrait((FloatTrait) component.getTrait(traits.get(name).getIndex()));
        } else {
            double x;
            try {
                x = derivative(price);
            } catch (RuntimeException e) {
                throw new RuntimeException("Error finding derivative for trait " + name + " try changing it.");
            }
            FloatTraitBlueprint trait = new FloatTraitBlueprint(name, 1, (float) x, 1);
            return new LoomTrait((FloatTrait) createNewTrait(trait));
        }
    }

    public Object requestEntityComponentBlueprint(EntityComponent component) {
        try {
            return VanillaLoader.getFunctionByClass(component.getClass()).apply(new CSVReader(new MyFile(component.build())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Trait createNewTrait(TraitBlueprint trait) {
        trait.setIndex(traits.size());
        component.getBlueprint().addTrait(trait);
        this.traits.put(trait.getName(), trait);
        return component.getTrait(trait.getIndex());
    }

    private static double derivative(double price) throws RuntimeException {
        double x = 1.0;

        for (int i = 0; i < 100; i++) {
            double sum = 0.0, derivative = 0.0;

            for (int j = 0; j < 100; j++) {
                sum += Math.pow(x, 5 - j) - Math.pow(x, 1 - j);
                derivative += (5 - j) * Math.pow(x, 4 - j) - (1 - j) * (1 / Math.pow(x, j));
            }

            if (Math.abs(price - 300 * sum) < 1e-6) return x;
            x -= (sum - price / 300.0) / derivative;
        }

        throw new RuntimeException();
    }

    public void setBundle(ComponentBundle bundle) {
        this.bundle = bundle;
    }

    public void setComponent(LoomComponent component) {
        this.component = component;
    }

    /**
     * @param classification the lineage to be matched
     * @return whether the entity classification belongs to the specified lineage.
     * @see Classifiable#belongs(Classifiable)
     */
    public boolean belongs(Classifiable classification) {
        return bundle.getEntity().getBlueprint().getClassification()
                .isTypeOf(Classifier.getClassification(classification.getClassification()));
    }
}
