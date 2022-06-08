public <T> T[] toArray(T[] a) {
    if(a.length < size){
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Arrays.copyOf(elements, size, a.getClass());
        return result;
    }

    System.arraycopy(elemtns, 0, a, 0, size);
    if(a.length > size)
        a[size] = null;
    return a;
}
