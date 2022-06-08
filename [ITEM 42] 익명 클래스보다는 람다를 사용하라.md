# Item 42 - 익명 클래스보다는 람다를 사용하라

# 함수형 인터페이스(functional interface)

- 추상 클래스와 달리 **단 하나의 추상 메서드만 가짐**
- 람다식 저장 위한 **참조 변수의 타입 결정(참조 변수의 타입 = 함수형 인터페이스)**
- `@FunctionalInterface` 어노테이션으로 함수형 인터페이스 명시 가능

```java
@FunctionalInterface
interface Calc {
	public int min(x ,y); //추상 메서드
}

//main
Calc minNum = (x, y) -> x < y ? x : y; //추상 메서드의 구현
```

- 컴파일러가 함수형 인터페이스에 두 개 이상의 메소드 선언 시 오류 발생 시킴!

# 함수 객체(function object)

- 추상 메서드를 하나만 담은 인터페이스(함수형 인터페이스)의 **인스턴스**
- **특정 함수나 동작을 나타내는데 사용**
- JDK 1.1 → 함수 객체를 만드는 **주요 수단 : 익명 클래스**

    ```java
    Collections.sort(words, new Comparator<String>(){ //익명 클래스 구현
    	public int compare(String s1, String s2){
    		return Integer.compare(s1.length(), s2.length());
    	}
    });
    ```

    - Comparator 인터페이스 : 정렬을 담당하는 추상 전략
    → 문자열을 정렬하는 구체적인 전략은 익명 클래스로 구현
- **익명 클래스 방식은 코드가 너무 길어 함수형 프로그래밍에 적합 X**

    **→ 함수형 인터페이스의 인스턴스를 람다식을 사용해 간결하게 만들 수 있음**

# 람다식(lambda expression)

- **함수나 익명 클래스와 개념 비슷** BUT 코드 간결
- `→` 기호 → 매개변수를 이용해 `{}` 함수 바디를 실행한다는 뜻

```java
//메서드
int  min(int x, int y){
	return x < y ? x : y;
}

//람다식
(x,y) -> x < y ? x : y //-> return 생략 후 표현식 사용시 반환값은 표현식의 결과값
```

## 람다식 예제

```java
//익명 클래스 방식
Collections.sort(words, new Comparator<String>(){
	public int compare(String s1, String s2){
		return Integer.compare(s1.length(), s2.length());
	}
});

//람다식 방식
Collections.sort(words,
	(s1, s2) -> Integer.compare(s1.length(),s2.length())
});
```

## 람다식의 타입추론

- 람다, 매개변수, 반환값의 타입은 각각 (`Comparator<String>`, `String`, `int`)
    - **코드에서 언급X** → **컴파일러가 문맥에 맞게 타입 추론**
    - 컴파일러가 타입 결정 X →프로그래머 직접 명시

**→ 타입을 명시해야 코드가 더 명확할 때만 제외하고 람다의 모든 매개변수 타입은 생략!**

- ~~반환값이나 람다식 전체 형변환 상황 존재 BUT 드물다...~~

# 메소드 참조(method reference)

- 람다 표현식 → **단 하나의 메소드만 호출하는 경우** 람다 표현식에서 **불필요한 매개변수 제거**해 사용
- 불필요한 매개변수 제거 후  `::` 기호로 사용해 표현

```java
//람다식
(base, exp) -> Math.pow(base, exp) // 람다식이 단순히 인수전달 역할만 하기때문에 제거 가능

//메소드 참조
Math::pow;
```

# 생성자 참조

- 람다 표현식 → **단순히 객체 생성, 반환이 람다 표현식**이라면 생성자 참조로 변환 가능

```java
//람다식
(a) -> { return Object(a); }

//생성자 참조
Object::new;
```

## 비교자 생성 메서드

- 람다식 자리에 **비교자 생성 메서드 사용시 더 코드 간결**하게 표현 가능

