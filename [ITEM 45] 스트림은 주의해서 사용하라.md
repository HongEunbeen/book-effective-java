# Item 45 - 스트림은 주의해서 사용하라

# 스트림 API

- 스트림 API는 다량의 데이터 처리 작업을 돕고자 자바 8에 추가
- 컬렌션, 배열, 파일, 정규표현식 매턴 캐처, 난수 생성기, 다른 스트림 등에서 스트림의 원소 탄생
- 스트림 안의 데이터 원소
    - 객체 참조
    - 기본 타입 : `int`, `long`, `double`

## 추상 개념 핵심

- 스트림(stream)은 데이터 원소의 유한 혹은 무한 시퀀스(sequence)를 뜻함
- 스트림 파이프라인(stream pipeline)은 이 원소들로 수행하는 연산 단계를 표현하는 개념

## 스트림 파이프라인 동작 흐름

- 시작 : 소스 스트림
- 하나 이상의 중간 연산(intermediate operation)
- 끝 : 종단 연산(terminal operation)

## 중간 연산 특징

- 중간 연산들은 모두 한 스트림을 다른 스트림으로 변환
- 변환된 스트림의 원소 타입은 변환 전 스트림의 원소타입과 같을 수도 있고 다를 수도 있음

## 종단 연상 특징

- 종단 연산은 마지막 중간 연산이 내놓은 스트림에 최후의 연산을 가한다.
- 원소를 정렬해 컬렉션에 담거나, 특정 원소 하나를 선택하거나, 모든 원소를 출력하는 식

→ 지연 평가(Lazy evaluation)

- 지연 평가 → 조단 연산이 호출될 때 이루어짐 (스트림 파이프라인은 지연 평가)
- 종단 연산에 쓰이지 않는 데이터 원소 → 계산에 사용 X
- 지연 평가로 무한 스트림 다룰 수 있음
- 종단 연사 없는 스트림 파이프라인 → 아무일도 하지 않는 명령어인 no-op과 같으니 종단 연산 필수

## 플루언트 API(fluent API)

- 스트림 API는 메서드 연쇄를 지원하는 프로루언트 API
- 파이프라인 하나를 구성하는 모든 호출(생성, 중간 연산, 종단 연산) 연결 → 단 하나의 표현식 완성
    - 파이프라인 여러 개를 연결해 표현식 하나로 생성 가능

## parallel 메서드(병렬처리)

- 기본적으로 스트림 파이프라인 → 순차적 수행
- 파이프라인 병렬로 실행 → 파이프라인 구성하는 스트림 중 하나에서 prallel 메서드 호출

# 스트림 API 사용시 주의사항

- 스트림을 제대로 사용하면 프로그램이 짧고 깔끔 → 잘못 사용시 읽기 어렵고 유지보수 힘들어짐

