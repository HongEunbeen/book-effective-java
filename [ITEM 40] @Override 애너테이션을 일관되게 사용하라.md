# Item 40 - @Override 애너테이션을 일관되게 사용하라

# @Override 애너테이션

- 자바가 **기본으로 제공하는 애너테이션 중 가장 중요한 에너테이션**
- **메서드 선언에만 사용 가능**
- 애너테이션 사용 시 **상위 타입의 메서드를 재정의했음을 뜻함**
- 일괄되게 사용하면 여러 가지 악명 높은 버그들 예방

## @Override 미사용 예시

```java
//똑같은 소문자 2개로 구성된 바이그램 26개를 10번 반복해 집합에 추가 -> 집합의 크기 출력
import java.util.HashSet;

public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }
		
    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size());
    }

}
```

- 예상 출력 : 26 (Set은 중목을 허용하지 않기 때문에)
- 실체 출력 : 260
- `HashSet` → `equals()` , `hashCode()`메서드 재정의 X
→ **재정의(Override) 한게 아닌 다중정의(overloading) 함**

    ![Item%2040%20-%20@Override%20%E1%84%8B%E1%85%A2%E1%84%82%E1%85%A5%E1%84%90%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%89%E1%85%A7%E1%86%AB%E1%84%8B%E1%85%B3%E1%86%AF%20%E1%84%8B%E1%85%B5%E1%86%AF%E1%84%80%E1%85%AA%E1%86%AB%E1%84%83%E1%85%AC%E1%84%80%E1%85%A6%20%E1%84%89%E1%85%A1%E1%84%8B%E1%85%AD%2051d6c465d8914414869dde931e63e4e3/Untitled.png](Item%2040%20-%20@Override%20%E1%84%8B%E1%85%A2%E1%84%82%E1%85%A5%E1%84%90%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%89%E1%85%A7%E1%86%AB%E1%84%8B%E1%85%B3%E1%86%AF%20%E1%84%8B%E1%85%B5%E1%86%AF%E1%84%80%E1%85%AA%E1%86%AB%E1%84%83%E1%85%AC%E1%84%80%E1%85%A6%20%E1%84%89%E1%85%A1%E1%84%8B%E1%85%AD%2051d6c465d8914414869dde931e63e4e3/Untitled.png)

    - `Object`의 `equals()` 재정의 위한 매개변수 타입 → `Object`
        - `Object`에서 상속한 `equals()`와 별개인 `equals()` **새로 정의한 꼴**
    - `Object`의 `equals()`는 *== 연산자*와 똑같이 **객체 식별성(identity)만을 확인**
    - 소유한 바이그램 10개 각각이 서로 **다른 객체로 인식 → 260개 출력(버그 발생!)**

## @Override 사용 예시

```java
//똑같은 소문자 2개로 구성된 바이그램 26개를 10번 반복해 집합에 추가 -> 집합의 크기 출력
import java.util.HashSet;

public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

		@Override
    public boolean equals(Object o) {
				if(!(o instanceof Bigram))
					return false;
				Bigram b = (Bigram) o;
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size());
    }

}
```

- **@Override 애너테이션을 사용하면** **컴파일 오류가 발생**하여 잘못된 부분 알려줌

    ```java
    //컴파일 오류 발생
    @Override
    public boolean equals(Bigram o) {
        return b.first == first && b.second == second;
    }

    //컴파일 오류 해결
    @Override
    public boolean equals(Object o) {
    		if(!(o instanceof Bigram))
    			return false;
    		Bigram b = (Bigram) o;
        return b.first == first && b.second == second;
    }
    ```

- **상위 클래스의 메서드를 재정의 하려는 모든 메서드에 @Override 사용!**

## @Override 사용 예외

- 구체 클래스에서 상위 클래스의 **추상 메서드를 재정의할 때는 굳이 @Override  추가 X 가능**

    ```java
    abstract class Animal { 
    	abstract void cry(); 
    }

    class Cat extends Animal { //정상 작동
    	//@Override 추가 X 
    	void cry() { System.out.println("냐옹냐옹!"); } 
    }

    class Dog extends Animal { //정상 작동
    	@Override //추가해도 상관X 
    	void cry() { System.out.println("멍멍!"); } 
    }

    class Pig extends Animal { // 컴파일러 오류 발생
    	 void eat() { System.out.println("냠냠"); }
    		//컴파일러가 구현하지 않은 추상 메서드 cry 알려줌
    }

    //출처 : http://www.tcpschool.com/java/java_polymorphism_abstract
    ```

    - 구체 클래스인데 **아직 구현하지 않은 추상 메서드 존재 시 컴파일러 알려줌**
- **재정의 메서드 모두에 @Override 일괄로 붙이는게 좋아 보인다면 상관X**

## IDE의 @Override 사용 권장

- IDE는 **@Override를 일관되게 사용하도록 권장**
    - 관련 설정 활성화 시 @Override가 달려있지 않은 메서드가 실제로는 재정의 했으면 경고 발생
- **@Override 일관되게 사용한다면 이처럼 실수로 재정의했을 때 경고 발생**
- **IDE와 컴파일러 덕분에 우리는 의도한 재정의만 정확하게 재정의 가능**

## 인터페이스 메서드 재정의 @Override

- *디폴트 메서드* 지원시작으로 **인터페이스 메서드 구현한 메서드 재정의 가능**
    - @Override 다는 습관 들이면서 시**그니처 올바른지 재자 확인 가능**
- 인터페이스에 ***디폴트 메서드*가 없음을 안 다면 이를 구현한 메서드에서는 @Overrdie 생략**
→ 조금 더 깔끔한 코드 구현 가능
- 추상 클래스나 인터페이스에서는 **상위 클래스나 상위 인터페이스의 메서드를 재정의하는 모든 메서드 @Override 다는 것 좋음**
    - 상위 클래스가 구체클래스이든 추상 클래스든 마찬가지
    - ex ) Collection 인터페이스 확장한 Set 인터페이스는 새로 추가한 메서드가 없으므로 모든 메서드 선언에  @Override 추가해 실수로 추가한 메서드 없음 보장

# 정리

- **재정의한 모든 메서드에 @Override 애너테이션 의식적으로 달면 컴파일러 실수 바로 알려줌**
- **구체 클래스에서 상위 클래스의 추상 메서드를 재정의한 경우에만 애너테이션 달지 않아도 됌
(추가해도 문제 없음)**