# Item 37 - ordinal 인덱싱 대신 EnumMap을 사용하라

# ordinal 메서드

- 배열이나 리스트에서 원소를 꺼낼때 사용하는 메서드
- **해당 열거체 상수가 열거체 정의된 순서(0부터) 반환 → *상수값 자체가 아님을 주의***

# ordinal 메서드로 배열 인덱스 활용 시 문제점

```java
//식물의 생애주기에 따라 분류하는 코드
public class Plant {
    enum LifeCycle {
        ANNUAL, PERENNIAL, EIENNIAL
    }

    final String name;
    final LifeCycle lifeCycle;

    Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}

//main
public class main {
    public static void main(String args[]) {
        Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];

        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }

        for (Plant p : garden) {
						//garden의 식물을 분류해서 각 enum에 넣는 코드
            //생애주기에 따라 넣어야 하기 때문에 각 식물의 생애주기를 찾아서 index를 구해서 넣어야 한다.
            plantsByLifeCycle[p.LifeCycle.ordinal()].add(p);
        }

        // 출력
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            System.out.println("%s : %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
        }
    }
}
```

- 코드 설명

![](./Image/Item37_Image1.png)

- 문제점
    - **배열은 제네릭과 호환되지 않아 비검사 형변환**을 수행(컴파일 깔끔X)

        ```java
        Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
        ```

    - **배열은 각 인덱스의 의미 알 수 없어** 출력 결과에 직접 레이블을 달아야 함

        ```java
        System.out.println("%s : %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
        ```

    - **정확한 정숫값을 사용한다는 것 개발자가 직접 보증해야 됌**
    → 정수는 열거타입과 달리 타입 안전하지 않기 때문에!

# EnumMap이란?

- 배열은 실질적으로 열거 타입 상수를 값으로 매핑하는 일 → `Map` 사용 가능
- **열거 타입을 키로 사용하도록 설계한 아주 빠른 `Map` 구현체**

```java
import java.util.EnumMap;

public class main {
    public static void main(String args[]) {

        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

        for( Plant.LifeCycle lc : Plant.LifeCycle.values()){
            plantsByLifeCycle.put(lc, new HashSet<>);
        }

        for( Plant p : garden){
            plantsByLifeCycle.get(p.LifeCycle).add(p);
        }

        System.out.println(plantsByLifeCycle);
    }
}
```

- 코드 설명

![](./Image/Item37_Image2.png)

## EnumMap의 생성자

```java
new EnumMap<>(Plant.LifeCycle.class);
```

- EnumMap의 생성자가 받는 키 타입의 *Class 객체*는 **한정적 타입 토큰**
→ 런타임 제네릭 타입 정보를 제공

## EnumMap 장점

- **맵의 키 = 열거 타입** → 그 자체로 **출력용 물자열 제공**(출력 결과에 직접 레이블 달 일 없음)
- **배열 인덱스를 계산하는 과정에서 오류 날 가능성 원천 봉쇄**
- 성능 배열 사용 코드와 비슷함
- **안전하지 않은 형변환은 쓰지 않음**
- **내부에서 배열을 사용**
    - 내부 구현 방식을 안으로 숨겨서 **`Map`의 타입 안전성과 배열의 성능 모두 얻음**

## 스트림과 EnumMap

- *스트림* 사용하여 *맵* 관리 → 코드 더 줄이기 가능

```java
//스트림을 사용한 코드
System.out.println(Arrays.stream(garden).collect(groupingBy(p -> p.lifeCycle)));
```

- 스트림 사용의 **문제점**
    - `EnumMap` 이 아닌 고유한 맵 구현체 사용으로 `EnumMap`을 써서 얻은 공간과 성능 이점 사라짐

```java
//스트림 + EnumMap을 사용한 코드
System.out.println(Arrays.stream(garden).collect(
			groupingBy(
					p -> p.lifeCycle,
					() -> new EnumMap<>(LifeCycle.class), 
					toSet())));     //매개변수 3개
```

- `Collectors.groupingBy` 메서드 → `mapFactory` 매개변수에 **원하는 맵 구현제 명시해 호출 가능**

→ **단순한 프로그램에서는 최적화 굳이 필요 없지만, 맵을 빈번히 사용하는 프로그램에서 꼭 필요**

- 스트림과 `EnumMap`을 함께 사용 **주의사항**
    - `EnumMap` 버전 → 식물의 생애주기당 **하나씩의 중첩 맵 생성**
    - 스트림 버전 → 해당 생애주기에 **속하는 식물이 있을때만 생성**

