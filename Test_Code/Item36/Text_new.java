public class Text_new {
    public enum Style {
        BOLD, ITALIC, UNDERLINE, STRIKETHROUGH
    };

    // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다. > 이왕이면 인터페이스로 받는게 일반적으로 좋은 습관
    public void applyStyles(Set<Style> styles){...}

    //applyStyles 메서드에 EnumSet 인스턴스를 건네는 클라이언트 ㅗ드
    //EnumSet은 집합 생성 등 다양한 기능의 정적 팩터리를 제공하는데, 다음 코드에서는 그중 Of 사용ㅎㅆ다.
    // text.applyStyles(EnumSet.of(Style.BOLD, Sytle.ITALIC));
}
