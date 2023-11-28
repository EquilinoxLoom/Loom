package equilinox;

/*
/**
 * A requirement for an entity to be evolved from its parent entity. Entities often share common evolution requirements,
 * such as the {@code Altitude} requirement, which implies that the entity must be in a specified height from sea level
 * to evolve. However, the height varies from entity to entity.
 *
 * <p>
 * To add a new parameter to the custom evolution requirement, users must create a constructor.
 * The allowable parameter types are: integers, floats, integer arrays, float arrays, {@link Vector3f}s,
 * booleans, longs, enums, {@link Classifiable}s, strings (excluding semicolons),
 * or also maps of allowable parameters as both key and value.
 * </p>
 *
 * <p>
 * Additionally, some parameters may be optional. In order to create additional constructors, 
 * users must create additional constructors with extra parameters and explicitly register
 * all optionals at {@link #create() create} method. The parameters must be registered in order.
 * </p>
 */
/*
public abstract class EvolutionRequirement {
    abstract boolean check(ComponentRequester requester);

    abstract String name();

    abstract String value();

    public Color color() {
        return new Color(237, 224, 178);
    }

    public boolean isSecret() {
        return false;
    }

    public final String compile() {
        new ParamRegistry(getClass());
    }
}
 */
