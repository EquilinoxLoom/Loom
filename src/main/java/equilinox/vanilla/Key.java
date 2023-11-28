package equilinox.vanilla;

public enum Key {
    NONE,
    ESCAPE,
    NUMBER_1, NUMBER_2, NUMBER_3, NUMBER_4, NUMBER_5, NUMBER_6, NUMBER_7, NUMBER_8, NUMBER_9, NUMBER_0,
    MINUS,
    EQUALS,
    BACK,
    TAB,
    Q, W, E, R, T, Y, U, I, O, P,
    LEFT_BRACKET, RIGHT_BRACKET,
    ENTER,
    LEFT_CONTROL,
    A, S, D, F, G, H, J, K, L,
    SEMICOLON,
    APOSTROPHE,
    GRAVE_ACCENT,
    LEFT_SHIFT,
    BACKSLASH,
    Z, X, C, V, B, N, M,
    COMMA, PERIOD, SLASH,
    RIGHT_SHIFT,
    MULTIPLY,
    LEFT_MENU,
    SPACE,
    CAPITALS_LOCK,
    F1, F2, F3, F4, F5, F6, F7, F8, F9, F10,
    NUMBERS_LOCK,
    SCROLL_LOCK,
    NUMBER_PAD_7, NUMBER_PAD_8, NUMBER_PAD_9, SUBTRACT,
    NUMBER_PAD_4, NUMBER_PAD_5, NUMBER_PAD_6, ADD,
    NUMBER_PAD_1, NUMBER_PAD_2, NUMBER_PAD_3,
    NUMBER_PAD_0, KEY_DECIMAL,
    F11, F12,

    KEY_UP(200), KEY_LEFT(203), KEY_RIGHT(205), KEY_DOWN(208);

    private final int ordinal;

    Key() {
        this.ordinal = ordinal();
    }

    Key(int id) {
        this.ordinal = id;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
