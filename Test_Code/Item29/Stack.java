import java.util.Arrays;

public class Stack {
    // private Object[] elements;
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        // elements = new Object[DEFAULT_INITIAL_CAPACITY];
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    // public void push(Object e) {
    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    // public Object pop() {
    public E pop() {
        if (size == 1)
            throw new EmptyStackException();
        // Object result = elements[--size];
        E result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
