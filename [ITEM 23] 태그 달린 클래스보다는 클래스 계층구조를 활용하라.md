# Item 23 - 태그 달린 클래스보다는 클래스 계층구조를 활용하라

### 태그 달린 클래스 단점

```java
//Figure.java
class Figure {
    enum Shape {
        RECTANGEL, CIRCLE
    };

    final Shape shape;//태그 필드(현재 모양)

    double length;//RECTANGEL만을 위한 필드
    double width;//RECTANGEL만을 위한 필드
	
    double radius;//CIRCLE만을 위한 필드

    Figure(double length, double width) {//RECTANGEL 생성자
        shape = Shape.RECTANGEL;
        this.length = length;
        this.width = width;
    }

    Figure(double radius) { //CIRCLE 생성자
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    double area(){ //태그 값에 따라 동작이 달라지는 메서드
        switch(shpae){
            case RECTANGEL:
                return length * width;
            case CIRCLE : 
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }

}
```

- 열거 타입 선언, 태그 필드, switch 문 등 **쓸데없는 코드가 많다.**
- 여러 구현이 한 클래스에 혼합 → **가독성 낮다**.
- 다른 의미를 위한 코드도 언제나 함께 하니 **메모리 많이 사용한다**.
- 필드들을 final로 선언 
→ 해당 의미에 쓰이지 않는 필드들까지 생성자에서 초기화(**쓰지 않는 필드를 초기화 하는 불필요한 코드가 늘어남**)
- **또 다른 의미를 추가하려면 코드를 수정**해야 한다.
- **인스턴스의 타입만으로는 현재 나타내는 의미를 알 길이 전혀 없다.**
- **태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적이다.**

### 태그 달린 클래스 단점 해결 방법

- 객체지향 언어는 **타입 하나로 다양한 의미의 객체를 표현하는 훨씬 나은 수단 제공**
    - **클래스 계층구조를 활용하는 서브타이핑(subtyping)**
- 태그 달린 클래스는 클래스 계층구조를 어설프게 흉내낸 아류일 뿐이다.

### 태그 달린 클래스→ 클래스 계층구조 변경 방법

```java

//1. 계층 구조의 루트(root)가 될 추상 클래스 정의
abstract class Figure{
	//2. 태그 값에 따라 동작이 달라지는 메서드들을 루트 클래스의 추상 메서드로 선언
	abstract double area();
	//태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가
  //모든 하위 클래스에서 공통으로 사용하는 데이터 필드들도 전부 루트클래스로 올린다.
}

//3. root 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다.
class Cirlcle extends Figure{

	//4. 각 하위 클래스에는 각자의 의미에 해당하는 데이터 필드들을 넣는다.
	final double radius;
	Circle(double radius) {this.radius = radius}

	//5. 루트 클래스가 정의한 추상 메서드를 각자의 의미에 맞게 구현한다.
	@Override double area() {return Math.PI * (radius * radius);}
}

//3. root 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다.
class Rectangle extends Figure{
	final double length;
	final double witdh;
	
	Rectangle(double length, double width){
		this.length = length;
		this.width = width;
	}
	@Override double area() {return width * length;}
}

//추가 시
class Square extends Figure{
	Square(double side){
		super(side, side);
	}
}
```

### 클래스 계층구조 장점

- 간결하고 명확
- 각 의미를 독립된 클래스에 담아 **관련 없던 데이터 필드 모두 제거(쓸데없는 코드 사라짐)**
- 살아 남은 필드들은 모두 final
    - 각 클래스의 생성자가 모든 필드를 남김없이 초기화
    - 추상 메서드를 구현했는지 컴파일러가 확인
- 루트 클래스의 코드를 건드리지 않고 다른 프로그래머들이 **독립적으로 계층구조 확장하고 함께 사용 가능**
- 타입이 의미별로 따로 존재 → 변수의 의미를 명시 or 제한 가능, 특정 의미만 매개변수 받기 가능
- 타입 사이의 자연스러운 계층 관계를 반영 가능해 유연성, 컴파일타임 타입 검사 능력 높여준다.

### 정리

- 태그 달린 클래스를 써야 하는 상황은 거의 없다.
- 새로운 클래스를 작성하는 데 태그 필드 등장 시 → **태그를 없애고 계층구조로 대체하는 방법 생각**
- 기존 클래스가 태그 필드를 사용하고 있다면 **계층구조로 리팩터링 고민**