class DefaultInner {
    private String food = "burger";

    class Inner {
        private int price = 200;

        public void print() {
            System.out.println(food + ", price :" + price);
        }
    }
}