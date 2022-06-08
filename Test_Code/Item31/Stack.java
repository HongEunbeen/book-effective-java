public class Stack<E> {
    public Stack();

    public void push(E e);

    public E pop();

    public boolean isEmpty();

    public void pushAll(Iterable<E> src) {
        for (E e : src) {
            push(e);
        }
    }

    public void popAll(Collection<E> dst) {
        while (!isEmpty())
            dst.add(pop());
    }
}
