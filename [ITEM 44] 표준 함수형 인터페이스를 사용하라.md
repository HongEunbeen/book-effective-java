# Item 44 - 표준 함수형 인터페이스를 사용하라

# 람다로 인한 API 작성

- 이전 : 상위 클래스의 기본 메서드를 재정의 해 원하는 동작 구현 **템플릿 메서드 패턴**
- 현재 : 같은 효과의 **함수 객체를 받는 정적 팩터리나 생성자 제공**

→ 함수 객체를 매개변수로 받는 생성자와 메서드를 더 많이 만들어야 됌

- 함수형 매개변수 타입을 올바르게 선택 중요

# 람다 활용 방법

- LinkedHashMap → removeEldestEntry 재정의시 캐시로 사용 가능
    - 맵에 새로운 키 추가하는 put 메서드가 removeEldestEntry 호출
    → true 반환시 맵에서 가장 오래된 요소 제거

```java
protected boolean removeEldestEntry(Map.Entry<K,V> eldest){
	return size() > 100;
}
//맵에 원소 100개가 될 떄까지 커지다가, 그 이상이 되면 새로운 키 더해질때마다 가장 오래된 요소 제거
//->가장 최근의 원로 100개 유지
```

- 함수형 인터페이스

```java
@FunctionalInterface interface EldestEntryRemovalFunction<K, V>{
	boolean remove(Map<K, V> map, Map.Entry<K,v> eldest);
}
```

- 자바 표준 라이브러리에 같은 인터페이스 존재!!

# 표준 함수형 인터페이스

- `java.util.function` 패키지를 보면 다양한 용도의 표준 함수형 인터페이스 존재
- 필요한 용도에 맞는게 이싿면 직접 구현하지 말고 표준 함수형 인터페이스 사용

    → API를 다루는 개념의 수가 줄어들어 익히기 더 쉬워진다

    → 유용한 디폴트 메서드를 만힝 제공하므로 다른 코드와의 상호운용성도 크게 좋아진다.

## java.util.function 패키지

- 총 43개의 인터페이스 존재
- 기본 인터페이스 6개 → 모두 참조 타입

## 기본 인터페이스

- `Operator` 인터페이스
    - `UnaryOperator` → 인수가 1개
    - `BinaryOperator` →인수가 2개

    → 반환값과 인수의 타입이 같은 함수

    |인터페이스|함수 시그니처|예|
    |------|---|---|
    |`UnaryOperator<T>`|`T apply(T t)`|`String::toLowerCase`|
    |`BinaryOperator<T>`|`T apply(T t1, T t2)`|`BigInteage::add`|

    ```java
    public BigInteger add(BigInteger val)
    //returns a BigInteger object whose value is (this + val).
    ```

- `Predicate` 인터페이스

    →하나의 인수를 받아 boolean을 반환하는 함수

    |인터페이스|함수 시그니처|예|
    |------|---|---|
    |`Predicate<T>`|`boolean test(T t)`|`Collection::isEmpty`|

    ```java
    public static boolean isEmpty(Collection<?> coll)
    //Returns true if this collection contains no elements.
    ```

- `Function` 인터페이스

    →반환값과 인수의 타입이 다른 함수

    |인터페이스|함수 시그니처|예|
    |------|---|---|
    |`Function<T, R>`|`R apply(T t)`|`Arrays::asList`|

    ```java
    @SafeVarargs
    public static <T> List<T> asList(T... a)
    //Returns a fixed-size list backed by the specified array.
    ```

- `Supplier` 인터페이스

    → 인수를 받지 않고 값을 반환하는 함수|인터페이스|함수 시그니처|예|
    |------|---|---|
    |`Supplier<T>`|`T get()`|`Instant::now`|

    ```java
    public static Instant now()
    //Obtains the current instant from the system clock.
    //Returns the current instant using the system clock, not null
    ```

- `Consumer` 인터페이스

    → 인수를 하나 받고 반환값은 없는 함수(인수를 소비)

    |인터페이스|함수 시그니처|예|
    |------|---|---|
    |`Consumer<T>`|`void accept(T t)`|`System.out::println`|

    ```java
    public static final PrintStream out
    public void println(boolean x)
    //Prints a boolean and then terminate the line. This method behaves as though it invokes print(boolean) and then println().
    //Parameters : x - The boolean to be printed

    ```

## 기본 인터페이스의 변형

- 기본 인터페이스는 기본 타입인 `int` `long` `double` 용으로 각 3개씩 변형 존재
- 이름 → 기본 인터페이스의 이름 앞에 해당 기본 타입 이름 붙여 사용
    - `predicate` → `IntPredicate`
    - `BinaryOperator` → `LongBinaryOperator`
