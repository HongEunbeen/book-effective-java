public class Main {
    public static void main(String[] args) {
        DefaultInner outer = new DefaultInner();
        DefaultInner.Inner inner = outer.new Inner();

        inner.print();
    }
}
