# Item 58 - 전통적인 for문보다는 for-each 문을 사용하라

# 전통적인 for 문

```java
for(Iterator<Element< i = c.iterator(); i.hasNext(); ){
	Element e = i.next();
	...
}

for(int i = 0; i < a.length; i++){
	...
}
```

위의 두 `for` 문은 전통적인 `for` 문으로 배열, 컬렉션을 순회하는 코드입니다. 

- 반복자와 인덱스 변수는 모두 코드를 지저분하게 할 뿐 우리에게 진짜 필요한 건 원소들 뿐입니다.
- 쓰이는 요소 종류가 즐어나면 오류가 생길 가능성이 높아집니다.
- 잘못된 변수를 사용 했을 때 컴파일러가 잡아주리라는 보장이 없습니다.
- 컬렉션이냐 배열이냐에 따라 코드 형태가 상당히 달라집니다.

이렇게 많은 단점들이 전통적인 `for` 문에는 존재합니다.

🤔그럼 `for` 문을 사용할 때 어떻게 해야 할까요?

바로, `for-each`문을 사용하면 됩니다.

# for-each 문

```java
for(Element e : elements) {
	... 
}
```

`for-each` 문은 향상된 `for`문 (enhanced of statement) 입니다.

위의 코드를 읽어 보자면 "`elements` 안의 각 원소 `e` 에 대해" 라고 읽습니다.

- 반복자와 인덱스 변수를 사용하지 않으니 코드가 깔끔해지고 오류가 날일이 없습니다.
- 하나의 관용구로 컬렉션과 배열을 모두 처리할 수 있어 어떤 컨테이너를 다루는지 신경 쓰지 않아도 됩니다.
- 반복 대상이 컬렉션이든 배열이든 속도는 그대로 입니다. ( `for-each` 문은 최적화 한것과 사실상 같습니다.)
- 컬렉션을 중첩해 순회한다면 이점은 더욱 커집니다.

이러한 장점으로 전통적인 `for` 문 보다 사용을 권장합니다.

```java
enum Suit { CLUB, DIAMOND, HEART, SPADE }
enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT,
    NINE, TEN, JACK, QUEEN, KING }

static Collection<Suit> suits = Arrays.asList(Suit.values());
static Collection<Rank> ranks = Arrays.asList(Rank.values());

List<Card> deck = new ArrayList<>();

for (Iterator<Suit> i = suits.iterator(); i.hasNext(); )
    for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
        deck.add(new Card(i.next(), j.next()));
```

위의 코드는 컬렉션을 중첩해 순회하는 코드입니다.

여기서 문제를 찾으셨나요? 바로 바깥 컬렉션의 반복자에서 `next` 메서드가 너무 많이 불린다는 것입니다.

```java
for (Suit suit : suits)
  for (Rank rank : ranks)
      deck.add(new Card(suit, rank));
```

이런식으로 코드를 고쳐주면 해결되는 문제입니다.

## for-each 문을 사용할 수 없는 상황

- 파괴적인 필터링(destructive filtering)

    → 컬렉션 순회하면서 원소를 제거해야 하는 상황 반복자의 `remove` 메서드 호출해야 하는 상황

- 변형(transforming)

    → 리스트, 배열 순회 시 원소의 값 일부 혹은 전체 교체 시 리스트의 반복자, 배열의 인덱스 사용해야 하는 상황

- 병렬 반복(parallel iteration)

    → 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수 사용해 엄격, 명시적 제어해야 한다.

## Iterable 인터페이스

```java
public interface Iterable<E>{
	Iterator<E> iterator();
}
```

Iterable 인터페이스는 하나의 메서드만 존재합니다. 

원소들의 묶음을 표현하는 타입을 작성해야 합다면 Iterable 을 추천드립니다. `for-each` 문과 잘 어울립니다.

# 정리

가능한 모든 곳에서 `for` 문이 아닌 `for-each` 문을 사용하는 것을 권장합니다.

이유는 명료하고, 유연하고, 버그를 예방해주소, 성능 저하를 없애줍니다.