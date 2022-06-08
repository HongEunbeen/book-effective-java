# Item 46 - 스트림에서는 부작용 없는 함수를 사용하라

# 스트림 패러다임

- 스트림은 그저 또 하나의 API가 아닌, 함수형 프로그래밍에 기초한 패러다임
- 스트림이 제공하는 표현력, 속도, 병렬성을 얻으려면 API는 말할 것도 없고 이 패러다임까지 함께 받아들여야 함

## 스트림 패러다임의 핵심

- 계산을 일련의 변환(transformation)으로 재구성 하는 부분
- 각 변환 단계(중간, 종단) → 가능한 한 이전 단뎨의 결과를 받아 처리하는 순수 함수!!
    - 순수함수
    → 오직 입력만이 결과에 영향을 주는 함수
    → 다른 가변 상태 참조 X, 함수 스스로도 다른 상태를 변경 X

→ 스트림 연산에 건네는 함수 객체는 모두 부작용(side effect)이 없어야 한다.

## 잘못된 스트림의 사용

```java
Map<String, Long> freq = new HashMap<>();
try(Stream<String> words = new Scanner(file).tokens()){
	words.forEach(word -> {
		freq.merge(word.toLowerCase(), 1L, Long::sum);
	});
}
```

- 스트림, 람다, 메서드 참조 사용 + 결과 올바르지만 스트림 코드 아님!!

    → 스트림 코드를 가장한 반복적 코드

- forEach() → 외부 상태를 수정하는 람다를 실행하면서 문제 발생
→ 그저 스트림이 수행한 연산 결과를 보여주는 일만 하는게 올바르다.

```java
Map<String, Long> freq = new HashMap<>();
try(Stream<String> words = new Scanner(file).tokens()){
	freq = words.collect(groupingBy(String::toLowerCase, counting()));
}
```

→ forEach() 연산은 스트림 계산 결과를 보고할 때만 사용하고 계산하는데는 쓰지 말자

# 수집기(Collector)

- `java.util.stream.Collectors` 클래스는 메서드 39개 가짐
    - 이 중 타입 매개변수가 5개나 되는 것도 존재,,!!
- 복잡한 세부 내용을 잘 몰라도 이 API의 장점 활용 가능

→ 축소(reducion)전략을 캡슐화한 블랙박스 객체라고 생각(스트림의 원소들을 객체 하나에 취합 개념)

## 수집기 종류

- 수집기 사용시 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있음

`toList()` : 리스트 반환

`toSet()` : 집합 반환

`toCollection(collectionFactory)` : 프로그래머가 지정한 컬렉션 타입 반환

```java
List<String> topTen = freq.keySet().stream()
														.sorted(comparing(freq::get(reversed())
														.limit(10)
														.collection(toList());
```

- `comparing` → 키 추출 함수를 받는 비교자 생성 메서드
- `freq::get(reversed())` → 한정적 메서드 참조, 키 추출 함수

## 수집기 toMap() 소개

스트림의 각 원소는 키 하나와 값 하나에 연관되어 있고 다수의 스트림 원소가 같은 기에 연관 가능성 존재

### toMap(KeyMapper, valueMapper)

```java
toMap(Function<? super T,? extends K> keyMapper,
		  Function<? super T,? extends U> valueMapper)
```

- 가장 간단한 맵 수집기
- 인수 : 스트림원소를 키에 매핑하는 함수, 값에 매핑하는 함수

```java
private static final Map<String, Operation> stringToEnum = 
	Stream.of(values()).collect(
		toMap(Object::toString, e -> e));
//가변 매개변수 values()
```

- 각 원소가 고유한 키에 매핑되어 있을 때 적합
- 스트림 원소 다수가 같은키를 사용한다면 파이프라인이 `IllegalStateException` 던지면 종료

→ 충돌을 다루는 전략

- `toMap` → 키 매퍼와 값 매퍼는 물론 병합 함수까지 제공가능(같은 키를 공유하는 값들은 병합함수 사용해 기존 값에 합쳐짐)