# EnumMap  중첩 사용

```java
//두가지 상태(Phase)를 전이(Transition)와 매핑하도록 구현한 프로그램
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT, FREEZE, BOIL, CONDENES, SUBLIME, DEPOSIT;

				//행은 from의 ordinal을, 열은 to의 ordinal을 인덱스로 사용
        private static final Transition[][] TRANSITIONS = {
					{ null, MELT, SUBLIME }, 
					{ FREEZE, null, BOIL },
          { DEPOSIT, CONDENES, null } 
				};

        // 한 상태에서 다른 상태로의 전이를 반환
        public static Transition from(Phase from, Phase to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```

- 문제점
    - 컴파일러는 `ordinal과` 배열 인덱스의 관계를 알 수 없음
        - `Phase`나 `Phase.Transition` 열거 타입을 수정 시 `TRANSITIONS`(상전이 표)를 함께 수정하지 않거나 실수로 잘못 수정하면 **런타임 오류 발생
        →** `ArrayIndexOutOfBoundException`, `NullPointerException`
    - `TRANSITIONS`(상전`이 표)의 크기는 상태의 가짓수가 늘어나면 제곱해서 커지지만 `null` 로 채워지는 칸도 늘어남
- **해결방법** → `EnumMap`을 사용
    - 전이 하나를 얻으려면 이전상태(from)와 이후 상태(to)가 필요하니 **맵 2개를 중첩하면 쉽게 해결 가능**

## 중첩 EnumMap 사용 방법

- 안쪽 맵 : 이전 상태와 전이 연결
- 바깥 맵 : 이후 상태와 안쪽 맵 연결
- 전이 전후의 두 상태를 전이 열거 타입 `Transition`의 입력으로 받아, 이 `Transition` 상수들로 중첩된 `EnumMap`을 초기화

```java
//중첩 EnumMap으로 데이터와 열거 타입 쌍을 연결
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID), BOIL(LIQUID, GAS), CONDENES(GAS, LIQUID), SUBLIME(SOLID, GAS),
        DEPOSIT(GAS, SOLID);

        private final Phase from;
        private final Phase to;

        private static final Map<Phase, Map<Phase, Transition>> m 
            = Stream.of(values()).collect(
                groupingBy(t -> t.from, 
                            () -> new EnumMap<>(Phase.class), 
                            toMap(t ->t.to, t -> t, (x,y) -> y. () -> new EnumMap<>(Phase.class))));
                            
        // 한 상태에서 다른 상태로의 전이를 반환
        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}
```

- 맵의 타입 → `Map<Phase, Map<Phase, Transition>>`
    - 이후 상태에서 전이로의 맵에 대응시키는 맵
- 맵의 맵 타입 → `Map<Phase, Transition>` 
(초기화 위해 수집기 `java.util.stream.Collector` 2개 사용)
    - `groupingBy` → 전이를 이전 상태 기준으로 묶음
    - `toMap` → 이후 상태를 전이에 대응시키는 `EnumMap`을 생성
        - `toMap`의 **병합함수** `(x ,y) → y` : 선언만 하고 실제 사용 X 
        → 단지 `EnumMap`을 얻으려면 맵 팩터리 필요, 수집기들은 *점층적 팩터리(telescoping factory)*를 제공하기 때문

### 새로운 상태 추가 예제

- **배열**로 만든 코드
    - 새로운 상수를 `Phase`에 1개, `Phase.Transition`에 2개를 추가하고, 원소 9개 짜리인 배열들의 배열을 원소 16개 짜리로 교체
- **EnumMap**로 만든 코드
    - 상태 목록에 `PLASMA` 추가 후 전이 목록에 2개만 추가

    ```java
    public enum Phase {
        SOLID, LIQUID, GAS, PLASMA;

        public enum Transition {
            MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
    				BOIL(LIQUID, GAS), CONDENES(GAS, LIQUID),
    				SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID), 
    				IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);
    		...
    	}
    }
    ```

    - 맵들의 맵이 배열들의 배열로 구현 → 낭비되는 공간과 시간거의 없이 명확하고 안전하게 유지보수 가능

# 정리

- 배열의 인덱스를 얻기 위해 `ordinal`을 쓰는 것 일반적으로 좋지 않으니, `EnumMap` 사용 추천
- 다차원 관계 `EnumMap<...` , `EnumMap<...>>` 표현
