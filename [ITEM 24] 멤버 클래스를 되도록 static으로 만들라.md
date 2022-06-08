# Item 24 - 멤버 클래스를 되도록 static으로 만들라

### 중첩 클래스

```java
//Outer.java
class Outer {
    private String food = "burger";

    class Inner {
				//outer클래스 멤버를 inner에서 사용 가능(private도 가능)
				//정적 멤버 클래스를 제외하곤 inner안에 static변수 선언 x
        private int price = 200;

        public void print() {
            System.out.println(food + ", price :" + price);
        }
    }
}
```

- **다른 클래스 안에 정의된 클래스**를 말한다
- 자신을 **감싼 바깥 클래스에서만 쓰여야 하며 그외의 쓰임새로 사용하려면 톱레벨 클래스로 만들어야 한다.**
    - 단, static Inner 클래스는 바로 접근 가능
- 종류
    - 정적 멤버 클래스
    - (비정적) 멤버 클래스
    - 익명 클래스
    - 지역 클래스

     → 정적 멤버 클래스를 제외하고 **나머지는 내부 클래스(inner class)에 해당**

### 정적 멤버 클래스

```java
//Outer.java
public class Outer {
	int a = 10;
  private int b = 20;
  static int c = 30;

    //내용
    static class inner{
        static int d = 40;  // static 변수 선언 가능
		    public void print(){
					// Outer 클래스의 static 변수만 접근 가능하고 a, b 변수는 접근 불가
		      System.out.println(c + b); 
		    }
				// static 메소드 선언
		    public static void staticPrint(){
		      System.out.println(c + b);
		    }
    }
}
```

- **다른 클래스 안에 선언**, **바깥 클래스의 private 멤버 접근 가능** 점 **제외**하고 일반 클래스와 똑같다.
- **정적 멤버 클래스는 다른 정적 멤버와 똑같은 접근 규칙을 적용**받는다.
- 바깥 클래스와 함께 쓰일 때만 유용한 **public 도우미 클래스**로 쓰인다.
    - Operation 열거 타입은 Calculator 클래스의 public 정적 멤버 클래스
        - Calculator의 클라이언트에서 Calculator.Operation.PLUS, Calculator.Operation.MINUES 같은 형태로 원하는 연산 참조 가능

### 비정적 멤버 클래스

```java
public class Outer {
  int a = 10;
  private int b = 20;
  static int c = 30;

  public void outerMethod() {

    // Inner class
    class Inner {
      public void print() {
        System.out.println(a + " " + b + " " + c);
      }
    }

    Inner i = new Inner();
    i.print();
  }
}
```

- 어댑터를 정의할 때 자주 사용 된다.
    - **어떤 클래스의 인스턴스를 감싸 마치 다른 클래스의 인스턴스처럼 보이게 하는 뷰**로 사용하는 것
- Map 인터페이스의 구현제들은 **보통 자신의 컬렉션 뷰를 구현할 때 비정적 멤버 클래스를 사용**한다.
- 비슷하게, Set과 List 같은 다른 컬렉션 인터페이스 구현들도 자신의 반복자를 구현할 때 비정적 멤버 클래스를 주로 사용한다.

    ```java
    public class MySet<E> extends AbstractSet<E> {
    	...//생략
    	@Overrind public Iterator<E> interator(){
    		return new MyIterator();
    	}

    	public class MyIterator implements Iterator<E>{...}
    }
    ```

### **정적 멤버 클래스 vs 비 정적 멤버 클래스 (구문상의 차이 static)**

- **비정적 멤버 클래스**
    - 비정적 멤버 클래스의 인스턴스 → 바깥 클래스의 인스턴스와 암묵적으로 연결
        - **this를 사용해 바깥 인스턴스의 메서드를 호출**하거나 **바깥 인스턴스의 참조를 가져**올 수 있다.
    - 멤버 클래스가 인스턴스화될 때 확립되며, 더 이상 변경 불가
    - 바깥 클래스의 인스턴스 메서드 → **비정적 멤버 클래스의 생성자 호출** (자동으로 생성)
        - 바깥인스턴스의클래스.new MemberClass(args) 도 가능
    - 바깥클래스의 인스턴스와 비정적 멤버 클래스의 인스턴스의 관계 정보는 비정적 멤버 클래스의 인스턴스 안에 만들어져 **메모리 공간을 차지하며, 생성 시간도 더 걸린다.**
- **정적 멤버 클래스**
    - 중첩 클래스의 인스턴스가 **바깥 인스턴스와 독립적으로 존재시** 정적 멤버 클래스
- **멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙여서 정적 멤버 클래스로 만들자**
    - static을 생략하면 바깥 인스턴스로의 숨은 외부 참조 갖게 됨(**참조 저장 → 시간과 공간 소비**)
    - **가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못하면 메모리 누수 발생**
    - 참조가 눈에 보이지 않으니 **문제의 원인을 찾기 어려워 때때로 심각한 상황 초래**

