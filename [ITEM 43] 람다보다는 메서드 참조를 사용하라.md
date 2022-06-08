# Item 43 - 람다보다는 메서드 참조를 사용하라

# 메서드 참조(method reference)

- 함수 객체를 람다보다 더 간결하게 만드는 방법

```java
//람다식
map.merge(key, 1, (count, incr) -> count + incr);//Map에 merge 메서드 사용

//메서드 참조
map.merge(key, 1 ,Integer::sum);//박싱 타입의 정적 메서드 sum 사용
```

- 람다식의 매개변수인 `count`와 `incr`은 하는 일 없이 공간 차지
**→ 람다식은 두 인수의 합을 단순히 반환할 뿐**
- 매개변수 수가 늘어날수록 메서드 참조로 제거할 수 있는 코드양 늘어남
- **메서드 참조 → 기능을 잘 드러내는 이름을 지어줄 수 있고 친절한 설명을 문서로 남길 수 있음**

# 람다와 메서드 참조

- **람다로 할 수 없는 일 → 메서드 참조로도 할 수 없음**
- **람다로 작성할 코드를 새로운 메서드에 담은 다음, 람다 대신 그 메서드 참조 사용**
    - `(count, incr) -> count + incr` 이 부분을 `Integer`의 `sum` 메서드에 담음
    - 람다 대신 `sum` 메서드 참조 `Integer::sum` 사용

## 람다가 메서드 참조보다 간결할 때 존재

```java
//메서드 참조
service.execute(GoshThisClassNameIsHumongous::action);

//람다식
service.execute(() -> action());
```

# 메서드 참조의 유형

- 정적 메서드 참조
    - 가장 흔하게 쓰임
    - `클래스::메서드`

    ```java
    Integer::parseInt

    str -> Integer.parseInt(str)
    ```

- 한정적 인스턴스 참조
    - 인스턴스 메서드를 참조하는 유형
    - 수신객체(참조 대상 인스턴스)를 특정함
    - 함수 객체가 받는 인수와 참조되는 메서드가 받는 인수가 같다 → 정적 참조와 비슷
    - 수신 객체 전달용 매개변수가 매개변수 목록의 첫번째 → `now()`
    - 그 뒤로는 참조되는 메서드 선언에 정의된 매개변수 → `isAfter`

    ```java
    Instant.now()::isAfter

    Instant then = Instant.now()
    t -> then.isAfter(t)
    ```

- 비한정적 인스턴스 참조
    - 인스턴스 메서드를 참조하는 유형
    - 수신객체(참조 대상 인스턴스)를 특정하지 않음
    - 함수 객체를 적용하는 시점에 수신 객체 알려줌
    - 주로 스트림 파이프라인에서의 매핑과 필터 함수에서 쓰임

    ```java
    String::toLowerCase

    str -> str.toLowerCase()
    ```

- 클래스 생성자 참조
    - 팩터리 객체로 사용

    ```java
    TreeMap<K, V>::new

    () -> new TreeMap<K, Y>()
    ```

- 배열 생성자 참조

    ```java
    int[]::new

    len -> new int[len]
    ```

# 정리

- 메서드 참조 쪽이 짧고 명확하다면 메서드 참조를 쓰고, 그렇지 않을 때만 람다 사용!