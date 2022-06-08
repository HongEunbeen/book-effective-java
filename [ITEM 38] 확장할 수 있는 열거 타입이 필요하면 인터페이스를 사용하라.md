# Item 38 - 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

# 타입 안전 열거 패턴(typesafe enum pattern) VS 열거 타입

- **열거 타입은 거의 모든 상황에서 타입 안전 열거 패턴보다 우수**
- 차이점
    - *타입 안전 열거 패턴* : **확장 가능** 
     → 열거한 값들을 그대로 가져온 다음 갓을 더 추가하여 다른 목적으로 사용 가능
    - *열거 패턴* : **확장 불가능**

# 열거 타입의 확장

- 대부분 상황에서 **열거 타입을 확장하는 건 좋지 않은 생각**
    - 확장한 타입의 원소는 기반 타입의 원소로 취급 → 반대는 성립 X 이상!
    - **기반 타입과 확장된 타입들의 원소 모두 순회 방법도 없음**
    - 확장성 높이려면 고려할 요소가 늘어나 **설계와 구현 복잡**

## 연산코드(operation code, opcode)

- 각 원소는 특정 기계가 수행하는 연산 뜻
- **확장할 수 있는 열거 타입이 어울리는 쓰임**
    - ex ) API가 제공하는 기본 연산 외에 **사용자 확장 연산을 추가할 수 있도록 할때**

## 연산코드 확장

- **열거 타입이 임의의 인터페이스를 구현할 수 있다는 사실 이용**

```java
//Operation타입을 확장할 수 있게 만든 코드

//interface
//연산코드용 인터페이스 정의
public interface Operation {
    double apply(double x, double y);
}

//Enum
//열거 타입이 이 인터페이스 구현
public enum BasicOperation implements Operation {
    PLUS("+") {
	    public double apply(double x, double y) {return x + y};
    },
    MINUS("-") {
	    public double apply(double x, double y) {return x - y};
    },
    TIMES("*") {
	    public double apply(double x, double y) {return x * y};
    },
    DIVIDE("/") {
	    public double apply(double x, double y) {return x / y};
    };

    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        reutnr symbol;
    }
}
```

- 열거타입 → 인터페이스의 표준 구현체 역할
- 열거타입 `BasicOperation`은 확장 X, 인터페이스 `Operation`은 확장 O
    - `Operation` 구현한 **또 다른 열거 타입**을 정의해 기본 타입인 `BasicOperation` 대체 가능

    ```java
    //확장 가능한 열거 타입 - 연산 타입을 확장해 지수연산과 나머지 연산 추가
    public enum ExtendedOperation implements Operation {
        EXP("^") {
            public double apply(double x, double y) {return x ^ y};
        },
        REMAINDER("%") {
            public double apply(double x, double y) {return x % y};
        };

        private final String symbol;

        BasicOperation(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            reutnr symbol;
        }
    }
    ```

    - 확장된 열거 타입 연산은 **기존 연산을 쓰는 곳이면 어디든 사용 가능**
        - `BasicOperation`이 아닌 `Operation` 인터페이스를 사용하도록 작성되어 있는 곳
    - `apply`가 인터페이스(`Operation`)에 선언되어 있기 때문에 **열거 타입에 따로 추상 메서드 선언 X**

        ```java
        //interface 사용 전 추상 메서드 사용 방법
        public enum Operation_new {
            PLUS {public double apply(double x, double y) {return x + y; }},
            MINUS { public double apply(double x, double y) {return x - y; }},
            TIMES { public double apply(double x, double y) {return x * y;}},
            DIVIDE { public double apply(double x, double y) {return x / y;}};

            private final String symbol;

            Operation_new(String symbol) {
                this.symbol = symbol;
            }

            @Override
            public String toString() {
                return symbol;
            }

            public abstract double apply(double x, double y);
        }
        ```

## 타입 수준에서의 확장

- 개별 인스턴스 수준에서뿐 아니라 **타입 수준에서도 확장 가능**
    - **기본 열거 타입** 대신 **확장된 열거 타입 넘겨 확장된 열거 타입의 원소 모두를 사용**

```java
//test 프로그램을 가져와 ExtendedOperation의 모든 원소 테스트
public class main {
    public static main(String args[]) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);

        test(ExtendedOperation.class, x, y);
    }

    private static <T extends Enum<T> & Operation> void test(Class<T> onEnumType, double x, double y) {
        for (Operation op : onEnumType.getEnumConstants()) {
            System.out.println("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }
}
```

- `test` 메서드에 `ExtendedOperation`의 ***class 리터럴* 넘겨 확장된 연산들이 무엇인지 알려줌**
    - *class 리터럴* = 한정적 타입 토큰 역할
- `opEnumType` 매개변수의 선언 → `<T extends Enum<T> & Operation> Class<T>`
    - `class` 객체 → **열거 타입**인 동시에 **`Operation`의 하위 타입**
    - 열거 타입이어야 **원소를 순회 가능** +  `Operation`이어야 원소가 뜻하는 **연산 수행 가능**

```java
//Class 객체 대신 한정적 와일드카드 타입 사용
import java.util.Arrays;

public class main {
    public static main(String args[]) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);

        test(Arrays.asList(ExtendedOperation.values()), x, y);
    }

    private static void test(Collection<? extends Operation> opSet, double x, double y) {
        for (Operation op : opSet) {
            System.out.println("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }
}
```

- 한정적 와일드카드 → `Collection<? extends Operation>`
- 덜 복잡하고 `test` 메서드 더 **유연함**
    - **여러 구현 타입의 연산을 조합해 호출 가능**

## 인터페이스 이용 확장 가능한 열거 타입 문제점

- 특정 연산에서 `EnumSet`과 `EnumMap` 사용 불가

    ```java
    //ExtendedOperation에 4와 2 넣어 사용

    4.000000 ^ 2.0000000 = 16.000000
    4.0000000 % 2.0000000 = 0.0000000
    ```

- **열거 타입끼리 구현을 상속할 수 없음**
- 해결 방법
    - 아무 상태에도 의존하지 않는 경우 ***디폴트 구현* 이용해 인터페이스 추가 방법** 사용
    - 확장된 `Enum`이 **공유하는 기능이 많다면 그 부분을 별도의 도우미 클래스나 정적 도우미 메서드로 분리**하는 방식으로 코드 중복 없애기
        - ex) `Operation`에 연산 기호를 저장하고 찾는 로직 `BasicOperation`, `ExtendedOpertaion` 둘다 들어가야 함

## java.nio.file.LinkOption 열거 타입

- **자바 라이브러리에서도 인터페이스를 이용해 확장 가능한 열거 타입 패턴 사용**
- 열거 타입은 `CopyOption`과 `OpenOption` 인터페이스를 구현

# 정리

- 열거 타입 자체는 **확장 불가**, 인터페이스와 그 **인터페이스를 구현하는 기본 열거 타입을 함께 사용**해 같은 효과 낼 수 있음
- 이 패턴 사용 시 **클라이언트는 이 인터페이스를 구현해 자신만의 열거 타입 생성 가능**
- ***API***가 기본 열거 타입을 직접 명시하지 않고 **인터페이스 기반으로 작성**
    - 기본 열거 타입의 인스턴스가 쓰이는 **모든 곳을 새로 확장한 열거타입의 인스턴스로 대체해 사용 가능**