### private 정적 멤버 클래스

- 바깥 클래스가 표현하는 **객체의 한 부분(구성요소)**을 나타낼 때 쓴다.
- 키와 값을 매핑시키는 Map 인스턴스 사용 예시
    - 모든 Entry객체(키-값 쌍을 표현)가 맵을 직접 사용 x 
    → **Entry를 비정적 멤버 클래스로 표현하는 것 낭비(**private 정적 멤버 클래스가 가장 알맞다.**)**
    - 실수로 static을 빠뜨려도 동작 → **엔트리가 바깥 맵으로의 참조를 갖게 되어공간과 시간을 낭비**

    ```java
    static class Node<K, Y> implements Map.Entry<K,V>{

    	final int hash;
    	final K key;
    	V value;
    	Node<K, V> next;
    	
    	//생성자
    	Node(int hash, K key, V value, Node<K, v> {
    		this.hash = hash;
    		this.key = key;
    		this.value = value;
    		this.next = next;
    	}
    	
    	//getter 메소드
    	public final K getKey() {return key;}
    	public final V getValue() {return value;}
    	public final String toString() {return key + "=" + value;}

    	public final int hashCode() {return Objects.hashCode(key) ^ Objects.hashCode(value);}
    	
    	//setter 메소드
    	public final V setValue(V newValue){
    		V oldValue = value;
    		value = newValue;
    		return oldValue;
    	}

    	//모든 엔트리가 맵과 연관되어있지만
    	//엔트리의 메서드들은(getKey, getValue, setValue)은 맵을 직접적으로 사용하진 않는다.

    	public final boolean equals(Object o){
    		if(o == this) return true;
    		if(o instanceof Map.Entry){
    			Map.Entry<?, ?> e = (Map.Entry<?,?>)o;
    			if(Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue()){
    				return true;		
    			}
    		return false;
    	}
    }
    ```

### 공개된 클래스의 멤버일 경우

- 공개된 클래스의 public / protected **멤버라면 정적이냐 아니냐는 두 배로 중요해진다.**
- 멤버 클래스 역시 공개 API가 되니, 혹시라도 향후 릴리스에서 static을 붙이면 하위 호환성이 깨진다.

### 익명 클래스

```java
//Student.java
interface Student {
  public void getInfo();
}

//Test.java
public class Test {
  public static void main(String[] args) {

    // anonymous 클래스
		// 코드의 어디서든 만들 수 있다.
    Student student = new Student() {
			//멤버와 달리 쓰이는 시점에 선언과 동시에 인스턴스가 생성
      public void getInfo() {
        System.out.println("anonymous class");
      }
    };
    student.getInfo();
  }
}

// 비정적인 문맥에서 사용될 때만 바깥 클래스의 인스턴스를 참조할 수 있다.
// 정적 문맥에서라도 상수 변수 이외의 정적 멤버는 가질 수 없다.
// 상수 표현을 위해 초기화된 final 기본 타입과 문자열 필드만 가질 수 있다.
```

- 익명 메소드 단점
    - **선언한 지점에서만 인스턴스 생성 가능**
    - instanceof 검사나 클래스의 이름이 필요한 작업은 수행할 수 없다.
    - 여러 인터페이스를 구현할 수 없다.
    - 인터페이스를 구현하는 동시에 다른 클래스를 상속할 수 없다
    - 익명 클래스를 사용하는 클라이언트는 그 익명 클래스가 상위 타입에서 상속한 멤버 외에는 호출할 수 없다.
- 익**명 클래스보다는 람다를 사용하라(Item 42)**
- 익명 클래스의 **또 다른 주 쓰임은 정적 팩터리 메서드를 구현할** 때

### 지역 클래스

- 네 가지 중첩 클래스 중 **가장 드물게 사용**된다.
- 지역 클래스는 지역변수와 같게 사용한다.
- 익명 클래스처럼 **비정적 문맥에서 사용될 때만 바깥 인스턴스를 참조 가능**하다
- 가독성을 위해 짧게 작성해야 한다.

### 정리

- **중첩 클래스에는 네 가지가 존재 → 각각의 쓰임이 다르다**
- 멤버 클래스
    - 메서드 밖에서도 사용하거나 메서드 안에 정의하기엔 너무 길다면 멤버 클래스로 생성
    - **멤버 클래스의 인스턴스 각각이 바깥 인스턴스를 참조하지 않는다면 정적 멤버 클래스로 선언**
- 익명 클래스
    - 중첩 클래스가 한 메서드 안에서만 쓰임
    - 그 인스턴스를 생성하는 지점이 단 한곳