# Item 36 - 비트 필드 대신 EnumSet을 사용하라

# 비트 필드(bit field)

- 비트별 OR를 사용해 여러 상수를 하나의 집합으로 모을 수 있으며 이렇게 만들어진 집합
- 비트 필드를 사용하면 비트별 연산을 사용해 합집합과 교집합 같은 집합 연산을 효율적 수행 가능

# 비트 필드 열거 상수

열거한 값들이 주로 단독이 아닌 집합으로 사용될 경우, 예전에는 각 상수에 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴 사용해 옴!

```java
public class Text {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

    // 매개변수 syltes는 0개 이상으 ㅣSTYLE_ 상수를 비트별 OR 한 값이다.
    public void applyStyles(int styles){...}

    // text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
}
```

정수 열거 상수의 단점을 그대로 지니며 단점 존재

- **비트 필드 값이 그래도 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기가 훨씬 어려움**
- 비트 필드 하나에 녹아있는 모든 **원소 순회 하기도 어려움**
- 최대 몇 비트가 필요한지를 **API 작성 시 미리 예측하여 적절한 타입을 선택함**
    - API를 수정하지 않고는 비트수(32 or 64)를 **더 늘릴 수 없음**

# 더 나은 대안 - EnumSet 클래스

java.util 패키지의 `EnumSet` 클래스

```java
public class Text_new {
  public enum Style {
      BOLD, ITALIC, UNDERLINE, STRIKETHROUGH
  };

  // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다. > 이왕이면 인터페이스로 받는게 일반적으로 좋은 습관
  public void applyStyles(Set<Style> styles){...}

  //applyStyles 메서드에 EnumSet 인스턴스를 건네는 클라이언트 코드
  //EnumSet은 집합 생성 등 다양한 기능의 정적 팩터리를 제공하는데, 다음 코드에서는 그중 Of 사용
  // text.applyStyles(EnumSet.of(Style.BOLD, Sytle.ITALIC));
}
```

- 열거 타입 상수의 값으로 구성된 집합을 **효과적으로 표현 가능**
- `set` 인터페이스를 완벽히 구현 (다른 어떤 `Set` 구현체와도 함께 사용 가능)
- **타입 안전함**
- 내부는 **비트 벡터로 구현**
    - 원소가 총 64개 이하라면 `EnumSet` 전체를 `long` 변수 하나로 표현하여 비트 필드에 비견되는 성능을 보여줌
    - `removeall`과 `retainAll` 같은 대량 작업은 비트 필드를 사용할 때 쓰는 것과 같은 비트를 효율적으로 처리할 수 있는 산술 연산을 써서 구현
    - 난해한 작업을 `EnumSet`이 다 처리해줌

# 정리

- 열거할 수 있는 타입을 한데 모아 **집합 형태로 사용한다고 해도 비트 필드를 사용할 이유 없음!!**
- `EnumSet` 클래스가 비**트 필드 수준의 명료함과 성능을 제공 + 열거 타입 장점**
- `EnumSet`의 **유일한 단점**
    - 자바 9까지는 아직 불변 `EnumSet` 만들 수 없다는 것
    - 명확성과 성능이 조금 희생되지만 `Collections`, `unmodifiableSet`으로 `EnumSet`을 감싸 사용 가능