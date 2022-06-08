# Item 53 - 가변인수는 신중히 사용하라

# 가변인수란?

가변인수는 필요에 따라 인수를 가변적으로 조절할 수 있는 인수로

- 가변인수 메서드는 명시한 타입의 인수를 0개 이상 받을 수 있다.
- 호출시 인수의 개수와 길이가 같은 배열 생성 → 인수들 배열에 저장해 가변인수 메서드에 전달!

```java
static int sum(int... args) {
	int sum = 0;
	for (int arg : args) 
		sum += arg;
	return sum;
}
```

가변인수 없던 시절에는 컬렉션이나 배열 이용해 가변인수를 대체했다고 한다.

# 가변 인수의 문제점

인수 개수는 **런타임에 자동 생성된 배열의 길이로 알 수 있는데 ...**

→ 인수가 1개 이상으로 제한되어야 하는 상황 발생 가능성 존재한다!!

ex ) 최솟값 찾는 메서드

```java
static int min(int... args) {
  if (args.length == 0)
      throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
  int min = args[0];
  for (int i = 1; i < args.length; i++)
      if (args[i] < min)
          min = args[i];
  return min;
}
```

이 코드에서 문제점을 찾아보자!

- 인수를 0개만 넣어 호출 시 컴파일 타임이 아닌 런타임에 실패!
- 코드 지저분
- `args` 유효성 검사 명시적으로 필요
- `min`의 초기값 `Integer.MAX_VALUE` 로 설정하지 않고는 ****`for-each`문 사용 못함

## 가변인수 문제 해결방법

```java
static int min(int firstArg, int... remainingArgs) {
  int min = firstArg;
  for (int arg : remainingArgs)
      if (arg < min)
          min = arg;
  return min;
}
```

→ 매개변수를 2개 받도록 유도하면 문제 해결 가능

# 가변인수의 이중성

가변인수는 인수 개수 정해지지 X 유용하지만 **성능에 민감한 상황이라면 가변인수 걸림돌**이 될 수 있다.

→ **가변인수는 호출될 때마다 배열을 새로 하나 할당 + 초기화 때문**

가변인수의 유연성이 필요할 때 선택할 수 있는 멋진 패턴 존재!?!

- 인수가 3개 이하인 메서드가 많이 사용된다면  4 ~ N 개의 메서드는 가변인수로 배열 생성

    ```java
    public void foo() {}
    public void foo(int a1) {}
    public void foo(int a1, int a2) {}
    public void foo(int a1, int a2, int a3) {}
    public void foo(int a1, int a2, int a3, int... rest) {}
    //4개 이상의 인수를 가진 메서드를 호출 할 때만 배열을 생성한다!
    //-> 성능적으로 극복 가능
    ```

- `EnumSet`의 정적 팩터리 → 열거 타입 집합 생성 비용 최소화

# 결론

- 인수 개수가 일정하지 않은 메서드를 정의해야 한다면 가변인수가 반드시 필요하다.
    - 필수 매개변수 가변인수 앞에 두기
- 가변인수 사용시 성능 문제까지 고려해야 한다.!