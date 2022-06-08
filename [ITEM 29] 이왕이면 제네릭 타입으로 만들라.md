# Item 29 - 이왕이면 제네릭 타입으로 만들라

# 제네릭 타입

- JDK 제공 제네릭 타입과 메서드 사용 → 일반적으로 쉬운 편
- 제네릭 타입을 새로 만들어 사용 → 어려움

# 제네릭 타입으로 생성

- 클래스를 제네릭으로 변경
    - 현재 버전을 사용하는 클라이언트 피해 없음
- Stack(Object) 기반 클래스 → 제네릭 타입 변경
    - 현재
    → 클라이언트는 스택에서 꺼낸 객체를 형변환(런타임 오류 가능성)

### 1. 클래스 선언에 타입 매개 변수 추가

- 타입 이름으로는 보통 `E`를 사용

```java
import java.util.Arrays;

public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 1)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

### 2. Object 적절한 타입 매개변수로 변경

```java
import java.util.Arrays;

public class Stack {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 1)
            throw new EmptyStackException();
        E result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

### 3. 컴파일

- `E`와 같은 실체화 불가 타입으로는 배열 생성X → 컴파일 오류

# 배열을 사용하는 코드 제네릭 변환 시 문제 해결방법

### 1. 제네릭 배열 생성 금지하는 제약 대놓고 우회

```java
//배열 elements는 push(E)로 넘어온 E 인스턴스만 담는다.
//따라서 타입 안전성을 보장하지만,
//이 배열의 런타임 타입은 E[]가 아닌 Object[]다!
@SuppressWarning("unchecked")
public Stack() {
    elements = new Object[DEFAULT_INITIAL_CAPACITY];
}
```

- `Object` 배열 생성 후 **제네릭 배열로 형변환**
    - 컴파일 오류 대신 경고 → 타입 안전하지 않다
- **비검사 형변환이 프로그램의 타입 안전성을 해치지 않음을 개발자 스스로 확인**
    - 문제의 배열 elements는 private 필드에 저장
    - 클라이언트로 반환 or 다른 메서드 전달 X
    - push 메서드 통해 배열에 저장되는 원소의 타입 항상 E
    - 비검사 형변환은 안전 개발자 스스로 확인
    - 범위를 최소로 좁혀 @SuppressWarnings 애너테이션으로 해당 경고 숨김

### 2. elements 필드의 타입을 `E[]`에서 `Object[]`로 변경

```java
public E pop() {
    if (size == 1)
        throw new EmptyStackException();

		//push에서 E 타입만 허용하므르 이 형변환은 안전하다.
		@SuppressWarning("unchecked")
		E result = elements[--size];
    elements[size] = null; // 다 쓴 참조 해제
    return result;
}
```

- 배열이 반환한 원소를 E로 형변환하면 오류 대신 경고 발생
- E는 실체화 불가 타입
    - 컴파일러는 런타임에 이뤄지는 형변화 안전한지 증명 방법 없다
    - 직접 증명하고 경고를 숨길 수 있다.
    - 비검사 형변환을 수행하는 할당문에서 숨김

### 1번 VS 2번

- **첫번째 방법(클래스 선언에 타입 매개 변수 추가)**
    - 가독성 더 좋다
        - 배열의 타입을 E[]로 선언하여 오직 E 타입 인스턴스만 받음
    - 토드 더 짧다
    - 형변환을 배열 생성 시 단 한 번만 해주면 되지만 두 번재 방식에서는 배열에서 원소를 읽을 때마다 해줘야 하낟.
    - 현업에서 더 선호
- **두번째 방법 (elements 필드의 타입을 `E[]`에서 `Object[]`로 변경)**
    - 배열의 런타임 타입이 컴파일타임 타입과 달라 힙오염을 일으킨다

# 제네릭 타입 안에서 리스트를 사용하는게 항상 가능하지도,꼭 더 좋은 것도 아니다

- 자바가 리스트를 기본 타입으로 제공하지 않으므고 `arrayList` 같은 제네릭 타입
→ 기본 타입인 배열을 사용해 구현해야 한다.
- `HashMap` 같은 제네릭 타입은 성능을 높일 목적으로 배열을 사용하기도 한다.

# 제네릭 타입은 기본 타입을 사용 할 수 없다.

- 자바 제네릭 타입 시스템의 근본적인 문제
- 박싱된 기본 타입을 이용해 우회

# 한정적 타입 매개변수

- `class DelayQueue<E extends Delayed> impolements BlockingQueue<E>`
    - `java.util.consurrent.DelayQueue`
    - 타입 매개변수 목록인 `<E extends Delayed>` 는 `java.util.consurrent.Delayed` 의 하위 타입만 받는다.
    - `DelayQueue` 를 사용하는 클라이언트와 자기 자신은 원소에서 형변환 없이 곧바로 `Delayed` 클래스의 메서드를 호출할 수 있다.
    - `ClassCastException` 걱정 X
- 모든 타입은 자기자신의 하위 타입이므로 자기자신도 사용 가능

# 정리

- 클라이언트에서 직접 형변환해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다.
- 새로운 타입을 설계 시 형변환 없이도 사용할 수 있도록
    - 제네릭 타입으로 만들어야 할 경우가 많다.
- 기존 타입 중 제니릭이었어야 하는게 있다면 제네릭 타입으로 변경
- 기존 클라이언트는 아무 영향을 주지 않으면서, 새로운 사용자를 훨씬 편하게 해주는 길이