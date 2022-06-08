# Item 28 - 배열보다는 리스트를 사용하라

# 배열과 제네릭 타입의 차이점

## 1. 배열 공변 VS 제네릭 불공변

- 배열은 **공변(covariant)**
    - `Sub`가 `Super`의 하위 타입이라면 배열 `Sub[]`는 배열 `Super[]`의 하위 타입이 된다
    - 공변, 즉 함께 변한다는 뜻이다.
- 제네릭은 **불공변(invariant)**
    - 서로 다른 타입 `Type1` 과 `Type2`가 있을 때 `List<Type1>`은 `List<Type2>`의 하위 타입도 아니고 항위 타입도 아니다.

    ```java
    //문법상 허용하지만 런타임에 실패
    Object[] objectArray = new Long[1];
    objectArray[0] = "타입이 달라 넣을 수 없다."; 
    //ArrayStoreException을 던진다.
    ```

    ```java
    //문법상 비 허용(컴파일에서 발견)
    List<Object> ol = new ArrayList<Long>();//호환되지 않는 타입이다.
    ol.add("타입이 달라 넣을 수 없다.");
    ```

    - 둘의 차이는 **배열은 런타임에 오류 발견** 하지만 **제네릭은 컴파일 타임에서 오류 발견**할 수 있다.
        - `Object`는 모든 자바 클래스의 부모타입이지만 `Object`를 가지는 컬렉션이 다른 컬렉션의 부모 타입은 아니다.

## 2. 배열 실체화 VS 제네릭 타입 이레이저

- 배열은 **실체화(reify)**
    - 배열은 **런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인**
    - `Long 배열`에 `String`을 넣으려 하면 `ArrayStoreException` 발생
- 제네릭은 **타입 이레이저(Type erasure)**
    - **원소 타입을 컴파일타임에만 검사 → 런타임에는 알수 x**
    (컴파일러가 컴파일 시점에 제네릭에 대해 type erasure 프로세스 적용)
        - 컴파일러에 의해 **자동으로 검사되어 타입 변환**
    - 소거는 제네릭이 지원되기 전의 레거시 코드와 제네릭 타입을 함께 사용할 수 있게 해주는 메커니즘으로 자바 5가 제네릭으로 순조롭게 전환될 수 있도록 해줌
        - **컴파일 된 .class 파일에는 제네릭 타입 포함 x**

# 제네릭 배열은 사용할 수 없음

- 배열은 **제네릭 타입, 매개변수화 타입, 타입 매개변수**로 사용할 수 없다.
    - `new List<E>[]` `new List<String>[]` `new E[]` 식으로 작성 시 컴파일
     → 제네릭 배열 생성 오류
    - **타입이 안전하지 않기 때문**
        - 배열은 `new` 연산자로 *heap 영역*에 충분한 공간이 존재하는지 확인 후 메모리를 확보하는데 확보 위해서는 **type 정보가 필요**
    - 허용 시 **컴파일러가 자동 생성한 형변환 코드**에서 런타임에 `ClassCastException` 발생 가능성 존재

```java
List<String>[] stringLists = new List<string>[1];
//1. 제네릭 배열 생성 허용 가정

List<Integer> intList = List.of(42);
//2. 원소가 하나인 List<Integer>를 생성한다.

Object[] objects = stringLists;
//3. List<String>의 배열을 Object 배열에 할당한다.(배열은 공변이니 아무 문제없다.)

objects[0] = intList;
//4. List<Integer>의 인스턴스를 objects 배열의 첫 원소로 저장(Integer에 String 넣음)

String s = stringLists[0].get(0);
//5. 배열의 리스트에서 원소 컴파일러 자동으로 String 형변환(원소는 Integer 타입으로 런타임에 오류)

```

- `objects[0] = intList;`
    - 제네릭은 컴파일 시 타입 이레이저 프로세스 발생하여 `ArrayStoreException`가 발생하지 않는다.
