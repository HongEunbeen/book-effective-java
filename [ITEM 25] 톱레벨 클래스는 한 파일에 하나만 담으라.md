# Item 25 - 톱레벨 클래스는 한 파일에 하나만 담으라

### 소스 파일 하나에 톱레벨 클래스 여러개 선언 시 문제점

```java
//Main.java
public class Main {
    public static void main(String args[]){
        System.out.println(Burger.NAME + " burger and" + Drink.NAME + );
				//사용자는 original burger and water를 return 받길 원한다.
    }
}

//Burger.java
class Burger {
    static final String NAME = "original";
}

class Drink {
    static final String NAME = "water";
}

//Drink.java
class Burger {
    static final String NAME = "cheese";
}

class Drink {
    static final String NAME = "coke";
}
//이렇게 우연히 같은 클래스를 담은 파일 Burger.java, Drink.java를 생성했을 때
//컴파일러에 어느 소스 파일을 먼저 건네느냐에 따라 동작이 달라진다.
```

- 컴파일러는 문제 없지만 **한 클래스를 여러 가지로 정의**할 수 있고 그 중 **어느것을 사용할지는 소스 파일 컴파일 순서에 따라 달라**진다.
- 컴파일 예시
    1.  `javac Main.java Drink.java`
        - 컴파일 오류 발생, Burger와 Drink 중복 정의
    2.  `javac Main.java` ,  `javac Main.java Burger.java`
        - 동작, `original burger and water` 출력
    3. `javac Drink.java Main.java`
        - 동작, `cheese burger and coke` 출력

### 해결책

- 단순히 톱레벨 클래스들을 서로 다른 소스 파일로 분리

    ```java
    //Main.java
    public class Main {
        public static void main(String args[]){
            System.out.println(Burger.NAME + " burder and" + Drink.NAME + );
        }
    }

    //Burger.java
    class Burger {
        static final String NAME = "original";
    }

    //Drink.java
    class Drink {
        static final String NAME = "water";
    }
    ```

- **정적 멤버 클래스 사용**(굳이 여러 톱레벨 클래스를 한 파일에 담고 싶다면...)

    ```java
    //Main.java
    public class Main {
        public static void main(String args[]){
            System.out.println(Burger.NAME + " burder and " + Drink.NAME + );
        }

        private static class Burger {
            static final String NAME = "original";
        }
        
        private static class Drink {
            static final String NAME = "water";
        }
    		//정적 멤버 클래스
    }
    ```

    - **다른 클래스에 딸린 부차적인 클래스라면 정적 멤버 클래스**로 만드는 쪽이 일반적으로 더 나을 수 있다.
    - 읽기 좋고, **private으로 선언하면 접근 범위도 최소로 관리 가능**하기 때문이다.

### 정리

- 소스 파일 하나에는 반드시 **톱레벨 클래스(혹은 톱레벨 인터페이스)를 하나만 담자**
- 이 규칙만 따른다면 **컴파일러가 한 클래스에 대한 정의를 여러 개 만들어 내는 일 사라**진다.
    - 소스 파일을 **어떤 순서로 컴파일** 하든 바이너리 파일이나 **프로그램의 동작이 달라지는 일은 결코 일어나지 않을 것**이다.
