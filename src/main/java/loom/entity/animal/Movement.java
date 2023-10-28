package loom.entity.animal;

import loom.entity.weaver.EntityComponent;

@SuppressWarnings("unused")
public class Movement extends EntityComponent {
    public Movement(Object... os) {
        add(os);
    }
    
    private static Movement regularMovement(int id, float speed, float xRotation, float minRotation, float maxRotation,
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

    public static Movement newRegularMovement(float speed, float xRotation, float minRotation, float maxRotation,
                                   float rotationSpeed) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed);
    }

    public static Movement newAmphibiousMovement(float speed, float xRotation, float minRotation, float maxRotation,
                                      float rotationSpeed, float swimmingHeight) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed, swimmingHeight);
    }

    public static Movement newAmphibiousMovement(float speed, float xRotation, float minRotation, float maxRotation,
                                      float rotationSpeed, float swimmingHeight, boolean hasEggStage, float swimmingMultiplier) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed, swimmingHeight,
                hasEggStage ? 1 : 0, swimmingMultiplier);
    }

    public static Movement newAmphibiousMovement(float speed, float xRotation, float minRotation, float maxRotation,
                                      float rotationSpeed, float swimmingHeight, boolean hasEggStage,
                                      float swimmingMultiplier, float agilityMultiplier) {
        return regularMovement(9, speed, xRotation, minRotation, maxRotation, rotationSpeed, swimmingHeight,
                hasEggStage ? 1 : 0, swimmingMultiplier, agilityMultiplier);
    }

    public static Movement newFlyMovement() {
        return new Movement(10);
    }

    public static Movement newBeeMovement(float height) {
        return new Movement(11, height);
    }

    public static Movement newAerialMovement() {
        return new Movement(12);
    }

    /**Base gliding velocity is 0.6*/
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

    public static Movement newDolphinMovement(int id, float speed, float xRotation, float minRotation, float maxRotation,
                                   float rotationSpeed) {
        return regularMovement(45, speed, xRotation, minRotation, maxRotation, rotationSpeed);
    }
}
