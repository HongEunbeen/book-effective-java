# Item 54 - null이 아닌, 빈 컬렉션이나 배열을 반환하라

# `null`을 반환한다면?

```java
private final List<Cheese> cheeseInStock = ...;

public List<Cheese> getCheeses() {
	return cheesesInStock.isEmpty() ? null
		: new ArrayList<>(cheesesInStock);
}

//client
is( cheesse != null && ...){...}
```

→ 클라이언트는 이 `null` 상황을 처리하는 코드를 추가로 작성해야 한다!

컨테이너가 비었을 때 `null` 반환 메서드 → 방어 코드를 넣어워야 하는 불상사 발생(반환하는 쪽에서 특별 취급)

## `null`을 반환하는게 이득이라고?

→ 두가지 면에서 틀린주장!!!

- 성능 분석 결과 이 할당이 성능 저하의 주범이라고 확인되지 않는 한 성능 차이 신경X
- 빈 컬렉션과 배열을 굳이 새로 할당하지 않고도 반환 가능!

# 빈 컬렉션 반환하기

```java
public List<Cheese> getCheeses(){
	return new ArrayList<>(cheesesInStock);
}
```

→ 하지만 사용 패턴에 따라 빈 컬렉션 할당으로 성능 떨어질 수 있다. 다행히 해법 존재!

## 최적화 - 불변 컬렉션 반환

→ 불변 객체는 자유롭게 공유해도 안전하기에 매번 똑같은 빈 불변 컬렉션 반환하면 성능 면에서 좋다!

- 최적화에 해당하기 때문에 꼭 필요할 때만 사용 추천!

```java
public List<Cheese> getCheeses(){
	return cheesesInStock.isEmpty() ? Collections.emptyList()
		: new ArrayList<>(cheesesInStock);
}
```

# 길이 0인 배열 반환하기

```java
public Cheese[] getCheeses() {
	return cheesesInStock.toArray(new Cheese[0]);
}
```

→ 절때 null 반환하지 말고 길이가 0인 배열을 반환하자!

- 단순히 정확한 길이의 배열도 가능
- 길이가 0 인 배열은 반환 타입을 알려주는 역할 도 한다.

## 최적화 - 길이가 0인 배열 미리 선언 후 반환

→ 길이가 0인 배열은 모두 불변이기에 미리 선언 후 반환하면 성능 면에서 좋다!

- 하지만 단순히 성능을 개선할 목적이라면 이 방법 추천 X → 오히려 성능 떨어진다는 분석...
- `getCheeses`는 항상 `EMPTY_CHEESE_ARRAY`를 인수로 넘겨 `toArray` 호출

    → `cheesesInStock` 이 비었을 때면 언제나 `EMPTY_CHEESE_ARRAY` 반환

```java
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
	return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```

# 결론

`null`이 아닌, 빈 배열이나 컬렌션을 반환하자!

→ `null`을 반환하는 API는 사용하기 어렵고 오류 처리 코드도 늘어나지만 성능은 똥이다!