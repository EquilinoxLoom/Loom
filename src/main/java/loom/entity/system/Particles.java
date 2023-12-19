package loom.entity.system;

import loom.entity.weaver.EntityComponent;

import javax.annotation.Nonnull;
import java.awt.*;

@SuppressWarnings("unused")
public class Particles extends EntityComponent {
    public final SpawnPattern spawn;
    public final float second, speed, gravity, duration, scale;

    public int texture = 0;

    public Color color = null;
    public boolean additive;
    public float in, out;

    public float x1, y1, z1, dirDev;
    public boolean direction = false;

    public float x2, x3, y2, z2, durDev, scaleDev, speedDev, udSpeed = 0;
    public boolean rotation = false, xRotation = false;

    public Particles(@Nonnull SpawnPattern spawn, float particlesPerSecond, float speed, float gravity, float duration,
                     float scale, int particleTexture, float xOffset, float yOffset, float zOffset,
                     float durationDeviation, float scaleDeviation, float speedDeviation) {
        this.spawn = spawn;
        this.second = particlesPerSecond;
        this.speed = speed;
        this.gravity = gravity;
        this.duration = duration;
        this.scale = scale;
        this.texture = particleTexture;
        this.x2 = xOffset;
        this.y2 = yOffset;
        this.z2 = zOffset;
        this.durDev = durationDeviation;
        this.scaleDev = scaleDeviation;
        this.speedDev = speedDeviation;
    }

    public Particles(@Nonnull Color color, boolean additive, float timeToFadeIn, float timeToFadeOut,
                     @Nonnull SpawnPattern spawn, float particlesPerSecond, float speed, float gravity, float duration,
                     float scale, float xOffset, float yOffset, float zOffset,
                     float durationDeviation, float scaleDeviation, float speedDeviation) {
        this.color = color;
        this.additive = additive;
        this.in = timeToFadeIn;
        this.out = timeToFadeOut;
        this.spawn = spawn;
        this.second = particlesPerSecond;
        this.speed = speed;
        this.gravity = gravity;
        this.duration = duration;
        this.scale = scale;
        this.x2 = xOffset;
        this.y2 = yOffset;
        this.z2 = zOffset;
        this.durDev = durationDeviation;
        this.scaleDev = scaleDeviation;
        this.speedDev = speedDeviation;
    }

    /**
     * x, y and z of the direction of the particles
     */
    public Particles setDirection(float xDir, float yDir, float zDir, float standardDeviation) {
        this.direction = true;
        this.x1 = xDir;
        this.y1 = yDir;
        this.z1 = zDir;
        this.dirDev = standardDeviation;
        return this;
    }

    public Particles setRandomRotation() {
        rotation = true;
        return this;
    }

    public Particles setRandomXRotationSpeed(float xRot) {
        rotation = true;
        xRotation = true;
        this.x3 = xRot;
        return this;
    }

    public Particles setUpDown(float speed) {
        this.udSpeed = speed;
        return this;
    }

    @Override
    public String toString() {
        return build();
    }

    @Override
    public String build() {
        if (udSpeed != 0) addSub(udSpeed);
        if (color != null) {
            addSub(color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
            addSub(additive, color.getAlpha(), in, out);
        } else addSub(texture);
        if (direction) addSub(1, x1 + ";" + y1 + ";" + z1, dirDev);
        addSub(x2 + ";" + y2 + ";" + z2, durDev, scaleDev, speedDev, rotation);
        if (rotation) {
            if (xRotation) addSub(1, x3);
            else addSub(0);
        }
        return super.build();
    }

    public static class SpawnPattern extends EntityComponent {
        public SpawnPattern(Object... args) {
            add(args);
        }

    }

    public static SpawnPattern newPoint() {
        return new SpawnPattern();
    }

    public static SpawnPattern newSphere(float radius) {
        return new SpawnPattern("1;" + radius);
    }

    /**
     * x, y and z of the direction of the line
     */
    public static SpawnPattern newLine(float length, float x, float y, float z) {
        return new SpawnPattern("2;" + length + ";" + x + ";" + y + ";" + z);
    }

    /**
     * x, y and z of the normals of the circle
     */
    public static SpawnPattern newCircle(float radius, float x, float y, float z) {
        return new SpawnPattern("3;" + radius + ";" + x + ";" + y + ";" + z);
    }
}
