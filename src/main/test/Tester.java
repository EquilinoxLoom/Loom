import languages.GameText;

public class Tester {
    public static void main(String[] args) {
        GameText.init(0);
        System.out.println(new SheepEntity().build());
    }
}
