public enum Ensemble_new {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5), SECTET(6), SEPTET(7), OCTET(8), NONET(9), DETET(10),
    TRIPLE_QUARTET(12);

    private final int numberOffMusicians;

    Ensemble_new(int size) {
        this.numberOffMusicians = size;
    }

    public int numberOffMusicians(){return numberOffMusicians};
}
