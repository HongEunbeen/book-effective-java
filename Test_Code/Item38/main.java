import java.util.Arrays;

public class main {
    public static main(String args[]) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);

        test(ExtendedOperation.class, x, y);
        test(Arrays.asList(ExtendedOperation.values()), x, y);
    }

    private static <T extends Enum<T> & Operation> void test(Class<T> onEnumType, double x, double y) {
        for (Operation op : onEnumType.getEnumConstants()) {
            System.out.println("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }

    private static void test(Collection<? extends Operation> opSet, double x, double y) {
        for (Operation op : opSet) {
            System.out.println("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }
}
