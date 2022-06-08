public class Text {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

    // 매개변수 syltes는 0개 이상으 ㅣSTYLE_ 상수를 비트별 OR 한 값이다.
    public void applyStyles(int styles){...}

    // text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
}
