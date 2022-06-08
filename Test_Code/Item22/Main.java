public class Main {
    public static void main(String args[]) {
        PhoneInterface phone = new Samsung();
        phone.sendCall();
        phone.reciveCall();
    }
}
