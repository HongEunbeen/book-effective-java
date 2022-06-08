class Samsung implements PhoneInterface {
    @Override
    public void sendCall() {
        System.out.println("삼성폰 전화 걸기");
    }

    @Override
    public void reciveCall() {
        System.out.println("삼성폰 전화 받기");
    }

}
