package loom.entity.animal;

import loom.entity.Entity;
import loom.entity.weaver.EntityComponent;

@SuppressWarnings("unused")
public class Movement extends EntityComponent {
    public static final Movement REGULAR_FISH_MOVEMENT = newSwimmingMovement(0.5f, 2, -12, 12, 2, 4, false, 1, 0.25f);

    private final int id;

    private String overrideAi = null;

    public Movement(int id, Object... os) {
        this.id = id;
        add(id, os);
    }

    public String getMovementAi(Entity entity) {
        if (overrideAi == null) {
            return getBaseAi(entity);
        }
        if (overrideAi.matches("WALKING_BIRD(;;.*;;.*;;)?")) {
            overrideAi += entity.goesUnderwater() ? 0 : 1;
        }
        return overrideAi;
    }

    private String getBaseAi(Entity entity) {
        if (entity.hasEggStage() && entity.goesOverwater()) {
            if (id == 12) {
                return "BIRD";
            } else if (entity.getWaterHeightRequired() < 0.1f) {
                return "WALKING_BIRD;;2;;4.5;;" + (entity.goesUnderwater() ? 0 : 1);
            } else {
                return "TORTOISE";
            }
        } else if (entity.goesOverwater() && entity.goesUnderwater()) {
            return "PATROL_WITH_SWIM";
        } else if (entity.goesUnderwater()) {
            return "SWIM";
        } else {
            return "PATROL";
        }
    }


    private static Movement regularMovement(int id, float speed, int xRotation, float minRotation, float maxRotation,
                                            float rotationSpeed, Object... args) {
        return new Movement(id, speed, xRotation, minRotation, maxRotation, rotationSpeed, args);
    }

    public static Movement newFrogMovement(float speed, float bouncePower, float waitTime, float bounciness) {
        return new Movement(6, speed, bouncePower, waitTime, bounciness);
    }

    public static Movement newRabbitMovement() {
        return new Movement(7);
    }

    public static Movement newFlouncerMovement(float speed, float rotationSpeed, float bouncePower, float bounceRotation, float standardHeight) {
        return new Movement(8, speed, rotationSpeed, bouncePower, bounceRotation, standardHeight);
    }

    public static Movement newRegularMovement(float speed, int xRotation, float minRotation, float maxRotation,
                                              float rotationSpeed) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed);
    }

    public static Movement newAmphibiousMovement(float speed, int xRotation, float minRotation, float maxRotation,
                                                 float rotationSpeed, float swimmingHeight) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed, swimmingHeight);
    }

    public static Movement newAmphibiousMovement(float speed, int xRotation, float minRotation, float maxRotation,
                                                 float rotationSpeed, float swimmingHeight, boolean hasEggStage,
                                                 float swimmingMultiplier) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed, swimmingHeight,
                hasEggStage ? 1 : 0, swimmingMultiplier);
    }

    @Deprecated
    public static Movement newAmphibiousMovement(float speed, int xRotation, float minRotation, float maxRotation,
                                                 float rotationSpeed, float swimmingHeight, boolean hasEggStage,
                                                 float swimmingMultiplier, int agilityMultiplier) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed, swimmingHeight,
                hasEggStage ? 1 : 0, swimmingMultiplier, agilityMultiplier);
    }

    public static Movement newSwimmingMovement(float speed, int xRotation, float minRotation, float maxRotation,
                                                 float rotationSpeed, float swimmingHeight, boolean hasEggStage,
                                                 float swimmingMultiplier, float agilityMultiplier) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed, swimmingHeight,
                hasEggStage ? 1 : 0, swimmingMultiplier, agilityMultiplier);
    }

    public static Movement newFlyMovement() {
        return new Movement(10);
    }

    public static Movement newBeeMovement(float height) {
        return new Movement(11, height).override("BEE");
    }

    public static Movement newAerialMovement() {
        return new Movement(12);
    }

    /**
     * Base gliding velocity is 0.6
     */
    public static Movement newAerialMovement(float glideVelocity) {
        return new Movement(12, -glideVelocity);
    }

    public static Movement newGallopMovement() {
        return new Movement(13);
    }

    public static Movement newBouncingMovement(float speed, float rotationSpeed, float bouncePower) {
        return new Movement(14, speed, rotationSpeed, bouncePower);
    }

    public static Movement newWaddleMovement(float speed, float rotationSpeed, float bouncePower) {
        return new Movement(15, speed, rotationSpeed, bouncePower);
    }

    public static Movement newFloatyMovement() {
        return new Movement(21);
    }

    public static Movement newDolphinMovement(int id, float speed, int xRotation, float minRotation, float maxRotation,
                                              float rotationSpeed) {
        return regularMovement(45, speed, xRotation, minRotation, maxRotation, rotationSpeed).override("DOLPHIN");
    }

    private Movement override(String ai) {
        this.overrideAi = ai;
        return this;
    }

    public Movement setMeerkatMovementAi() {
        return override("MEERKAT");
    }

    public Movement setMeerkatMovementAi(float minIdleTime, float maxIdleTime) {
        return override("MEERKAT;;" + minIdleTime + ";;" + maxIdleTime);
    }

    public Movement setWalkingBirdMovement(float minIdleTime, float maxIdleTime) {
        return override("WALKING_BIRD;;" + minIdleTime + ";;" + maxIdleTime + ";;");
    }

    public Movement setFlyingAi(float circleRotation, float minimumCircleTime) {
        return override("BIRD;;" + circleRotation + ";;" + minimumCircleTime);
    }
}
