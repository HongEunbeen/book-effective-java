public class Singleton {
    public static final Printer INSTANSE = new Singleton();

    // 생성자는 public static final 필드인 Elvis.INSTANSE를 초기화할 떄 딱 한 번만 호출
    // public 이나 protected 생성자가 없으므로 Elvis 클래스가 초기화될 때 만들어진 인스턴스가 전체 시스템에서 하나뿐임을 보장
    // 클라이언트는 손 쓸 방법이 없다.
    // 예외 > 권하닝 있는 클라이언트는 리플렉션 API인 AccessibleObject.setAccessible을 사용해 private 생성자를
    // 호출할 수 있다.
    // 이러한 공격 방어하려면 생성자를 수정해 두번 째 객체가 생성되려 할때 예외!!!!
    private Singleton() { ... }

    public void print() {
        System.out.println("Singleton Instance Created..");
    }
}