### toMap(keyMapper, valueMapper, mergeFunction)

```java
toMap(Function<? super T,? extends K> keyMapper,
		  Function<? super T,? extends U> valueMapper, 
			BinaryOperator<U> mergeFunction)
```

- 인수 3개 받는 toMap은 어떤 키와 그 키에 연관된 원소들 중 하나를 골라 연관 짓는 맵 만들때 적합
- 다양한 음악가의 앨범들을 담은 스트림 → 음악가와 그 음악가의 베스트 앨범 연관 시 사용!

    ```java
    Map<Artist, Album> topHits = albums.collect(
    	toMap(Album::artist, a->a, maxBy(comparing(Album::sales))));
    ```

    - 다양한 음악가의 앨범들을 담은 스트림 : `albums`
    - 음악가 : `Album::artist`
    - 음악가의 베스트 앨범 : `a→a, maxBy(comparing(Album::sales)));`
        - 비교자로 `BinaryOperator`에서 정적 임포트한 `maxBy` 정적 팩터리 메서드 사용
        - `maxBy` : `Comparator<T>` 입력 받아 `BinaryOperator<T>` 리턴
- 충돌 발생시 마지막 값을 취하는 수집기를 만들때도 유용
    - 많은 스트림의 결과 비결정적
    - 매핑 함수가 키 하나에 연결해준 값이 모두 같을 때, 값이 다르더라도 모두 허용되는 값일 때 동작하는 수집기 필요

    ```java
    //마지막에 쓴 값을 취하는 수집기
    toMap(keyMapper, valueMapper,(oldVal, newVal) -> newVal)
    ```

### toMap(keyMapper, valueMapper, mergeFunction, mapFactory)

```java
toMap(Function<? super T,? extends K> keyMapper, 
			Function<? super T,? extends U> valueMapper, 
			BinaryOperator<U> mergeFunction, 
			Supplier<M> mapFactory)
```

- 네 번째 인수로 맵 팩터리를 받음
- 네 번째 인수로 `EnumMap`이나 `TreeMap`처럼 원하는 구현체 직접 지정 가능

## 수집기 groupingBy 소개

- 입력 : 분류 함수(입력받은 원소가 속하는 카테고리(키) 반환)
- 출력: 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기 반환

### groupingBy(classifier)

```java
groupingBy(Function<? super T,? extends K> classifier)
```

- 인수 : 분류 함수 하나
- 반환 : 반환된 맵에 담긴 각각의 값은 해당 카테고리에 속하는 원소들을 모두 담은 리스트

    ```java
    words.collect(groupingBy(word -> alphabetize(word)))
    ```

### groupingBy(classifier, downstream)

```java
groupingBy(Function<? super T,? extends K> classifier, 
					 Collector<? super T,A,D> downstream)
```

- `groupingBy` 가 반환하는 수집기가 리스트 외의 값을 갖는 맵 생성 → 다운스트림(downstream) 수집기
- 다운스트림 수집기
    - 해당 카테고리의 모든 원소를 담은 스트림으로부터 값을 생성
    - 인수로 `toSet()` : `groupingBy`는 원소들의 리스트가 아닌 집합(`Set`)을 값으로 갖는 맵 생성
    - 인수로 `toCollection(collectionFactory)` : 리스트나 집합 대신 컬렉션을 값으로 갖는 맵 생성
    → 원하는 컬렉션 타입을 선택할 수 있는 유연성
    - 인수로 `counting()` : 각 카테고리(키)를 원소를 담은 컬렉션이 아닌 해당 카테고리에 속하는 원소의 개수(값)와 매핑

        ```java
        Map<String, Long> freq = words
        											.collect(groupingBy(String::toLowerCase, counting()));
        ```

### groupingBy(classifier, mapFactory, downstream)

```java
groupingBy(Function<? super T,? extends K> classifier, 
					 Supplier<M> mapFactory, 
					 Collector<? super T,A,D> downstream)
```

