package loom.entity.weaver;

import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class ModelWriter {
    public static void writeEntityModel(File input, String output) {
        File log = Paths.get(".", "entities", "extracted", output).toFile();
        log.mkdirs();

        String[] model = readFile(input);

        int blueprints = Integer.parseInt(model[3]);

        for (int sub = 0, line = 5; sub < blueprints; sub++, line++) {
            int colors = Integer.parseInt(model[line++].split(";")[1]);

            Set<Vector3f> materials = new LinkedHashSet<>();

            Set<Vector3f> vertices = new LinkedHashSet<>();
            Set<Vector3f> normals = new LinkedHashSet<>();

            List<int[]> faces = new ArrayList<>();

            for (int color = 0; color < colors; color++) {
                String[] rgb = model[line++].split(";");

                materials.add(rgb[1].equals("-1") ? new Vector3f(0, 0, 0) : new Vector3f(
                        Float.parseFloat(rgb[1]), Float.parseFloat(rgb[2]), Float.parseFloat(rgb[3])
                ));

                String[] matrix = model[line++].split(";");

                for (int data = 0; data < matrix.length / 6; data++) {
                    int point = data * 6;
                    vertices.add(new Vector3f(Float.parseFloat(matrix[point]), Float.parseFloat(matrix[point + 1]), Float.parseFloat(matrix[point + 2])));
                    normals.add(new Vector3f(Float.parseFloat(matrix[point + 3]), Float.parseFloat(matrix[point + 4]), Float.parseFloat(matrix[point + 5])));

                    faces.add(new int[] { vertices.size(), normals.size(), materials.size() - 1});
                }
            }

            String name = output + "_" + sub;

            File obj = createFile(log, name, ".obj");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(obj))) {
                writer.write("mtllib " + name + ".mtl\n");
                writer.write("o " + name + "\n");

                for (Vector3f point : vertices)
                    writer.write("v " + values(point) + "\n");

                for (Vector3f point : normals)
                    writer.write("vn " + values(point) + "\n");

                int material = -1;
                for (int i = 0; i < faces.size(); i+=3) {
                    if (material != (material = faces.get(i)[2]))
                        writer.write("usemtl mat." + material + "\n");
                    writer.write(String.format("f %d//%d %d//%d %d//%d\n",
                            faces.get(i)[0], faces.get(i)[1],
                            faces.get(i+1)[0], faces.get(i+1)[1],
                            faces.get(i+2)[0], faces.get(i+2)[1]
                    ));
                }
            } catch (IOException e) {
                Log.error(LogCategory.LOG, "Attempt to create file " + name + ".obj failed", e);
            }

            File mtl = createFile(log, name, ".mtl");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mtl))) {
                List<Vector3f> materialList = new ArrayList<>(materials);
                for (int i = 0; i < materialList.size(); i++) {
                    writer.write("newmtl mat." + i + "\n");
                    Vector3f color = materialList.get(i);
                    writer.write("Kd " + values(color) + "\n");
                }
            } catch (IOException e) {
                Log.error(LogCategory.LOG, "Attempt to create file " + name + ".mtl failed", e);
            }
        }
    }

    private static String values(Vector3f vector) {
        return String.format(Locale.US, "%f %f %f", vector.x, vector.y, vector.z);
    }


    private static String[] readFile(File file) {
        List<String> lines = new ArrayList<>();

        // Read the lines using a BufferedReader
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines.toArray(new String[0]);
    }

    private static File createFile(File directory, String name, String extension) {
        File file = new File(directory, name + extension);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}