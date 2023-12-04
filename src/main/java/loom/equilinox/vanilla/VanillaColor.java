package loom.equilinox.vanilla;

import toolbox.Colour;

import java.awt.*;

public enum VanillaColor {
    RED(new Color(241, 145, 145)),
    RUBY_RED(new Color(162, 79, 79)),
    YELLOW(new Color(213, 211, 128)),
    ORANGE(new Color(225, 180, 126)),
    DARK_ORANGE(new Color(173, 103, 85)),
    LIGHT_BLUE(new Color(119, 186, 196)),
    DARK_BLUE(new Color(80, 84, 139)),
    PINK(new Color(238, 161, 210)),
    LIGHT_PINK(new Color(255, 198, 241)),
    PURPLE(new Color(182, 137, 213)),
    DARK_PURPLE(new Color(76, 55, 88)),
    LILAC(new Color(204, 194, 247)),
    CYAN(new Color(136, 227, 202)),
    BEIGE(new Color(209, 192, 162)),
    MUD(new Color(105, 102, 83)),
    LIGHT_BROWN(new Color(156, 129, 97)),
    BROWN(new Color(130, 104, 78)),
    DARK_BROWN(new Color(78, 61, 61)),
    YELLOW_GREEN(new Color(167, 187, 112)),
    DARK_GREEN(new Color(72, 111, 68)),
    BLUE_GREEN(new Color(88, 147, 112)),
    BRIGHT_GREEN(new Color(149, 245, 161)),
    LIGHT_GREEN(new Color(141, 227, 141)),
    GREEN(new Color(93, 150, 93)),
    PALE_GREEN(new Color(202, 222, 133)),
    MUD_GREEN(new Color(122, 133, 83)),
    GOLD(new Color(179, 163, 79)),
    WHITE(new Color(230, 230, 230)),
    GREY(new Color(128, 128, 128)),
    LIGHT_GREY(new Color(191, 191, 191)),
    DARK_GREY(new Color(64, 64, 64)),
    BLACK(new Color(38, 38, 38)),
    NEON_RED(new Color(255, 102, 102)),
    NEON_ORANGE(new Color(255, 202, 91)),
    NEON_YELLOW(new Color(255, 245, 102)),
    NEON_GREEN(new Color(194, 255, 102)),
    NEON_BLUE(new Color(102, 255, 235)),
    NEON_PURPLE(new Color(125, 85, 255)),
    NEON_PINK(new Color(255, 102, 235));

    public final Color color;

    VanillaColor(Color color) {
        this.color = color;
    }

    public static String getClosestVanilla(Color color) {
        String name = null;
        double d = Math.sqrt(3);
        for (VanillaColor vc : values()) {
            double diff = distance(vc.color, color);
            if (diff < d) {
                d = diff;
                name = vc.name();
            }
        }
        return name;
    }

    public static double distance(Color c1, Color c2) {
        float[] rgb1 = c1.getRGBColorComponents(null);
        float[] rgb2 = c2.getRGBColorComponents(null);

        double x = rgb2[0] - rgb1[0];
        double y = rgb2[1] - rgb1[1];
        double z = rgb2[2] - rgb1[2];

        return Math.sqrt(x * x + y * y + z * z);
    }

    public static String isVanilla(Color color) {
        String name = null;
        double d = Math.sqrt(3);
        for (VanillaColor vc : values()) {
            double diff = distance(vc.color, color);
            if (diff < d) {
                d = diff;
                name = vc.name();
            }
        }
        if (d < 0.25) return name;
        return "CUSTOM;" + color.getRed() / 255f + ";" + color.getGreen() / 255f + ";" + color.getBlue() / 255f;
    }

    public static Colour parseColor(Color color) {
        return new Colour(color.getRed(), color.getGreen(), color.getBlue(), true);
    }
}