- 두번째 인수로 맵 팰터리 지정
- 점층적 인수 목록 패턴(telescoping argument list pattern)에 어긋남
    - `mapFactory`가 두번째 인수가 아닌 마지막 인수로 와야 함
- 맵과 그 안에 담긴 컬렉션의 타입을 모두 지정 가능 → 값이 `TreeSet`인 `TreeMap`을 반환 수집기 생성 가능

### groupingByConcurrent()

- `groupingBy` 각각에 대응하는 메서드
- 대응하는 메서드의 동시 수행버전으로 `ConcurrentHashMap` 생성

```java
groupingByConcurrent(Function<? super T,? extends K> classifier)
groupingByConcurrent(Function<? super T,? extends K> classifier, 
										 Collector<? super T,A,D> downstream)
groupingByConcurrent(Function<? super T,? extends K> classifier,
										 Supplier<M> mapFactory,
										 Collector<? super T,A,D> downstream)
```

### partitioningBy()

- 분류 함수 자리에 프레드키트(predicate) 받고 키가 `Boolean`인 맵 반환
- 다운스트림 수집기도 받는 버전도 다중정의

```java
partitioningBy(Predicate<? super T> predicate)
partitioningBy(Predicate<? super T> predicate, 
							 Collector<? super T,A,D> downstream)
```

## counting() 메서드 사용?

- `counting` 메서드가 반환하는 수집기 ⇒ 다운스트림 수집기 전용
- `collect(counting())` 형태로 사용할 일 전혀 없음
    - 이미 `Stream`의 `count` 메서드를 직접 사용하여 같은 기능 수행 가능하기 때문
- `Collections`에는 이런 속성의 매서드 16개 존재
    - `summing`, `averagin`, `summarizing` → 각각 `int`, `long`, `double` 스트림용으로 존재
    - `reducing`메서드들, `filtering`, `mapping`, `flatMapping`, `collectingAndThen` 존재

→ 이 수집기들은 스트림 기능의 일부를 복제하여 다운 스트림 수집기를 작은 스트림처럼 동작하게 한 것

## Collertors에 정의되었지만 수집과 관련 없는 메서드

- 특이하게도 Collectors에 정의되어있지만 수집과는 관련이 없다!

### minBy(comparator), maxBy(comparator)

- 인수로 받은 비교자를 이용해 스트림에서 값이 가장 작은 / 가장 큰 원소를 찾아 반환

```java
maxBy(Comparator<? super T> comparator)
minBy(Comparator<? super T> comparator)
```

- `Stream` 인터페이스의 `min` 과 `max` 메서드를 일반화 한 것
- `java.util.function.BinaryOperator`의 `minBy`와 `maxBy` 메서드가 반환하는 이진 연산자의 수집기 버전

### joining()

- 문자열 등의 CharSequence 인스턴스의 스트림에만 적용 가능

```java
joining()
joining(CharSequence delimiter)
joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix)
```

- `joining()`
    - 단순히 원소들을 연결하는 수집기 반환
- `joining(CharSequence delimiter)`
    - `CharSequence` 타입의 구분문자를 매개변수로 받음
    - 연결 부위에 이 구분문자 삽입 → 쉽표 입력 시 CSV 형태의 문자열로 생성
- `joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix)`
    - 구분문자에 더해 접두문자(prefix)와 접미 문자(suffix) 받음
    - 접두 (`[`) +  구분 (`,`) +  접미 (`]`) →`[came, saw, conquered]` 처럼 컬렉션 출력한 듯한 문자열 생성 가능

# 정리

- 스트림 파이프라인 프로그래밍의 핵심은 부작용 없는 함수 객체에 있음
- 스트림뿐 아니라  스트림 관련 객체에 건네지는 모든 함수 객체가 부작용이 없어야 함
- 종단 연산 중 `forEach`는 스트림이 수행한 계산 결과를 보고 할 때만 이용(계산 자체에 이용X)
- 스트림을 올바로 사용하려면 수집기 중요 
→ 가장 중요 수집기 팩터리 `toList` `toSet` `toMap` `groupingBy` `joining`