- `String s = stringLists[0].get(0);`
    - 컴파일러에서는 문제 발생하지 않지만 **런타임에서 `ClassCastException` 발생**
    - `List<String>` 인스턴스만 담겠다고 선언한 `stringLists` 배열에서 `List<Integer>` 인스턴스가 저장 됌

## 제네릭 배열 생성되지 않도록 컴파일 오류 발생

- **실체화 불가 타입(non-reifiable type)**
    - 실체화되지 않아서 **런타임에는 컴파일 타임보다 타입 정보를 적게 가지는 타입**
    - `E`  `List<E>` `List<String>` 등
- **실체화 가능 타입(reifiable type)**
    - 비한정적 와일드 카드 타입
        - `List<?>` `Map<?,?>`
        - 매개변수 타입 가운데 실체화 될 수 있는 유일한 타입
    - 배열을 비한정적 와일드 카드 타입 가능 → 유용 X
        - 배열은 `new` 연산자로 *heap 영역*에 충분한 공간이 존재하는지 확인 후 메모리를 확보하는데 확보 위해서는 **type 정보가 필요**

### @SafeVararge 애너테이션

- 배열을 제네릭에서 사용X 단점 극복 가능
    - 제네릭 컬렉션에서 자신의 원소 타입을 담은 배열 반환 불가능
    - 제네릭 타입과 가변인수 메서드 함께 쓰면 해석하지 어려운 경고 메시지
    - 가변 인수 메서드 호출 시 가변인수 메개변수 담을 배열 생성 -> 배열의 운소가 실체화 불가 타입이면 경고

# 제네릭 클래스에서 배열로 형 변환시 오류, 경고

- 배열로 형변환 시 **제네릭 배열 생성 오류**, **비검사 형변환 경고**
    - 배열 `E[]`  →  컬렉션 `List<E>` 사용
    - 코드 복잡 + 성능 안 좋아질 수 있지만 **타입 안전성과 상호운용성** 좋아짐

→ **제네릭을 쓰지 않고 구현(Objcet[]) - 런타임에서 형변환 오류**

```java
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chooser {
    private final Object[] choiceArray;

	  //생성자에 어떤 컬렉션을 넘기느냐에 따라 이 클래스를 다양한 용도로 사용 가능
    public Chooser(Collection choices) {
        choiceArray = choices.toArray();
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
} 
```

- `Chooseer` 클래스 사용시 `choose` 메서드 호출 마다 반환된 Object를 원하는 타입으로 형 변환
    - 타입이 다른 원소가 들어 있다면 **런타임에 형변화 오류(ClassCastException)**

**→ 제네릭으로 구현(T[]) - 컴파일 타임에서 비거사 형변환 경고**

```java
//제네릭으로 구현
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chooser_new<T> {
    private final T[] choiceArray;

    public Chooser_new(Collection<T> choices) {
        choiceArray = (T[]) choices.toArray();//비검사 형변환 경고
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
```

- `T`가 무슨 타입인지 알 수 없어 **컴파일러는 경고 발생(비검사 형변화 경고)**
    - 제네릭에서는 원소의 타입 정보가 소거되어 런타임에는 무슨 타입인지 알 수 없음
    - **프로그램 동작 → 컴파일러 안전 보장X**
    (안전 확신 시 **애너테이션**으로 경고 숨김 BUT **경고의 원인 제거 가능 시 제거**!)

**→ 제네릭으로 구현(List<T>)**

```java
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chooser_last<T> {
    private final List<T> choiceList;

    public Chooser_last(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
```

- 비검사 형변환 경고 제거
    - **배열 → 리스트**

# 정리

- 배열과 제너릭에는 **매우 다른 타입 규칙 적용**
- *배열*
    - **공변, 실체화**
- *제네릭*
    - **불공변, 타입 정보가 소거**
- 둘을 함께 사용 어려움
    - 둘을 섞어 쓰다가 컴파일 오류나 경고 발생 시 **가장 먼저 배열을 리스트로 대체하는 방법 적용**