interface MyInterface {
    default void printHello() {
        System.out.println("Hello World");
    }
}

class MyClass implements MyInterface {
}

public class Main {
    public static void main(String[] args) {
        MyClass myClass = new MyClass();
        myClass.printHello(); // 실행결과 Hello World 출력
    }
}