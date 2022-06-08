# Item 27 - 비검사 경고를 제거하라

# 비검사 컴파일러 경고

- 비검사 형변환 경고, 비검사 메서드 호출, 비검사 매개변수화 가변인수 타입 경고, 비검사 변환 경고 등
    - `Set<Lark> exaltation = new HashMap();`
    → 이러한 경고는 컴파일러가 무엇이 잘못됐는지 설명해줌
        - 해결 방법 1 :  **타입 매개변수 명시** 
        `new HashMap<Lark>();`
        - 해결 방법 2 : **다이아몬드 연산자<>** → 컴파일러가 올바른 실제 타입 매개변수 추론
        `new HashMap<>();`
- 대부분의 비검사 경고는 쉽게 제거 가능

# 할 수 있는 한 모든 비검사 경고를 제거하라

- 모두 제거 → 그 코드는 **타입 안전성 보장**
(런타입에 ClassCastException 발생 X)
- 경고를 제거할 수는 없지만 **타입 안전하다고 확신 →** **@SuppressWarnings(“unchecked”) 애너테이션**
    - 애너테이션을 달아 **경고 숨김**
    - 타입 안전함 검증 x 채 애너테이션 
    → 경고 없이 컴파일 가능하지만 런**타임에는 여전히 ClassCastException 발생 가능성**
    - 안전하다고 검증된 **비검사 경고를 숨기지 않아도 문제**
        - 다른 새로운 경고가 파뭍힐 수 있다.(거짓 경고 속에!!)

## @SuppressWarnings 애너테이션

```jsx
public <T> T[] toArray(T[] a) {
    if(a.length < size){
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Arrays.copyOf(elements, size, a.getClass());
        return result;
    }

    System.arraycopy(elemtns, 0, a, 0, size);
    if(a.length > size)
        a[size] = null;
    return a;
}
```

- 개별 지역변수 선언부터 클래스 전체까지 어떤 선언 가능
    - 항상 가능한 한 좁은 범위에 적용
        - 변수 선언, 짧은 메서드, 생성자 등
- 심각한 경고를 놓칠 수 있어 **절대로 클래스 전체에 적용 X**
    - 한 줄이 넘는 메서드나 생성자에 딸려있으면 **지역변수 선언쪽으로 옮겨야 됌**
    (지역 변수를 새로 선언하더라도)
- 애너테이션은 **선언에만 달 수** 있어 **return문에는 애너테이션 불가능**

    ```jsx
     @SuppressWarnings("unchecked")
      T[] result = (T[]) Arrays.copyOf(elements, size, a.getClass());
      return result;
    ```

    - `return`  문에 애너테이션 X 
    → 반환값을 담을 **지역변수를 선언한 후 그 변수에 애너테이션** 달아줌
- 경고를 무시해도 **안전한 이유를 항상 주석 작성**
    - 다른 사람이 그 코드를 이해하는데 도움
    - 코드를 잘못 수정하여 타입 안전성을 잃는 상황 줄여줌

# 정리

- **비검사 경고는 중요하니 무시하지 말자**
- 모든 비검사 경고 → 런타임에 `ClassCastException` 일으킬 수 있는 **잠재적 가능성**
    - 최선을 다해 제거
- 경고 없앨 방법 x
    - **코드가 타입 안전함을 증명**
    - 가능한 한 범위를 좁혀 `@SuppressWarnings(“unchecked”)` **어노테이션으로 경고 숨기기**
        - **경고를 숨기기로 한 근거를 주석**으로!