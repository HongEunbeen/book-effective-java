# Item 31 - 한정적 와일드카드를 사용해 API 유연성을 높이라

# 한정적 와일드 카드

## 1. Stack의 pushAll 메서드

```java
public class Stack<E> {
  public Stack();

  public void push(E e);

  public E pop();

  public boolean isEmpty();

	public void pushAll(Iterable<E> src) {
    for (E e : src) {
        push(e);
    }
	}
}

//예시
Stack<Number> numberStack = new Stack<>();
Iterable<Integer> integers = ...;
numberStack.pushAll(integers);//오류 발생
```

- `E`에 스택의 원소 타입과 일치하면 잘 작동
- `Stack<Number>` 로 선언 후 `pushAll(intVal)` 호출
    - **매개변수화 타입이 불공변으로 오류 발생**
- 이런 불공변 오류 해결 위해 **한정적 와일드 카드라는 타입으로 특별한 매개변수화 지원**
    - `Interable<? extends E>`
        - `E`의 `Interable` → `E`의 하위 타입인 `Interable`

    ```java
    //매개변수에 와일드카드 타입 적용
    public void pushAll(Iterable<? extends E> src) {
      for (E e : src) {
          push(e);
      }
    }
    ```

    - 와일드카드 타입 수정으로 **클라이언트 코드도 말끔히 컴파일**
        - **모든것이 타입 안전하다는 뜻**

## 2. Stack의 popAll 메서드

```java
public void popAll(Collection<E> dst) {
    while (!isEmpty())
        dst.add(pop());
}

//예시
Stack<Number> numberStack = new Stack<>();
Collection<Object> objects = ...;
numberStack.popAll(objects);//오류 발생
```

- `E`에 스택의 원소 타입과 일치하면 잘 작동
- `Stack<Number>` 의 원소를 `Object` 용 컬렉션으로 옮길때
    - **매개변수화 타입이 불공변으로 오류 발생**
- 불공변 오류 해결 위해 와일드카드 타입으로 해결
    - `Collection<? super E>`
        - `E`의 `Collection` → `E`의 상위타입의 `Collection` (모든 타입은 자시 자신의 상위 타입이다.)

    ```java
    public void popAll(Collection<? super E> dst) {
        while (!isEmpty())
            dst.add(pop());
    }
    ```

> 유연성을 극대화하려면 원소의 생상자나 소비자용입력 매개변수에 와일드 카드 타입을 사용하라

## 한정적 와일드 카드 사용시 주의

- 입력 매개변수가 생상자와 소비자 역할을 동시에 한다면 사용 추천
- 타입을 정확히 지정해야 하는 상황에서는 쓰지 않아야 한다.

# 펙스(PECS) 공식

- 팩스(PECS) : producer-extends, consumer-super
- 와일드카드 타입을 사용하는 기본 원칙
- 나프탈린(Naftalin), 와들러(Wadler)는 이를 겟풋원칙(Get and Put Principle)으로 부름
- 매개변수화 타입 `T`가 생산자 → `<? extends T>`
    - `Stack`의 `pushAll`의 `src` 매개변수는 `Stack`이 사용할 `E` 인스턴스 생산
- 매개변수화 타입 T가 소비자 → `<? super T>`
    - `Stack`의 `popAll`의 `dst` 매개변수는 `Stack`으로부터 `E` 인스턴스를 소비

## 1. Chooser 생성자 예시

```java
//수정 전
public Chooser(Collection<T> choices)

//수정 후
public Chooser(Collection<? extends T> choices)
```

- `choices` 컬렉션은 `T` 타입의 값을 생산하기만 한다.
- `T`를 **확장하는 와일드 카드 타입**을 사용
    - `Chooser<Number>`의 생성자에 `List<Integer>` 넘기기 가능

## 2. Union 메서드 예시

