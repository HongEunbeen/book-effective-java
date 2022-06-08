# Item 22 - 인터페이스는 타입을 정의하는 용도로만 사용하라

### 인터페이스

```java
//Main.java
public class Main {
    public static void main(String args[]) {
        PhoneInterface phone = new Samsung();
        phone.sendCall();
        phone.reciveCall();
    }
}

//PhoneInterface.java
interface PhoneInterface {
    void sendCall();
    void reciveCall();
}

//Samsung.Java
class Samsung implements PhoneInterface {
//**서브클래스가 자신의 목적에 맞게 메소드 구현 목적**
    @Override
    public void sendCall() {
        System.out.println("삼성폰 전화 걸기");
				//자신의 인스턴스로 무엇을 하고 있는지 클라이언트에게 알려주는 것
    }
    @Override
    public void reciveCall() {
        System.out.println("삼성폰 전화 받기");
    }

}
```

- 자신을 구현한 클래스의 **인스턴스를 참조할 수 있는 타입 역할**
- 클래스가 어떤 인터페이스를 구현한다
→ **자신의 인스턴스로 무엇을 하고 있는지를 클라이언트에 알려주는 것**
    - 인터페이스는 오직 이 용도로만 사용해야 한다.

### 인터페이스 사용 지침에 맞지 않는 예

- **상수 인터페이스**

    ```java
    public interface PysicalConstants {
        static final double AVOGADROS_NUMGER = 6.022_140_857e23;
    		// 이 상수를 사용하려는 클래스에서는 정규화된 이름(qualified name)을 
    		// 쓰는 걸 피하고자 인터페이스를 구현
    }
    ```

    - 메서드 없이, **상수를 뜻하는 static final 필드로만 가득 찬 인터페이스**
    - **상수 인터페이스 안티패턴은 인터페이스를 잘못 사용한 예**
        - 클래스 내부에서 사용하는 상수는 **외부 인터페이스가 아니라 내부 구현에 해당**(이 내부 구현을 **클래스의 API로 노출하는 행위**)
        - 클래스가 **어떤 상수 인터페이스를 사용하는 사용자에게는 아무런 의미가 없다**.
        - java.io.ObjectStreamConstants 등, 자바 플랫폼 라이브러리에도 상수 인터페이스가 몇개 있으나, 인터페이스를 잘못 활용한 예
    - **상수를 공개할 목적의 해결책(3가지)**
        - 특정 클래스나 인터페이스가 강하게 연관된 상수라면 **그 클래스나 인터페이스 자체에 추가**
        - 열거 타입을 나타내기 적합한 상수라면 **열거 타입으로 만들어 공개**
        - 인스턴스화 할 수 없는 **유틸리티 클래스에 담아 공개**

            ```java
            //PhysicalConstants.java
            //인스턴스화 할 수 없는 유틸리티 클래스
            public class PhysicalConstants {
                private PhysicalConstatns() {}//인스턴스 화 방지

                static final double AVOGADROS_NUMGER = 6.022_140_857e23;

            }

            //Main.java
            //정적 임포트를 사용하여 클래스 이름 생략 가능
            //기존 사용 방법 : PhysicalContants.AVOGADROS_NUMGER
            import static PhysicalContants.*;

            public class Main{
            	public static void main(String args[]){
            		System.out.println(AVOGADROS_NUMGER);
            	}
            }
            ```

### 정리

- **인터페이스는 타입을 정의하는 용도로만 사용**해야 한다.
- 상수 공개용 수단으로 사용하지 말자