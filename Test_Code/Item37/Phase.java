public enum Phase {
    SOLID, LIQUID, GAS, PLASMA;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID), BOIL(LIQUID, GAS), CONDENES(GAS, LIQUID), SUBLIME(SOLID, GAS),
        DEPOSIT(GAS, SOLID), IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);

        private final Phase from;
        private final Phase to;

        private static final Map<Phase, Map<Phase, Transition>> m 
            = Stream.of(values()).collect(
                groupingBy(t -> t.from, 
                            () -> new EnumMap<>(Phase.class), 
                            toMap(t ->t.to, t -> t, (x,y) -> y. () -> new EnumMap<>(Phase.class))));
                            
        // 한 상태에서 다른 상태로의 전이를 반환
        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}