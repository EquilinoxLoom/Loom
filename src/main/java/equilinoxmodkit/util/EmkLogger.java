package equilinoxmodkit.util;

import equilinoxmodkit.EMK;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class EmkLogger {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final StringBuilder tempString = new StringBuilder();

    private EmkLogger() {}

    public static void log(Object... msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildPrefix(Thread.currentThread().getStackTrace()[2].getClassName()));
        for (Object o : msg) sb.append(o.toString());
        tempString.append(sb.append(LINE_SEPARATOR));
        System.out.print(sb);
    }

    public static void logAdv(String callerName, Object... msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildPrefix(callerName));
        for (Object o : msg) sb.append(o.toString());
        tempString.append(sb.append(LINE_SEPARATOR));
        System.out.print(sb);
    }

    public static void warn(Object... msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("ERROR: ").append(buildPrefix(Thread.currentThread().getStackTrace()[ 2 ].getClassName())).append(" > ");
        for (Object o : msg) sb.append(o.toString());
        sb.append("!").append(LINE_SEPARATOR);
        tempString.append(sb);
        System.err.print(sb);
    }

    public static void warnAdv(String callerName,Object... msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("ERROR: ").append(callerName).append(" > ");
        for (Object o : msg) sb.append(o.toString());
        sb.append("!").append(LINE_SEPARATOR);
        tempString.append(sb);
        System.err.print(sb);
    }

    public static void save(File file) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            tempString.insert(0,buildFileInfo());
            writer.write(tempString.toString());
        } catch(IOException ignored) {}
    }


    private static String buildPrefix(String className) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getTimeString()).append("] ");
        if (className.startsWith("equilinoxmodkit.")) {
            String[] tokens = className.split("\\.");
            sb.append("[EMK] [").append(tokens[ tokens.length - 1 ]).append("] ");
        } else {
            sb.append(className).append(" ");
        }
        return sb.toString();
    }

    private static String buildFileInfo() {
        return getDateString() + LINE_SEPARATOR +
                "Equilinox Mod Kit Version " + EMK.VERSION + " for Equilinox " + EMK.TESTED_EQUILINOX_VERSION + LINE_SEPARATOR +
                LINE_SEPARATOR;
    }

    private static String getTimeString() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private static String getDateString() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
    }
}
