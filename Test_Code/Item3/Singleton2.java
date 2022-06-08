public class Singleton2 {
    private static final Singleton2 INSTANCE = new Singleton2();

    private Singleton2(){ ... }

    public static Singleton2 getInstance(){return INSTANCE};
    // 여기서 항상 같은 객체의 참조를 반환하므로 제2의 Singleton2 의 인스턴스란 결코 만들어지지 않는다.

    public void print() {
        System.out.println("");
    }

    // 싱글턴임을 보장해주는 readResolve 메서드
    private Object readResolve() {
        // 진짜 Singleton2를 반환하고, 가짜 Singleton2는 가비지 컬렉터에 맡긴다.
        return INSTANCE;
    }
}
