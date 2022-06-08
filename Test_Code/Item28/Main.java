public class Main {
    public static void main(String args[]) {
        // Object[] objectArray = new Long[1];
        // objectArrauy[0] = "타입이 달라 넣을 수 없다."; // ArrayStoreException을 던진다.

        // List<Object> ol = new ArrayList<Long>();// 호환되진 않는 타입이다.
        // ol.add("타입이 달라 넣을 수 없다.");

        List<String>[] stringLists = new List<string>[1];
        //제네릭 배열 생성 허용 가정
        List<Integer> intList = List.of(42);
        //원소가 하나인 List<Integer>를 생성한다.
        Object[] objects = stringLists;
        //List<String>의 배열을 Object 배열에 할당한다.
        //배열은 공변이니 아무 문제없다.
        objects[0] = intList;
        //List<Integer>의 인스턴스를 Ojbect 배열의 첫 원소로 저장
        //제네릭은 소거 방식으로 구현되어 성공한다.
        //런타임에는 
            //List<Integer> 인스턴스의 타입 :  List
            //List<Integer>[] 인스턴스의 타입 : List[]
        //ArrayStoreException 발생 안함
        String s = stringLists[0].get(0);
        //런타임에 ClassCastException 발생
            // 배열의 터음 리스트에서 원소 꺼낸다 > 컴파일러는 꺼낸 원소를 자동으로 String으로 형변환한ㄷ. > 원소는 Integer 타입으로 런타임에 오류
        //문제! > List<String> 인스턴스만 담겠다고 선언한 stringLists배열 > List<Integer> 인스턴스가 저장되어 있다!!!


        return 0;
    }
}