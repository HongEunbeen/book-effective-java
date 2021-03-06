# Item 64 - 객체는 인터페이스를 사용해 참조하라

# 인터페이스를 사용해 참조

적합한 인터페이스만 있다면 매개변수뿐 아니라 반환값, 변수, 필드를 전부 인터페이스 타입으로 선언해야 합니다.

객체의 실제 클래스를 사용해야 할 상황은 오직 생성자로 생성할 때 뿐입니다.

```java
Set<Son> sonSet = new LinkedHashSet<>();

LinkedHashSet<Son> sonSet1 = new LinkedHashSet<>();
```

위의 두 예제만 봐도 인터페이스를 타입으로 사용한 첫 번째 예제가 더 좋은 코드란 걸 알 수 있습니다.

인터페이스를 타입으로 사용하는 습관을 길러두면 프로그램이 훨씬 유연해질 것입니다.

구현 클래스를 교체하고자 할 때 새 클래스의 생서자만 호출해주면 되기 때문입니다.

```java
Set<Son> sonSet = new HashSet<>();
```

 🤔 그럼 선언 타입과 구현 타입 둘다 변경하면 되는거 아닌가요?

→ 아닙니다!! 자칫하면 프로그램이 컴파일 되지 않는 문제도 발생할 수 있습니다. 

클라이언트에서 기존 타입에서만 제공하는 메소드를 사용하거나, 기존 타입을 사용해야 하는 다른 메서드에 그 인스턴스를 넘겼다고 가정할 시 코드는 컴파일되지 않습니다. 변수를 인터페이스 타입에 선언하면 이런일이 방지가 되기에 선업 타입에 인터페이스를 사용하는 것 입니다.

## 주의할 점

원래의 클래스가 인터페이스의 일반 규약 이외의 특별한 기능을 제공하고 주변 코드가 이 기능에 기대어 동작 시 새로운 클래스도 반드시 같은 기능을 제공해야 합니다.

⇒ 인터페이스 참조 객체의 구현 타입 클래스 변경시 이전 클래스와 반드시 같은 기능 제공해야 합니다.

🤔구현 타입을 바꾸려고 하는 이유는 무엇일까요?

바로, 원래 것보다 성능이 좋거나 멋진 신기능을 제공하기 때문입니다.

하지만 적합한 인터페이스 없다면 당연히 클래스를 참조해야 합니다.

- `String` `BigInteger` 같은 값 클래스
- 클래스 기반으로 작성된 프레임워크가 제공하는 객체
    - `OutputStream` 등 [java.io](http://java.io) 패키지가 속하며 이런 경우라도 특정 구현 클래스보다는 추상 클래스인 기반 클래스를 사용해 참조하는걸 추천드립니다.
- 인터페이스에는 없는 특별한 메서드를 제공하는 클래스
    - `PriorityQueue` 클래스는 `Queue` 인터페이스에는 없는 `comparator` 메서드를 제공하는 데 이 메서드가 속하며 클래스 타입을 직접 사용하는 경우는 추가 메서드를 꼭 사용해야 하는 경우로 최소화 해야 합니다.(남발 금지)

→ 적합한 인터페이스가 없다면 클래스의 계층구조 중 필요한 기능을 만족하는 가장 덜 구체적인 상위의 클래스를 타입으로 사용하는 걸 권장합니다.