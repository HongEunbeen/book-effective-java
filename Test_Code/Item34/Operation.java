public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE;

    public double apply(double x, double y) {
        switch (this) {
            case PLUS:
                return x + y;
            case MINUS:
                return x - y;
            case TIMES:
                return x * y;
            case DIVIDE:
                return x / y;
        }
        throw new AssertionError("알 수 없는 연산" + this);
        // 새로 추가한 연산을 수행하려 할 때 런타임 오류 발생
    }

    //switch 문을 이용해 원래 열거 타입에 없는 기능 수행
    //의미상 열거 타입에 속하지 않는다면 직접 만든 열거 타입이라도 이 방식 적용 추천
    public static Operation inverse(Operation op) {
        switch (op) {
            case PLUS:
                return Operation.MINUS;
            case MINUS:
                return Operation.PLUS;
            case TIMES:
                return Operation.DIVIDE;
            case DIVIDE:
                return Operation.TIMES;
        }
        throw new AssertionError("알 수 없는 연산" + this);
    }
}