```java
//람다식 방식
Collections.sort(words,
	(s1, s2) -> Integer.compare(s1.length(),s2.length());
});

//비교자 생성 메서드 방식
Collections.sort(words, comparingInt(String::length));
```

# 람다 활용

- 람다를 언어 차원에서 지원하면서 기존에는 적합하지 않았던 곳에서도 함수 객체를 실용적으로 사용 가능!

    → ex ) 열거 타입 상수 표현

```java
public enum Operation_new {
    PLUS("+"){
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-"){
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*"){
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/"){
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation_new(String symbol) {this.symbol = symbol;}

    @Override
    public String toString() { return symbol; }
    public abstract double apply(double x, double y);
}
```

- 각 상수마다 `apply` 메서드의 동작이 달라해 `apply` 메서드 각 상수마다 **재정의**
- 열거 타입의 인스턴스 필드를 이용하는 방식으로 상수별로 다르게 동작하는 코드 쉽게 구현 가능

    **→ 각 열거 타입 상수의 동작 → 람다로 구현해 생성자에 넘김 → 생성자는 람다를 인스턴스 필드로 저장**

```java
public enum Operation_new {
    PLUS ("+", (x + y) -> x + y), //생성자로 람다 넘김
    MINUS ("-", (x - y) -> x - y),
    TIMES ("*", (x * y) -> x * y),
    DIVIDE ("/", (x / y) -> x / y);

    private final String symbol;

    Operation_new(String symbol, DoubleBinaryOperator op) {//생성자에서 람다!!
			this.symbol = symbol;
			this.op = op;
		}
		//DoubleBinaryOperator op = (x + y) -> x + y;

    @Override public String toString() { return symbol; }

    public double apply(double x, double y){
			return op.applyAsDouble(x , y);
		};
}
```

- 이전 코드보다 간결하고 깔끔해짐!
- `DoubleBinaryOperator` 인터페이스
    - `java.util.function` 패키지 제공하는 다양한 함수 인터페이스 중 하나
    - double 타입의 인수 2개를 받아 double 타입 반환

# 람다식 문제점

- 메서드나 클래스와 달리,  람다는 문제점 존재
- **이름이 없고 문서화 불가** → 코드 자체로 동작이 명확히 설명 X, 줄 수 늘어난다면 람다 사용 금지!
- 람다식 **한 줄 best ! , 세 줄 이상은 가독성 나빠짐**
- **열거 타입 생성자 안의 람다는 열거 타입의 인스턴스 멤버에 접근 불가**
    - **인스턴스는 런타임에 만들어지기 때문**
    - 상수별 동작을 단 몇줄로 구현 어렵, 인스턴스 필드나 메서드 사용해야만 하는 상황
    → 상수별 클래스 몸체 사용 추천

## 람다식을 사용 못하는 코드

- 람다는 함수형 인터페이스에서만 사용
- 익명 클래스 사용
    - 추상 클래스의 인스턴스를 만들 때
    - 추상 메서드가 **여러 개**인 인터페이스의 인스턴스를 만들때
    - **함수 객체가 자신을 참조할때 (반드시)**
        - *람다*에서의 `this` → 바깥 인스턴스
        - *익명클래스*에서의 `this` → 인스턴스 자신

## 람다 직렬화

- 람다로 익명 클래스처럼 직렬화 형태가 구현별로 다를 수 있음
→ **람다를 질렬화하는 일 극히 삼가야 함**
- 직렬화해야만 하는 함수 객체 존재시 **private 정적 중첩 클래스**의 인스턴스 사용!
    - ex )  `Comparator`

# 정리

- **익명 클래스는 함수형 인터페이스가 아닌 타입의  인스턴스를 만들 때만 사용**
- 람다식은 결국 로컬 익명 구현 객체 생성 
BUT 람다식의 사용 목적 → 인터페이스가 가지고있는 메서드 간편, 즉흥적 구현해 사용 목적