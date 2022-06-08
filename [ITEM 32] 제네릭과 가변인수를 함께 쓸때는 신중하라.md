# Item 32 - 제네릭과 가변인수를 함께 쓸때는 신중하라

# 가변인수란?

- 함수 호출 시 사용 인자 값 한정하는 것
- 함수 내부에서 배열처럼 사용 가능

```java
public void test(String ... str);
```

- 일반 변수 사용하는 것과 비슷함
- 자료형과 변수명 사이에 `...` 추가
- 가변인자 항상 마지막에 두기 
(어디까지가 가변인수로 선언되었는지 컴파일러 확인 x)

# 제네릭과 가변인수

## 가변인수 구현 방식 허점

- **가변인수** 특징
    - 메서드에 넘기는 **인수의 개수 클라이언트 조절**
    - 메서드 호출 시 **가변인수 담기 위한 배열 자동 생성**
    - 내부로 감춰야 했을 이 배**열을 클라이언트에 노출하는 문제 발생**!
- `varargs` 매개변수에 ***제네릭***이나 ***매개변수화 타입***이 포함되면 **알기 어려운 컴파일 경고 발생**

## `varargs` 매개변수 컴파일 경고

- `varargs` 매개변수에 ***제네릭***이나 ***매개변수화 타입***이 포함되면 **알기 어려운 컴파일 경고 발생**
    - 가변인수 메서드 선언 시
        - **실체화 불가 타입으로 `varargs` 매개변수 선언** → 컴파일러 경고
    - 가변인수 메서드 호출 시
        - `varargs` 매개변수가 **실체화 불가 타입으로 추론**되면서 호출 → 컴파일러 경고

## 제네릭과 `varargs` 혼용 → 타입 안전성 깨진다!

- 매개변수화 타입의 변수가 타입이 다른 객체 참조 → 힙 오염 발생
    - 다른 타입 객체를 참조하는 상황 → 컴파일러가 자동 생성한 형변환 실패 가능성
        - 제네릭 타입 시스템이 약속한 타입 안전성 위협

```java
static void dangerous(List<String> ... stringLists){
	List<Integer> intList = List.of(42);
	Object[] objects = stringLists;
	object[0] = intList;  //힙 오염 발생
	String s = stringLists[0].get(0); //ClassCastException
}
```

- 형변환 없는데고 인수를 건데 호출하면 `ClassCastException` 던짐
    - 마지막 줄에 컴파일러가 생성한 보이지 않는 형변환 숨어서 존재
- 제네릭 `varargs` 배열 매개변수에 값을 저장하는 것은 안전하지 않음

# 제네릭, 매개변수화 타입의 `varargs` 매개변수

- 이렇게 오류를 내면서 경고로 끝내는 이유
→ 제네릭이나 매개변수화 타입의 `varargs` 매개변수를 받는 메서드가 실무에서 매우 유용하기 사용!
    - ex) `Arrays.asList<T... a)` `Collections.addAll(Collections<? super T> c, T... elements)`
    - 이 메서드들은 타입 안전

# `@SafeVarargs` 에너테이션 : 자바 7 이후

- 자바 7 이전 → 제네릭 가변인수 메서드의 **작성자는 호출자 쪽에 발생 경고에 대한 대책 없었음**
    - 가독성 떨어뜨리고, 진짜 문제를 알려주는 경고마저 숨기는 안 좋은 결과 발생
- `@SafeVarargs` 에너테이션은 메서드 작성자가 그 메서드가 타입 안전함 보장 장치
    - 컴파일러는 이 약속 믿고 메서드가 안전하지 않을 수 있다는 경고 X
    - 메서드가 안전한게 확실치 않다면 `@SafeVarargs` 애너테이션 절때 X

## 메서드 안전 확인방법

*가변인수 메서드 호출 시 `varargs` 매개변수 담는 제네릭 배열 만들어 짐*

