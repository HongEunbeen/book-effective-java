//연산 타입을 확장해 지수연산과 나머지 연산 추가
public enum ExtendedOperation implements Operation {
    EXP("^") {
    public double apply(double x, double y) {return x ^ y};
    },
    REMAINDER("%") {
    public double apply(double x, double y) {return x % y};
    };

    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        reutnr symbol;
    }
}
