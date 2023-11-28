package loom;

import baseMovement.MovementComp;
import eating.EatingAnimation;
import eating.StandardEatingAi;
import equilinox.ducktype.EatingAnimationReference;
import gameManaging.GameManager;
import toolbox.Transformation;

public abstract class CustomEatingAnimation implements EatingAnimation, EatingAnimationReference {
    protected Transformation transform;
    protected MovementComp mover;
    protected StandardEatingAi eater;

    public final void create(Transformation transform, MovementComp mover, StandardEatingAi eater) {
        this.transform = transform;
        this.mover = mover;
        this.eater = eater;
    }

    @Override
    public final boolean doNomming(boolean targetAvailable) {
        return update(GameManager.getGameSeconds(), targetAvailable);
    }

    @Override
    public void interrupt() {
        this.transform.setXRotation(0.0F);
    }

    /**
     * This method is invoked by {@link StandardEatingAi} when the target is reached and it's available.
     *
     * @param frame           Float representing the length of the game second.
     * @param targetAvailable Indicates whether the target is available for consumption.
     *                        It should be true if the target is not null, not dead, not grabbed, and can be eaten.
     * @return whether the eating animation was finished.
     */
    public abstract boolean update(float frame, boolean targetAvailable);

    @Override
    public final int ordinal() {
        return hashCode();
    }
}
