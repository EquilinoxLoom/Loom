package loom;

public record LoomInfo(String id, String name, String version, String author, String description) {
    public boolean isValid() {
        return check(id) && check(name) && check(version) && check(author) && check(description);
    }

    private static boolean check(String check) {
        return check != null && !check.isEmpty();
    }
}
