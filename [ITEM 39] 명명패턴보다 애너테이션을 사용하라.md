# Item 39 - 명명패턴보다 애너테이션을 사용하라

# 명명패턴

- 전통적으로 도구나 프레임워크가 특별히 다뤄야 할 프로그램 요소 → 딱 구분되는 명명패턴 적용
    - ex) JUnit 버전 3까지 테스트 메서드 이름 test 시작

## 명명패턴 단점

- 오타가 나면 안됌
    - 실수로 메서드 이름 `tsetSafetyOverride` 지정 시JUnit은 메서드 무시 
    → 개발자 테스트 통과 오해 가능
- 올바른 프로그램 요소에서만 사용 보증할 방법 없음
    - 클래스 이름을 TestSafetyMechanisms 지정 시 JUnit은 클래스 이름 중요 X 로 무시
    → 개발자 의도 테스트 수행 X
- 프로그램 요소를 매개변수로 전달할 마당한 방법 없음
    - 컴파일러는 메서드 이름에 덧붙인 문자열이 예외를 가리키는지 알 도리 X

# 애너테이션(Annotaion)

- 자바소스코드에 추가해 사용할 수 있는 메타데이터의 일종
    - 본질적 목적 → 소스코드에 메타데이터 표현
- JDK 1.5 버전 이상 사용, 인터페이스
- 활용
    - 클래스에 특수 의미 부여
    - 기능 주입
    - 컴파일타임, 런타임에 해석

# 메타 애너테이션(meta-annotaion)

- 애너테이션 선언에 사용되는 애너테이션
- 해당 에너테이션의 동작 대상 결정

## `@Test` 애너테이션

```java
import java.lang.annotation.*;

/**
 * 테스트 메서드임을 선언하는 애너테이션
 *  매개변수 없는 정적 메서드 전용
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

}
```

- `@Test` 애너테이션 타입 선언 자체에 두가지의 다른 애너테이션 사용
- `@Retention(RetentionPolicy.RUNTIME)`
    - `@Test`가 런타임에도 유지되어야 한다는 표시
    - 이 메타 애너테이션 생략 시 테스트 도구 → `@Test` 인식 불가
- `@Target(ElementType.METHOD)`
    - `@Test` 가 반드시 메서드 선언에서만 사용돼야 한다는 표시
    - 클래스 선언, 필드 선언 등 다른 프로그램 요소에 달 수 없다.
- 주석 `매개변수 없는 정적 메서드 전용`
    - 이 제약 컴파일러가 강제할 수 있게 적절한 애너테이션 처리기 직접 구현 필요
    → javax.annotation.processing API 문서 참고

# 마커(marker) 애너테이션

- 아무 매개변수 없이 단순히 대상에 마킹하는 애너테이션

```java
import java.lang.annotation.*;

public class Sample {
    @Test
    public static void m1() { } //성공해야 한다.

    public static void m2() { } 

    @Test
    public static void m3() { //실패해야 한다.
        throw new RuntimeException("실패");
    }

    public static void m4() { }

    @Test
    public void m5() { } //잘못 사용(정적 메서드가 아니다.)

    public static void m6() { }

    @Test
    public static void m7() { //실패해야 한다.
        throw new RuntimeException("실패");
    }

    public static void m8() { }

}
```

- 정적 메서드 7개 중 4개 `@Test` 애너테이션 선언
    - 1개 성공 - `m1()`
    - 2개 실패 - `m3()` `m5()`
    - 1개 잘못 사용 - `m5()`
- @Test 애너테이션 선언 안된 나머지 4개의 메서드는 테스트 도구 무시
- @Test 애너테이션이 클래스의 의미에 직접적인 영향 X
    - 이 애너테이션에 관심있는 프로그램에게 추가 정보 제공
    - 대상 코드의 의미는 그대로 둔 채 그 애너테이션에 관심이쓴 도구에서 특별한 처리 할 기회 줌

    ```java
    //마커 애너테이션을 처리하는 프로그램
    import java.lang.reflect.InvocationTargetException;
    import java.lang.reflect.Method;

    public class RunTests {
        public static void main(String[] args) throws Exception {
            int tests = 0;
            int passed = 0;
            Class<?> testClass = Class.forName(args[0]);
            for (Method m : testClass.getDeclareMethods()) {
                if (m.isAnnotationPresent(Test.class)) {
                    tests++;
                    try {
                        m.invoke(null);
                        passed++;
                    } catch (InvocationTargetException wrappedExc) {
                        Throwable exc = wrappedExc.getCause();
                        System.out.println(m + " 실패 : " + exc);
                    } catch (Exception e) {
                        System.out.println("잘못 사용한 @Test: " + m);
                    }
                }
            }
            System.out.println("성공 : %d, 실패 : %d%n", passed, tests - passed);
        }
    }
    ```

    - args 명령줄 : 완전 정규화된 클래스 이름 받음
    - 그 클래스에서 @Test 애너테이션이 달린 메서드를 차례로 호출
    - isAnnotationPresent가 실행할 메서드를 찾아주는 메서드
    - 테스트 메서드가 예외를 던지면 리플렉션 메커니즘이 InvocationTrgetException으로 감싸서 다시 던진다.
    - 이 프로그램은 InvocationTargetException을 잡아 원래 예외에 담긴 실패 정보를 추출해(getCause) 출력한다.
    - InvocationTargetException 외의 예외 발생 → @Test 애너테이션 잘 못 사용
        - 인스턴스 메서드, 매개변수가 있는 메서드, 호출할 수 없는 메서드 등 달았을 것
- 특정 예외를 던져야만 성공하는 테스트

```java
import java.lang.annotation.*;

/**
 * 명시한 예외를 던져야만 성공하는 테스트 메서드용 애너테이션
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
	Class<? extends Throwable> value();
}
```

- 이 애너테이션의 매개변수 타입 → Class<? extends Throwable>
    - Throwable을 확장한 클래스의 Class 객체

    → 모든 예외와 오류 타입을 다 수용한다.

- 애너테이션 매개변수의 값을 추출해 데스트 메서드가 올바른 예외를 던지는지 확인하는 데 사용

## 배열 매개변수를 받는 애너테이션

```java
//매개 변수 타입을 Class 객체의 배열로 설정
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
	Class<? extends Throwable>[] value();
}
```

배열 매개변수를 받는 애너테이션 문법

- 원소가 여럿인 배열을 지정할 때는 다음과 같이 원소들을 중괄호로 감싸고 쉼표로 구분

```java
@ExceptionTest({indexOutOfBoundsException.class,
									NullPointerException.class})
public static void doublyBad() {
	List<String> list = new ArrayList<>();
	list.add(5, null);
}
```

### 자바 8 배열 매개변수 대신 @Repeatable 메타 에너테이션

- @Repeatable 메타애너테이션은 하나의 프로그램 요소에 여러번 사용 가능
- 주의점
    - @Repeatable을 단 애너테이션을 반환하는 컨테이너 애너테이션을 하나 더 정의 후 @Repeatable에 이 컨테이너 애너테이션의 class 객체를 매개변수로 전달
    - 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 함
    - 적절한 보존 정책(@Rentention) 과 적용 대상(@Target) 명시(컴파일 위해)

# 정리

- 애너테이션으로 할 수 있는 일을 명명 패턴으로 처리할 이유는 없다.
- 자바 프로그래머라면 예외 없이 자바가 제공하는 애너테이션 타입들은 사용해야 한다.