```java
//수정 전
public static <E> Set<E> union(Set<E> s1, Set<E> s2)

//수정 후
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2)

//수정 후 사용 예시
Set<Integer> integers = Set.of(1, 3, 5);
Set<Double> doubles = Set.of(2.0, 4.0, 6.0);
Set<Number> numbers = union(integers, doubles);//오류 안남!!
```

- `s1`과 `s2` 모두 `E`의 생산자 이니 PECS 공식에 따라 선언
- **반환 타입에는 한정적 와일드카드 타입을 사용하면 안됌**
    - 클라이언트 코드에서도 와일드 카드 타입을 써야 하기 때문!

## 특징

- 제대로 사용 시 클래스 사용자는 와일드카드 타입 사용 사실 의식 못함
- 받아들여야 할 매개변수를 받고 거절해야 할 매개변수는 거절 작업 자동으로 일어남
- 클래스 사용자가 와일드카드 타입을 신경 써야 한다면 그 API는 문제 가능성 존재

# 펙스(PECS) 공식 두 번 사용

```java
//수정 전
public static <E extends Comparable<E>> E max(List<E> list)

//수정 후
public static <E extends Comparable<? super E>> 
	E max(List<? extends E> list)
```

- `List<E>` → `List<? extends E>`
    - 입력 매개변수 에서는 `E`인스턴스를 **생산**
- `Comparable<E>` → `Comparable<? super E>`
    - 타입 매개변수 `E`로 **타입 매개변수에 와일드 카드 적용**
    - 원래 선언 : `E`가 `Comparable<E>` 확장을 정의 → 이때 `Comparable<E>`가 `E`인스턴스를 **소비**
    - 매개변수화 타입 `Comparable<E>`를 한정적 와일드카드 타입 `Comparable<? super E>` 대체

## MAX 메서드 선언 방법 옳은 건가?

```java
//이 리스트는 오직 수정 후 max 함수로만 처리 가능
List<ScheduledFuture<?>> scheduledFuture = ...;
```

- 수정 전 max 메서드 처리 불가
    - `java.util.concurrent` 패키지의 `ScheduledFuture`
    → `Comparable<ScheduledFuture>`를 구현하지 않았기 때문에 처리 불가
        - `ScheduledFuture` 는 `Delayed`의 하위 인터페이스, `Delayed`는 `Comparable<Delayed>`를 확장

            ```java
            public interface Comparable<E>
            public interface Delayed extends Comparable<Delayed>
            public interface ScheduledFuture<V> extends Delayed, Future<V>
            ```

            - `ScheduledFuture` 의 인스턴스는 다른 `ScheduledFuture` 인스턴스뿐 아니라 `Delayed` 인스턴스와도 비교할 수 있어서 수정 전 max 메서드 이 리스트 거부
        - `Comparable`을 직접 구현하지 않고, **직접 구현한 다른 타입을 확장한 타입을 지원하기 위해 와일드 카드 필요**

# Comparable<? super E>, Comparator<? super E>

- `Comparable`은 언제나 소비자
    - 일반적으로 `Comparable<E>`보다는 `Comparable<? super E>`사용하는 편이 좋다.
- `Comparator`은 언제나 소비자
    - 일반적으로 `Comparator<E>`보다는 `Comparator<? super E>`사용하는 편이 좋다.

# 명시적 타입 인수(explicit type argument) : 자바 7

- 자바 7까지는 명시적 타입 인수를 사용해야 한다.

```java
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2)

Set<Integer> integers = Set.of(1, 3, 5);
Set<Double> doubles = Set.of(2.0, 4.0, 6.0);
Set<Number> numbers = union(integers, doubles);//오류 안남!!
```

- 위 코드는 자바 8에서 제대로 컴파일 되고 자바 7에서는 타입 추론 능력이 강력하지 못해 문맥에 맞는 반환 타입(목표 타입)을 명시해야 한다.
    - 위 코드의 반환 타입(목표 타입)은 `Set<Number>` 이다.
