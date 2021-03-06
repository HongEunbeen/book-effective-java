# Item 34 - int 상수 대신 열거 타입을 사용하라

# 열거 타입(Enum type)

- 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입

## B) 정수 열거 패턴(int enum pattern)

```java
public static final int APPLE_A = 0;
public static final int APPLE_B = 1;
public static final int APPLE_C = 2;

public static final int ORANGE_A = 0;
public static final int ORANGE_B = 1;
public static final int ORANGE_C = 2;

int i = APPLE_A == ORANGE_A ? 0 : 1;
//오렌지와 사과를 혼돈해서 사용해도 컴파일러는 경고를 주지 않는다.
```

- Java → **정수 열거 패턴을 위한 별도 이름공간을 지원X 접두어로 이름 충돌 방지**
- 단점
    - 타입 안전을 보장할 방법 없음
    - 표현력이 좋지 않음
- 문제점
    - **컴파일하면 그 값이 클라이언트 파일에 그대로 새겨짐**(평범한 상수를 나열한 것)
        - 상수의 값이 변경되면 **클라이언트도 반드시 다시 컴파일**
        - *다시 컴파일하지 않은 클라이언트는 실행이 되더라도 엉뚱하게 동작*
    - 정수 상수 → **문자열로 출력 어렵**
    - 값을 출력하거나 디버거로 살펴보면 **의미가 아닌 단지 숫자(값**)으로만 보여서 도움X
    - **같은 정수 열거 패턴의 개수가 몇개인지 알 수 없고 순회하는 방법도 마땅치 않다.**

## B) 문자열 열거 패턴(string enum pattern)

- 문제점
    - 상수의 의미를 출력할 수 있다는 점은 좋지만, 경험이 부족한 프로그래머가 **문자열 상수의 이름 대신 문자열 값을 그대로 하드코딩**
    - 하드코딩한 문자열에 오타 있어도 **컴파일러 확인 길 없으니 자연스럽게 런타임 버그**
    - 문자열 비교에 따른 성능 저하 당연

## 열거 타입(enum type)

- 열거 패턴의 단점 보안
- 겉보기에는 *c, c++, c#* 같은 다른 언어의 열거 타입과 비슷
    - **자바의 열거 타입은 완전한 형태의 클래스**
    - 단순하게 값일 뿐인 다른 언어의 열거 타입보다 **훨씬 강력**

```java
public class School {
   public enum Elementary{FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH}
   public enum Middle {FIRST, SECOND, THIRD}
   public enum High {FIRST, SECOND, THIRD}
}
```

### 열거 타입 자체는 **클래스**

- 상수 하나당 **자신의 인스턴스**를 하나씩 만들어 `public static final` 필드로 공개
    - 열거 타입은 밖에서 접근할 수 있는 **생성자를 제공하지 않으므로 사실상 final**
    - 클라이언트가 인스턴스를 직접 **생성하거나 확장할 수 없으니 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나만 존재 보장**
    - 열거 타입은 **인스턴스 통제**

    → **싱글턴은 원소가 하나뿐인 열거 타입(열거타입은 싱글턴을 일반화한 형태)**

    ```java
    public class Singleton3 {
       INSTANCE;
     
       public void print(){
           System.out.println("enum SingleTon3");
       }
    }
    ```

### 열거 타입은 **컴파일 타임에서 타입 안전성을 제공**

- 열거 타입을 매개변수로 받는 메서드를 선언
→ 건네받은 참조는 `null` or  `Middle`의 세 가지 값 중 하나
- 다른 타입의 값을 넘기려 하면 컴파일 오류 발생
    - 타입이 다른 열거 타입 변수에 할당
    - 다른 열거 타입의 값끼리 == 연산자로 비교하려는ㄲ로

### 열거 타입에는 각자의 이름 공간이 있어서 이름이 같은 상수도 평화롭게 공존한다.

- 열거 타입에 새로운 상수 추가, 순서 변경 → 다시 컴파일 하지 않아도 된다.
- **공개되는 것이 오직 필드의 이름뿐!**
    - 정수 열거 패턴과 달리 상수 값이 클라이언트로 컴파일되어 각인 x
- 열거 타입의 `toString` 메서드는 출력하기에 적합한 문자열을 내어준다.

### 열거 타입 장점

- 열거타입에는 임의의 메서드나 필드 추가
- 임의의 인터페이스 구현
- Object 메서드들 높은 품질로 구현
- Comparable과 Serializalbe 구현
- 직렬화 형태도 웬만큼 변형 가해도 문제 없이 동작하겎므 구현
- 열거 타입에 메서드나 필드 추가
    - 상수마다 동작이 달라져야 하는 상황

# 상수별 메서드 구현(constant-specific method implementation)

- 열거 타입에 apply 추상 메서드 선언
- 각 상수별 클래스 몸체 즉 각 상수에서 자신에 맞게 재정의 하는 방법
- 이렇게 하면 새로운 사수 추가 시 오류 발생 확률 낮다
- 추상 메서드는 재정의 하지 않았다면 컴파일 오류 알려준다.
- 상수별 데이터와 결합 가능
    - 상수별 클래스 몸체와 데이터를 사용한 열거 타입
    - 상수 이름을 입력받아 그 이름에 해당하는 상수를 반환해주는 valueOf(String) 메서드 자동 생성
    - 열거타ㅇ입의 toString 재정의
        - toString이 반환하는 문자열 > 해당 열거 타입 상수
        - 변환 fromString 메서드 사용

# 열거 타입 언제 쓰란 말인라?

- 대부분의 경우 열거 타입의 성능은 정수 상수와 별반 다르지 않다.
- 열거 타입을 메모리에 올리는 공간과 초기화하는 시간이 들긴 하지만 체감 x
- 필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입 사용
    - 태양계 행성, 한 주의 요일, 체스 말
    - 본질적으로 열거 타입인 타입은 당연히 포함
    - 메뉴 아이템, 연산 코드, 명령줄 플래그 등 허용하는 값
- 열거 타입에 정의 된 상수 개수가 영원히 고정 불변인 필요 없다
    - 나중에 상수가 추가 돼도 바이너리 수준에서 호환 가능 설계

정리

- 열거 타입은 확실히 정수 상수보다 뛰어나고 더 읽기 쉽고 안전하고 강력
- **대다수 열거 타입이 명시적 생성자나 메서드 없이 사용**
- 각 상수를 특정 데이터와 연결짓거나 상수마다 다르게 동작할게 할 때는 필요
- 드물게는 하나의 메서드가 사수별로 다르게 동작해야 할 때도 있다
- 이런 열거 타입에서는 switch 문 대신 상수별 메서드 구현을 사용하자
- 열**거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자**