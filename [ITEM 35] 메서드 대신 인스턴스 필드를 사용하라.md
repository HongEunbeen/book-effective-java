# Item 35 -  메서드 대신 인스턴스 필드를 사용하라

# ordinal 메서드

- 대부분의 열거 타입 상수는 자연스럽게 하나의 정숫값에 대응
- 모든 열거 타입은 해당 상수가 **그 열거 타입에서 몇 번째 위치인지를 반환**하는 `ordinal` 메서드 제공

```java
//ordinal을 잘못 사용한 예
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET,
    SECTET, SEPTET, OCTET, NONET, DETET;

    pulibc int numberOffMusicians(){ return ordianl() + 1;}
}
```

- 열거 타입 상수와 연결된 정숫값이 필요하면 `Ordinal` 메서드 이용 유혹
    - 동작은 하지만 유지보수 어렵
    - 상수 선언 순서를 바꾸는 순간 함수 오동작
    - 이미 사용 중인 정수와 값이 같은 상수는 추가할 방법이 없다.
    - 값을 중간에 비워둘 수 없다.
        - 값을 중간에 비우려면 더미 상수를 같이 추가해야만 한다.

**→ 열거 타입 상수에 연결된 값을 oridnal 메서드로 얻지 말고 인스턴스 필드에 저장하자**.

```java
public enum Ensemble_new {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5), SECTET(6), SEPTET(7),
	  OCTET(8), NONET(9), DETET(10), //11 비워둘 수 있다
    TRIPLE_QUARTET(12);

    private final int numberOffMusicians;

    Ensemble_new(int size) {
        this.numberOffMusicians = size;
    }

    public int numberOffMusicians(){return numberOffMusicians};
}
```

# Enum API 문서 oridnal

```java
public abstract class Enum<E extends Enum<E>> extends Object
implements Comparable<E>, Serializable
```

- 대부분 프로그래머는 이 메서드를 쓸 일이 없다.
- 이 메서드는 `EnumSet`과 `EnumMap` 같이 열거 타입 기반의 범용 자료구조에 쓸 목적으로 설계
    - 이런 용도가 아니라면 `ordinal` 메서드 절대 사용 x