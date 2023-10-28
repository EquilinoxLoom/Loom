package loom.entity.weaver;

import com.sun.istack.internal.NotNull;
import loom.entity.Entity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EntityLoader {
    public static void createBlueprint(@NotNull Entity ie) {
        if (ie.getModelPaths() == null || ie.getModelPaths().length == 0) {
            throw new RuntimeException("Model from entity_" + ie.getId() + " is null or empty.");
        }

        if (ie.getId() <= 183 || (ie.getId() >= 1000 && ie.getId() <= 1003)) {
            throw new RuntimeException("Id:" + ie.getId() + " is not available.");
        }

        final List<List<List<String>>> model = new ArrayList<>();

        for (String stage : ie.getModelPaths()) {
            List<List<String>> lists = new ArrayList<>();
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                lists.add(Files.readAllLines(Paths.get(Objects.requireNonNull(classloader.getResource(stage + ".obj")).toURI())));
                lists.add(Files.readAllLines(Paths.get(Objects.requireNonNull(classloader.getResource(stage + ".mtl")).toURI())));
            } catch (NullPointerException e) {
                System.err.println("Couldn't find " + stage + ".obj and " + stage + ".mtl at resources folder.");
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
            model.add(lists);
        }

        List<List<Integer>> pointer = new ArrayList<>();
        List<List<String>> faces = new ArrayList<>();
        List<List<Float>> vertex = new ArrayList<>();
        List<List<Float>> normal = new ArrayList<>();
        List<List<Float>> colour = new ArrayList<>();
        int counter = 0;

        for (List<List<String>> stage : model) {
            List<String> obj = stage.get(0);
            List<String> mtl = stage.get(1);

            List<Integer> _pointer = new ArrayList<>();
            List<String> _faces = new ArrayList<>();
            List<Float> _vertex = new ArrayList<>();
            List<Float> _normal = new ArrayList<>();
            List<Float> _colour = new ArrayList<>();

            for (String o : obj) {
                String[] objSplit = o.split(" ");
                switch (objSplit[0]) {
                    case "v":
                        _vertex.add(Float.parseFloat(objSplit[1]));
                        _vertex.add(Float.parseFloat(objSplit[2]));
                        _vertex.add(Float.parseFloat(objSplit[3]));
                        break;
                    case "vn":
                        _normal.add(Float.parseFloat(objSplit[1]));
                        _normal.add(Float.parseFloat(objSplit[2]));
                        _normal.add(Float.parseFloat(objSplit[3]));
                        break;
                    case "usemtl":
                        _faces.add("POINTER");
                        _pointer.add(counter);

                        for (String m : mtl) {
                            String[] mtlSplit = m.split(" ");
                            if (mtlSplit[0].equals("Kd")) {
                                _colour.add(Float.parseFloat(mtlSplit[1]));
                                _colour.add(Float.parseFloat(mtlSplit[2]));
                                _colour.add(Float.parseFloat(mtlSplit[3]));
                            }
                        }
                        break;
                    case "f":
                        _faces.add(objSplit[1]);
                        _faces.add(objSplit[2]);
                        _faces.add(objSplit[3]);
                        counter += 3;
                        break;
                }
            }

            _pointer.add(counter);

            if (!ie.getMaterials().isEmpty() && !(ie.hasEggStage() && stage.equals(model.get(0)))) {
                for (int i = 0; i < _colour.size(); i += 6) {
                    _colour.set(i, -1F);
                    _colour.set(i + 1, 0F);
                    _colour.set(i + 2, 0F);
                }
            }

            pointer.add(_pointer);
            faces.add(_faces);
            vertex.add(_vertex);
            normal.add(_normal);
            colour.add(_colour);
        }

        String blueprint = loadSubBlueprints(pointer, faces, vertex, normal, colour);

        //return ie.processor.buildHeader() + "\n" + pointer.size() + "\n" + blueprint + "\n" + ie.processor.buildFooter();
    }

    private static String loadSubBlueprints(List<List<Integer>> pointer, List<List<String>> faces, List<List<Float>> vertex, List<List<Float>> normal, List<List<Float>> colour) {
        StringBuilder blueprint = new StringBuilder();
        for (int i = 0; i < pointer.size(); i++) {
            blueprint.append(String.format(Locale.US, "%.4f", getMin(vertex.get(i), 0))).append(";");
            blueprint.append(String.format(Locale.US, "%.4f", getMin(vertex.get(i), 1))).append(";");
            blueprint.append(String.format(Locale.US, "%.4f", getMin(vertex.get(i), 2))).append(";");

            blueprint.append(String.format(Locale.US, "%.4f", getMax(vertex.get(i), 0))).append(";");
            blueprint.append(String.format(Locale.US, "%.4f", getMax(vertex.get(i), 1))).append(";");
            blueprint.append(String.format(Locale.US, "%.4f", getMax(vertex.get(i), 2))).append(";1\n");

            int pointerSize = pointer.get(i).size() - 1;

            blueprint.append(faces.get(i).size() - pointerSize).append(";").append(pointerSize);

            int point = colour.get(i).size() / 3 - 1;
            for (int j = 0, k = 1; j < faces.get(i).size(); j++) {
                if (faces.get(i).get(j).contains("/")) {
                    String[] convertedFaces = faces.get(i).get(j).split("/");
                    int faces0 = Integer.parseInt(convertedFaces[0]) - 1;
                    int faces1 = Integer.parseInt(convertedFaces[2]) - 1;
                    blueprint.append(String.format(Locale.US, "%.4f", vertex.get(i).get(faces0 * 3))).append(";");
                    blueprint.append(String.format(Locale.US, "%.4f", vertex.get(i).get(faces0 * 3 + 1))).append(";");
                    blueprint.append(String.format(Locale.US, "%.4f", vertex.get(i).get(faces0 * 3 + 2))).append(";");
                    blueprint.append(String.format(Locale.US, "%.4f", normal.get(i).get(faces1 * 3))).append(";");
                    blueprint.append(String.format(Locale.US, "%.4f", normal.get(i).get(faces1 * 3 + 1))).append(";");
                    blueprint.append(String.format(Locale.US, "%.4f", normal.get(i).get(faces1 * 3 + 2)));
                    if (j + 1 < faces.get(i).size() && !faces.get(i).get(j + 1).contains("POINTER")) blueprint.append(";");
                }

                if (faces.get(i).get(j).contains("POINTER")) {
                    blueprint.append("\n").append(pointer.get(i).get(k) - pointer.get(i).get(k - 1)).append(";");
                    blueprint.append(colour.get(i).get(point * 3).toString()).append(";");
                    blueprint.append(colour.get(i).get(point * 3 + 1).toString()).append(";");
                    blueprint.append(colour.get(i).get(point * 3 + 2).toString()).append("\n");
                    point--; k++;
                }
            }
            blueprint.append("\n");
        }
        return blueprint.toString();
    }

    static float getMin(List<Float> floatList, int axis) {
        float min = floatList.get(axis);
        for (int i = axis; i < floatList.size(); i += 3)
            if (floatList.get(i) < min) min = floatList.get(i);
        return min;
    }

    static float getMax(List<Float> floatList, int axis) {
        float max = floatList.get(axis);
        for (int i = axis; i < floatList.size(); i += 3)
            if (floatList.get(i) > max) max = floatList.get(i);
        return max;
    }
}