- 유일하게 `Function`의 변형만 매개변수화(반환 타입만 매개변수화)
    - `LongFunction<int[]>` → `long`인수를 받아 `int[]` 반환

    ## Fucntion 인터페이스 변형

    - 기본 타입을 반환하는 변형이 총 9개 존재
    - 인수와 같은 타입 반환 함수 이미 존재(`UnaryOperator`) 
    → `Function` 인터페이스의 변형은 입력과 결과의 타입 항상 다름

    ### 접두어

    - *SrcToResult* : 입력과 결과의 타입이 모두 기본 타입 (6개)
        - `LongToIntFunction` → `long`을 받아 `int` 반환
    - *toResult* : 입력이 객체 참조이고 결과가 `int` `long` `double` 인 변형 (3개)
        - `ToLongFunction<int[]>` → `int[]`인수를 받아 `long` 반환

    ## 인수 2개 받는 기본 함수형 인터페이스 변형

    - 기본 함수형 인터페이스 중 3개에는 인수를 2개씩 받는 변형 존재 (총 9개)
    - `BiPredicate<T, U>`
    - `BiFunction<T, U , R>`
        - 기본 타입을 반환하는 세 변형 존재
        - `ToIntBiFunction<T, U>`
        - `ToLongBiFucntion<T, U>`
        - `ToDoubleBiFuction<T, U>`
    - `BiConsumer<T, U>`
        - 객체 참조와 기본 타입 하나(인수를 2개 받는 변형)
        - `ObjDoubleConsumer<T>`
        - `ObjIntConsumer<T>`
        - `ObjLongConsumer<T>`

    ## Supplier 인터페이스 변형

    - `BooleanSupplier` → `boolean`을 반환하도록 한 변형
    - 표준 함수형 인터페이스중 `boolean`을 이름에 명시한 유일한 인터페이스

        →BUT `Predicate`와 그 변형 4개도 `boolean` 값 반환 가능

→ 표준 함수형 인터페이스는 대부분 기본 타입만 지원

→기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하지 말자(성능 위해)

# 전용 함수형 인터페이스

- 대부분 상황에서 직접 작성하는 것보다 표준 항ㅁ수형 인터페이스 사용 추천
- 표준 인터페이스 중 필요한 용도에 맞는게 없다면 직접 작성
    - 매개변수 3개를 받는 `Predicate`
    - 검사 예외를 던지는 경우

### 표준 함수형 인터페이스 존재 → 직접 작성??

- 구조적으로 똑같은 표준 함수형 인터페이스 존재라도 직접 작성해야 하는 경우 발생
    - `Comparator<T>` 인터페이스 = `ToIntBiFunction<T, U>` 인터페이스
    → 두 인터페이스는 구조적으로 같다
- `Comparator` 인터페이스가 독자적인 인터페이스로 살아남아야 하는 이유
    - API에서 굉장히 자주 사용(지금의 이름이 그 용도를 아주 휼룡히 설명)
    - 구현하는 쪽에서 반드시 지켜야 할 규약 담고 있음
    - 비교자들을 변환하고 조합해주는 유용한 디폴트 메서드 많이 있음

### 전용 함수형 인터페이스를 구현 시 고려사항

- 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다.
- 반드시 따라야 하는 규약이 있다.
- 유용한 디폴트 메서드를 제공할 수 있다.

→ 이 중 하나 이상 만족시 전용 함수형 인터페이스 구현 고민!

→ 구현 시 인터페이스 작성임을 명심

# `@FunctionalInterface` 애너테이션

- 직접 만든 함수형 인터페이스에는 항상 `@FunctionalInterface` 애너테이션 사용
- 프로그래머의 의도 명시 위해
    - 해당 클래스의 코드나 설명 문서를 읽을 이에게 그 인터페이스가 람다용을 설계된 것임 명시
    - 해당 인터페이스 추상 메서드 오직 하나만 가지고 있어야 컴파일 가능(두개 이상 시 컴파일 경고)
    - 유지보수 과정에서 누군가 실수로 메서드를 추가하지 못하게 막아줌

# 함수형 인터페이스 API에서 사용 시 주의점

- 서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드 다중정의 X
    - 클라이언트에게 불필요한 모호함만 존재
    - `ExecutorService`의 `submit` 메서드 → `Callable<T>` `Runnable` 다중정의
    → 올바른 메서드 알려주기 위해 형변환 필요

→ 서로 다른 함수형 인터페이스를 같은 위치의 인수로 사용하는 다중정의 피하자!!

# 정리

- API 설계시 람다 염두에 두기
- 입력값과 반환값에 함수형 인터페이스 타입 활용
- 흔치는 않지만 직접 새로운 함수형 인터페이스를 만들어 사용하는 편이 나을 수 있음!!