- **메서드가 배열에 아무것도 저장 X** (그 매개변수들을 덮어쓰지 않는다)
    - 배열에 아무것도 저장하지 않고도 타입 안전성 깰 수 있으니 주의
- **배열의 참조가 밖으로 노출 X** (신뢰할 수 없는 코드가 배열에 접근할 수 없다)

→ *`varargs` 매개변수 배열*이 호출자로부터 그 메서드로 **순수하게 인수들을 전달하는 일**(`varargs` **목적대로만 쓰인다면**) 안전함!

## 배열 변경 없이 발생하는 경고

```java
static <T> T[] toArray(T... args){
	return args;
}
```

- **반환하는 배열의 타입** → **메서드에 인수 넘기는 컴파일타임에 결정**
    - 컴파일러에게 충분한 정보 없어 **타입 잘못 판단 가능성**
    - `varargs` 매개변수 배열을 그대로 반환으로 **힙 오염을 호출한 쪽의 콜스택까지 전이 결과 도출 가능성**

```java
//T 타입 인수 3개를 받아 그중 2개를 무작위로 골라 담은 배열 반환 메서드
static <T> T[] pickTwo(T a, T b, T c){
	switch(ThreadLocalRandom.current().nextInt(3)) {
		case 0: return toArray(a, b); //경고 발생 - Object[] 타입 배열을 반환하기 때문
		case 1: return toArray(a, c);
		case 2: return toArray(b, c);
	}
	throw new AssertionError(); //도달 불가
}

//main 메서드
public static void main(String[] args){
	String[] attributes = pickTwo("좋은", "빠른", "저렴한");
	//컴파일 되지만 ClassCastException 발생
}
```

- 제네릭 가변인수를 는 `toArray` 메서드 경고 발생
    - 컴파일러 `ToArray`에 넘길 T 인스턴스 2개를 담을 `varargs` 매개변수 배열 만드는 코드 생성
    - 이 코드가 만드는 배열의 타입 `Object[]` 
    → pickTwo 메서드에 어떤 타입의 객체를 넘기더라도 담을 수 있는 가장 구체적인 타입이기 때문
    - `toArray` 메서드가 `return` 한 배열이 그대로 `pickTow` 호출 클라이언트까지 전달
        - `pickTwo` 반환값을 `attributes`에 저장위 해 `String[]`로 형변화 코드 컴파일러 자동생성
        →`**ClassCastException` 발생**
        - `Object[]`는 `String[]`의 하위 타입 X
    - `varargs` 매개변수 배열 실제 매개변수가 저장된 후 변경되적 없는데 경고 발생함

## List.of() 사용

```java
static <T> List<T> pickTwo(T a, T b, T c){
	switch(ThreadLocalRandom.current().nextInt(3)) {
		case 0: return List.of(a, b);
		case 1: return List.of(a, c);
		case 2: return List.of(b, c);
	}
	throw new AssertionError(); //도달 불가
}

//main 메서드
public static void main(String[] args){
	List<String> attributes = pickTwo("좋은", "빠른", "저렴한");
}
```

- 컴파일러가 이 메서드의 타입 안전성을 검증할 수 있음
- `@SafeVarargs` 필요 없음
- 클라이언트 코드 살짝 지저분 + 속도 느려짐

→ 코드는 배열 없이 제네릭만 사용하므로 타입 안전

# 정리

- 가변인수와 제네릭은 궁합이 좋지 않음
    - 가변인수 기능 : 배열을 노출해 추상화가 완벽 X
    - 배열과 제네릭의 타입 규칙이 서로 다르기 때문
- 제네릭 `varargs` 매개변수는 타입 안전하지는 않지만 허용!
- 메서드에 제네릭 `varargs` 매개변수 사용 시 먼저 그 메서드가 타입 안전한지 확인한 다음 `@SafeVarargs` 애너테이션을 달아 사용하는데 불편하이 없게끔 하자!