- 컴파일러가 올바른 타입을 추론하지 못할 때 → 명시적 타입 인수를 사용해 타입을 알려주기

    ```java
    Set<Number> numbers = Union<Number>union(integers, doubles);
    ```

    - 자바 7까지는 명시적 타입 인수를 사용해야 경고없이 컴파일 된다.

# 매개변수(parameter) VS 인수(argument)

- 매개변수 → 메서드 선언에 정의한 변수
- 인수 → 메서드 호출 시 넘기는 '실제값'

## 1. 기본형 예시

```java
void add(int value){...}
add(10);
```

- value : 매개변수
- 10 : 인수

## 2. 제네릭 예시

```java
class Set<T> {...}
Set<Integer> = ...;
```

- T : 타입 매개변수
- Integer : 타입 인수

# 타입 매개변수 VS  와일드 카드

- 타입 매개변수와 와일드카드 → 공통되는 부분 존재
    - 메서드를 정의할 때 둘 중 어느 것을 사용해도 괜찮을 떄 많다!!

```java
//주어진 리스트에서 명시한 두 인덱스 아이템들을 교환(swap)하는 정적 메서드

//1. 비한정적 타입 매개변수
public static <E> void swap(List<E> list, int i, int j);

//2. 비한정적 와일드카드
public static void swap(List<?> list, int i, int j);
```

- *public API*라면 간단한 **비한정적 와일드카드 방식** 추천
    - 어떤 리스트든 이 메서드에 넘기면 명시한 인덱스의 원소들을 교환 해줌
    - 신경 써야 할 매개변수 없음

## 기본 규칙

- **메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체**
    - 비한정적 타입 매개변수 → 비한정적 와일드카드
    - 한정적 타입 매개변수 → 한정적 와일드카드

## private 도우미 메서드

```java
public static void swap(List<?> list, int i, int j){
	list.set(i, list.set(j, list.get(i));
}//컴파일 오류
```

- 리스트의 타입이 `List<?>` → `List<?>`에는 `null` 외에는 어떤 값도 넣을 수 없다.
    - `List<?>` 안에 어떤 형이 들어갈 지 모르기 때문에 `null` 만 넣을 수 있음
    - ***런타임 오류***를 낼 가능성이 있는 **형변환이나 리스트의 로 타입을 사용하지 않고도 해결 가능**
- **와일드카드 타입의 실제 타입**을 알려주는 메서드를 **private 도우미 메서드**로 따로 작성하여 활용 방법
    - 실제 타입을 알아내기 위해 **도우미 메서드 → 제네릭 메서드**

    ```java
    public static void swap(List<?> list, int i, int j){
    	swapHelper(list, i, j);
    }//깔끔하게 컴파일

    //와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
    private static <E> void swapHelper(List<E> list, int i, int j){
    	list.set(i, list.set(j, list.get(i));
    }
    ```

    - `swapHelper` 메서드는 리스트가 `List<E>`임을 인지함
        - `List<E>` 에서 꺼낸 값의 타입 → 항상 `E`
        - E 타입의 값이라면 `List<E>`에 넣어도 안전함 인지함
- `private 도우미 메서드` 이용해 `swap` 메서드 **내부 → 더 복잡한 제네릭 메서드 이용** 
BUT **외부 → 와일드카드 기반의 멋진 선언 유지 가능**
    - `swap` 메서드 호출 클라이언트 → **복잡한 `swapHelper`의 존재 모른 채 쉽게 호출 가능**
    - 도우미 메서드의 시그니처는 앞에서 "public API로 쓰기에는 너무 복잡하다"는 이유로 버렸던 첫 번재 swap 메서드의 시그니처와 완전히 같다.

# 정리

- 조금 복잡하더라도 **와일드카드 타입 적용 → API 유연**
    - 널리 쓰일 **라이브러리 작성 시 반드시 와일드카드 타입을 적절히 사용**
- **PECS 공식** 반드시 기억
    - *생산자(producer)* → `extends`
    - *소비자(consumer)* → `super`
        - `Comparable`과 `Comparator`은 모두 *소비자*!!