# Item 3 - private 생성자나 열거 타입으로 싱글턴임을 보증하라

# 싱글턴(singleton)

인스턴스를 오직 하나만 생성할 수 있는 클래스

- 함수와 같은 무상태(stateless) 객체
- 설계상 유일해야 하는 시스템 컴포넌트

클래스를 싱글턴으로 생성 시 → 사용하는 클라언트를 테스트하기 어려워짐

- 타입을 인터페이스로 정의한 다음 그 인터페이스를 구현해서 만든 싱글턴이 아니라면 싱글턴 인스턴스를 가짜(mock) 구현으로 대체할 수 없기 때문

## 싱글턴 생성 방식

두 방식 모두 생성자는 `private`으로 감춰두고, 유일한 인스턴스에 접근할 수 있는 수단으로 `public static` 멤버 마련

### 1. `public static final` 필드 방식의 싱글턴

- `new` 연산자를 사용하지 못하게 강제하는 것이 `private` 생성자

```java
public class Singleton {
   public static final Singleton INSTANSE = new Singleton();

   private Singleton() { ... }
 
   public void print() {
       System.out.println("Singleton Instance Created..");
   }
}
```

### 2. 정적 팩터리 방식의 싱글턴

```java
public class Singleton2 {
   private static final Singleton2 INSTANCE = new Singleton2();
 
   private Singleton2(){ ... }
 
   public static Singleton2 getInstance(){return INSTANCE};
   //여기서 항상 같은 객체의 참조를 반환하므로 제2의 Singleton2 의 인스턴스란 결코 만들어지지 않는다.
  
 
   public void print() {
       System.out.println("");
   }
}
```

- `public static final`
    - 장점 : **해당 클래스가 싱글턴임이 API에 명백히 드러난다, 간결함**
- 마음이 바뀌면 API를 변경하지 않고도 싱글턴이 아니게 변경 가능
- 정적 팩터리 → **제네릭 싱글턴 팩터리**로 만들기 가능
- 정적 팩터리의 메서드 참조를 공급자로 사용할 후 있다
    - `Singleton2::getInstance -> Supplier<Singleton2>`로 사용하는 방식

→ 둘중 하나의 방식으로 만든 싱글턴 클래스를 직렬화하려면 단순히 Serializable구현한다고 선언하는 것만으로는 부족

### 3. 열거 타입 방식의 싱글턴 → 바람직

```java
public class Singleton3 {
   INSTANCE;
 
   public void print(){
       System.out.println("enum SingleTon3");
   }
}
```

- `public` 필드 방식과 비슷하지만 더 간결하고 추가 노력 없이 **직렬화 가능**
- 아주 복잡한 직렬화 상황이나 리플렉션 공격에서도 **제2의 인스턴스가 생기는 일을 완벽히 막아줌**
- 만들려는 싱글턴이 `Enum` 이외의 **클래스를 상속해야 한다면 이 방법 사용 x**

**→ 대부분 상황에서는 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법**