```java
//사전 파일에서 단어를 읽어 사용자가 지정한 값보다 원소 수가 많은 아나그램 출력 예제
//스트림 미사용
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

public class Anagrams {
    public static void main(String[] args) {
        File dictionary = new File(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        Map<String, Set<String>> groups = new HashMap<>();
        try (Scanner s = new Scanner(dictionary)) {
            while (s.hashNext()) {
                String word = s.next();
                groups.computeIfAbsent(alpabetize(word), (unused) -> new TreeSet<>().add(word));
            }
        }

        for (Set<String> group : groups.values()) {
            if (group.size() >= minGroupSize) {
                System.out.println(group.size() + ":" + group);
            }
        }
    }

    private static String alpabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

- 사용자가 명시한 사전파일에서 각 단어를 읽어 맵에 저장
- 맵의 키 : 단어를 구성하는 철자들을 알파벳순으로 정렬한 값(아나그램끼리 같은 키 공유)
- 맵의 값 : 같은 키를 공유한 단어들을 담은 집합(`Set`)

    ```java
    groups.computeIfAbsent(alpabetize(word), (unused) -> new TreeSet<>().add(word));
    ```

    - `computeIfAbsent()`
        - 맵 안에 키가 있는지 찾음
        - 키 존재 : 단순히 그 키에 매핑된 값 반환
        - 키 미존재 : 건네진 함수 객체를 키에 적용해 값 계산 후 그 키와 값 매핑 후 계산된 값 반환
- 사전 처리 후 가 집합은 사전에 등재된 아나그램 담은 상태
- 맵의 `values()` 메서드로 아나그램 집합들 원소수 → `minGroupSize`보다 많은 집합들 출력

```java
//사전 파일에서 단어를 읽어 사용자가 지정한 값보다 원소 수가 많은 아나그램 출력 예제
//스트림 사용
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Anagrams_Stream {
    public static void main(String[] args) {
        File dictionary = new File(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

       try(Stream<String> words = Files.lines(dictionary)){
           words.collect(
               groupingBy(word -> word.chars().sorted()
													.collect(StringBuilder::new,
													(sb,c -> sb.append(char) c), 
													StringBuilder::append).toString()))
							 .values().stream()
							 .filter(group -> group.size() >= minGroupSize)
							 .map(group -> group.size() + ": " + group)
							 .forEach(System.out::println);
       }
    }
}
```

- 사전 파일을 여는 부분만 제외하면 프로그램 전체가 단 하나의 표현식으로 처리

→ 스트림을 과용하면 프로그램이 일거나 유지보수하기 어려워짐!

## 해결책

- 스트림을 적절히 활용하면 깔끔하고 명료해짐

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Anagrams_Stream_New {
    public static void main(String[] args) {
        File dictionary = new File(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

       try(Stream<String> words = Files.lines(dictionary)){
           words.collect(groupingBy(word -> alphabetize(word))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .forEach(g -> System.out.println(g.size() + ":"  + g));
       }
    }

    private static String alpabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

`Stream<String> words = Files.lines(dictionary)` 

- 파일의 한 행을 요소로 stream 생성
    - 스트림 변수명 : words → 스트림 안의 각 원소가 단어임을 명시

`words.collect(groupingBy(word -> alphabetize(word))` 

- (종단 연산)모든 단어를 수집해 맵으로 모은 뒤 `values()` 로 새로운 값(`List<String>`) 반환

`.stream()`

- (스트림 생성)`List<String>`의 `Collection` 인터페이스의 `stream()` 함수 사용해 stream 생성

`.filter(group -> group.size() >= minGroupSize)`

- (중간 연산)원소가 `minGroupSize`보다 적은 것은 필터링돼 무시 후 새로운 stream 반환

`.forEach(g -> System.out.println(g.size() + ":"  + g));`

- (종단 연산)살아남은 리스트 출력

## char  문제점

```java
private static String alpabetize(String s) {
    char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
}
```

- 스트림으로 구현 가능 BUT 명확성 떨어지고 잘못 구현할 가능성 높음
- 자바가 기본 타입인 char 용 스트림을 지원하지 않기 때문!!
- char 값들을 처리할 때는 스트림을 삼가는 편 추천

# 스트림과 반복문

- 기존 코드는 스트림을 사용하도록 리팩터링하되, 새 코드가 더 나아 보일 때만 반영하자.
- 스트림과 반복문을 적절히 조합하는게 최선

## 반복문(코드 블록) 사용

- 코드 블록에서는 범위 안의 지역변수를 읽고 수정가능
→ 람다에서는 final이거나 사실상 final인 변수만 읽을 수 있고 지연변수 수정 불가
- 코드 블록에서는 return문을 사용해 메서드를 빠져 나가거나 break, continue 문으로 블록 바깥의 반복문을 종료하거나 반복을 한 번 건너뛸 수 있다.

→ 계산 로직에서 이상의 일들을 수행해야 한다면 반복문 사용

## 스트림 사용

- 원소들의 시퀀스를 일관되게 변환한다.
- 원소들의 시퀀스를 필터링한다.
- 원소들의 시퀀스를 하나의 연산을 사용해 결합한다
- 원소들의 시퀀스를 컬렉션에 모든다.
- 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.

### 스트림 사용시 처리하기 어려운 점

- 파이프라인의 여러 단계의 값들에 동시에 접근 어려움
    - 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는 구조이기 때문!
- 앞 단계의 값이 필요할 경우 → 매핑을 거꾸로 수행하는 방법 추천

## 스트림 VS 반복문

- 스트림과 반복 중 어느 쪽을 써야 할지 모르는 경우 발생
- 개인 취향과 프로그래밍 환경의 문제

# 정리

- 스트림과 반복문 두 방법을 조합했을 때 가장 멋지게 해결가능
- 스트림과 반복 중 어느쪽이 나은지 확시하기 어렵다면 둘 다 해보고 더 나은 쪽 선택