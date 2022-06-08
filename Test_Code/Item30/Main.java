import java.util.Set;

public class Main {
    private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    public static void main(String[] args) {
        Set<String> guys = Set.of("톰", "딕", "해리");
        Set<String> stooges = Set.of("래리", "모에", "컬리");
        Set<String> aflCio = union(guys, stooges);
        System.out.println(aflCio);

        //제네릭 싱글턴을 사용하는 예
        String[] strings = {"삼배", "대마", "나일론"};
        UnaryOperator<String> sameString =identityFunction();
        for(String s : strings)
            System.out.println(sameString.apply(s));
        
        Number[] numbers = {1, 2.0, 3L};
        UnaryOperator<Number> sameNumber = identityFunction();
        //컴파일 오류나 경고가 발생하지 않는다.
        for(Number n : numbers){
            System.out.println(sameNumber.apply(s));
        }
    }

    public static Set<E> union(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction(){
        return (UnaryOperator<T>) IDENTITY_FN;
    }

    public static <E extends Comparable<E>> E max(Collections<E> c){
        if(c.isEmpty()){
            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
        }
        E result = null;
        for(E e : c) {
            if(result == null || e.compareTo(result) > 0){
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }


}
