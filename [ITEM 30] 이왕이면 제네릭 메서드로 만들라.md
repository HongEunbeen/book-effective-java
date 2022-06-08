# Item 30 - 이왕이면 제네릭 메서드로 만들라

# 제네릭 메서드

- 매개변수화 타입을 받는 **정적 유틸리티 메서드 거의 보통 제네릭**
- `Collections`의 **알고리즘 메서드(binarySearch, sort)등 모두 제네릭**
- 타입 매개변수들을 선언하는 **타입 매개변수 목록**은 **메서드의 제한자**와 **반환 타입 사이**에 온다.

    ```java
    public <E> Set<E> union(Set<E> s1, Set<E> s2) {
    //메서드 제한자 public 
    //타입 매개변수 목록 <E>
    //메서드 반환 타입 Set<E>

    //제네릭 메서드를 정의할때는 리턴타입이 무엇인지와는 상관없이 내가 제네릭 메서드라는 것을 컴파일러에게 알려줘야한다.
    //그러기 위해서 리턴타입을 정의하기 전에 제네릭 타입에 대한 정의를 반드시 적어야 한다.
    ```

- 타입 매개변수의 명명 규칙은 제네릭 메서드나 제네릭 타입이나 같다

    ```java
    //제네릭 미사용
    public static Set union(Set s1, Set s2) {
        Set result = new HashSet(s1);//경고 발생
        result.addAll(s2);//경고 발생
        return result;
    }
    //타입 안전하게 만들어 경고 제거

    //제네릭 사용(제네릭 메서드)
    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }
    //경고 없이 컴파일 되고 타입 안전
    ```

# 한정적 와일드카드 타입

- 한정적 와일드카드 타입으로 더 **유연**하게 사용가능

# 제네릭 싱글턴 팩터리

- 불변 객체
    - 생성 후 그 상태를 바꿀 수 없는 객체
    - 재할당은 가능하지만, 한 번 할당하면 내부 데이터 변경 할 수 없는 객체
        - ex ) String, Integer ...
- *불변 객체* → 여러 타입으로 활용 가능케 만들기
    - 제네릭 **런타임에 타입 정보가 소거** 되므로 **하나의 객체를 어떤 타입으로든 매개변수화 가능**
    - **요청한 타입 매개 변수**에 맞게 매번 그 **객체의 타입을 바꿔**주는 **정적 팩터리 사용**
    - 이러한 패턴을 **제네릭 싱클터 팩터리**
- `Collections.reverseOrder` 같은 함수 객체 or `Collections.emptySet` 같은 컬렉션용으로 사용

# 항등함수(identity function)을 담은 클래스

- 항등함수
    - 입력 값을 수정 없이 그대로 반환하는 특별한 함수
- 자바 Lib `Function.identity`를 사용 가능
- 항등 함수 객체는 상태가 없어 요청할 때마다 새로 생성하는 것은 낭비
- 자바의 제네릭이 실체화
    - **항등 함수를 타입별로 하나씩 만들어야 하는 상황 
    → 소거 방식을 사용한 덕에 제네릭 싱글턴 하나면 충분**
- 제네릭 싱글터 팩터리 패턴

```java
private static UnaryOperator<Objct> IDENTITY_FN = (t) -> t;

@SuppressWarnings("unchecked")
 public static <T> UnaryOperator<T> identityFunction(){
     return (UnaryOperator<T>) IDENTITY_FN;//비검사 형변환 경고 발생
 }
//UnaryOperator를 사용한 항등함수이기 때문에 경고를 숨긴다.

//사용방법
public static void main(String[] args){
	String[] strings = {"삼배", "대마", "나일론"};
	UnaryOperator<String> sameString = identityFunction();
	for(String s : strings) {
		System.out.println(sameString.apply(s));
	}

	Number[] numbers = {1, 2.0, 3L};
	UnaryOperator<Number> sameNumber = identityFunction();
	for(Number s : numbers) {
		System.out.println(sameNumber.apply(s));
	}
}
```

# 재귀적 타입 한정(recursive type bound)

- 타입의 자연적 순서를 정하는 Comparable 인터페이스와 함께 사용

    ```java
    public interface Comparable<T>{
    	int comparaTo(T o);
    }
    ```

    - 타입 매개변수 `T`
        - `Comparable<T>`를 구현한 타입이 비교할 수 있는 원소의 타입 정의
        - 거의 모든 타입은 자신과 같은 타입의 원소와만 비교 가능
        - `String` 은 `Comparable<String>`을 구현하고 `Integer`는 `Comparable<Integer>` 구현
    - 재귀적 타입 한정을 이용해 상호 비교
        - `Comparable`을 구현한 원소의 컬렉션을 입력받는 메서드들
            - 그 원소들을 정력, 검색, 최솟값, 최댓값 등 식으로 사용
            - 기능 수행 위해 **컬렉션에 담긴 모든 원소가 상호 비교 되어야 됌**

        ```java
        public static <E extends Comparable<E>> E max(Collection<E> c);
        ```

        - 타입 한정  `<E extends Comparable<E>>`
            - 모든 타입 `E`는 자신과 비교할 수 있다 라고 해석
    - 재귀적 타입 한정 사용
        - 컬렉션에서 최댓값 반환 예제
        (컬렉션에 담긴 원소의 자연적 순서를 기준으로 최댓값 계산)

            ```java
            public static <E extends Comparable<E>> E max(Collections<E> c){
                if(c.isEmpty()){
                    throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
                }
                E result = null;
                for(E e : c) {
                    if(result == null || e.compareTo(result) > 0){
                        result = Objects.requireNonNull(e);
                    }
                }
                return result;
            }
            //컴파일 오류나 경고 발생 X
            ```